/*
 * Copyright (C) 2013 Aaron Madlon-Kay <aaron@madlon-kay.com>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.madlonkay.supertmxmerge;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.JAXB.JAXBTmx;
import org.madlonkay.supertmxmerge.data.WriteFailedException;
import org.madlonkay.supertmxmerge.gui.ProgressWindow;
import org.madlonkay.supertmxmerge.util.FileUtil;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class MergeIOController extends DiffIOController {
    
    private static final Logger LOGGER = Logger.getLogger(MergeIOController.class.getName());
    
    public static final String PROP_MERGEBASEFILE = "mergeBaseFile";
    
    private File baseFile;
    
    public MergeIOController() {
        super();
    }
    
    public File getBaseFile() {
        return baseFile;
    }

    public void setBaseFile(File baseFile) {
        File oldBaseFile = this.baseFile;
        this.baseFile = baseFile;
        propertySupport.firePropertyChange(PROP_MERGEBASEFILE, oldBaseFile, baseFile);
        propertySupport.firePropertyChange(PROP_INPUTISVALID, null, null);
    }
    
    @Override
    public boolean getInputIsValid() {
        return FileUtil.validateFile(getFile1()) && FileUtil.validateFile(getFile2())
                && (getBaseFile() == null || !getBaseFile().equals(getFile1()))
                && (getBaseFile() == null || !getBaseFile().equals(getFile2()))
                && !getFile1().equals(getFile2());
    }
    
    @Override
    public void go() {
        
        MergeController merger = new MergeController();
        
        if (getOutputFile() != null) {
            merger.setQuiet(true);
        }
        
        ProgressWindow progress = new ProgressWindow();
        progress.setMaximum(3);
        
        ITmx baseTmx;
        ITmx leftTmx;
        ITmx rightTmx;
        try {
            progress.setValue(0);
            progress.setMessage(LocString.getFormat("FILE_PROGRESS", getFile1().getName(), 1, 3));
            leftTmx = new JAXBTmx(getFile1());
            progress.setValue(1);
            progress.setMessage(LocString.getFormat("FILE_PROGRESS", getFile2().getName(), 2, 3));
            rightTmx = new JAXBTmx(getFile2());
            progress.setValue(2);
            progress.setMessage(LocString.getFormat("FILE_PROGRESS",
                    getBaseFile() == null ? LocString.get("EMPTY_TMX_NAME") : getBaseFile().getName(), 3, 3));
            if (getBaseFile() == null) {
                baseTmx = JAXBTmx.newEmptyJAXBTmx((JAXBTmx) leftTmx);
                merger.setIsTwoWayMerge(true);
            } else {
                baseTmx = new JAXBTmx(getBaseFile());
            }
            progress.setValue(3);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                ex.toString(),
                LocString.get("ERROR_DIALOG_TITLE"),
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        } finally {
            GuiUtil.closeWindow(progress);
        }
        
        ITmx merged = merger.merge(baseTmx, leftTmx, rightTmx);

        if (merged == null) {
            // User canceled out.
            return;
        }
        while (true) {
            if (getOutputFile() != null) {
                break;
            }
            // Output location not set.
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                setOutputFile(chooser.getSelectedFile());
            } else {
                int response = JOptionPane.showConfirmDialog(null,
                    LocString.get("CONFIRM_CANCEL_SAVE_MESSAGE"),
                    LocString.get("MERGE_WINDOW_TITLE"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }

        try {
            merged.writeTo(getOutputFile());
        } catch (WriteFailedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
