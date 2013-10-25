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
import javax.swing.ProgressMonitor;
import javax.xml.bind.UnmarshalException;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.JAXB.JAXBTmx;
import org.madlonkay.supertmxmerge.gui.ProgressWindow;
import org.madlonkay.supertmxmerge.util.FileUtil;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class MergeIOController extends DiffIOController {
    public static final String PROP_MERGEBASEFILE = "mergeBaseFile";
    public static final String PROP_OUTPUTFILE = "outputFile";
    
    private File baseFile;
    private File outputFile;
    
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
    
    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        File oldOutputFile = this.outputFile;
        this.outputFile = outputFile;
        propertySupport.firePropertyChange(PROP_OUTPUTFILE, oldOutputFile, outputFile);
    }
    
    @Override
    public boolean getInputIsValid() {
        return FileUtil.validateFile(getBaseFile()) && FileUtil.validateFile(getFile1())
                && FileUtil.validateFile(getFile2())
                && !getBaseFile().equals(getFile1()) && !getBaseFile().equals(getFile2())
                && !getFile1().equals(getFile2());
    }
    
    @Override
    public void go() {
        
        MergeController merger = new MergeController();
        
        if (outputFile != null) {
            merger.setQuiet(true);
        }
        
        ProgressWindow progress = new ProgressWindow();
        progress.setMaximum(3);
        
        ITmx baseTmx;
        ITmx leftTmx;
        ITmx rightTmx;
        try {
            progress.setMessage(LocString.getFormat("file_progress", getBaseFile().getName(), 1, 3));
            baseTmx = new JAXBTmx(getBaseFile());
            progress.setValue(1);
            progress.setMessage(LocString.getFormat("file_progress", getFile1().getName(), 2, 3));
            leftTmx = new JAXBTmx(getFile1());
            progress.setValue(2);
            progress.setMessage(LocString.getFormat("file_progress", getFile2().getName(), 3, 3));
            rightTmx = new JAXBTmx(getFile2());
            progress.setValue(3);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                ex.toString(),
                LocString.get("error"),
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        }
        
        GuiUtil.closeWindow(progress);
        
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
                    LocString.get("confirm_cancel_save_message"),
                    LocString.get("merge_window_title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }

        try {
            merged.writeTo(getOutputFile());
        } catch (Exception ex) {
            Logger.getLogger(MergeIOController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
