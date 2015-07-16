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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.WriteFailedException;
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
    
    private boolean isDone;
    
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
        isDone = false;
        GuiUtil.safelyRunBlockingRoutine(new Runnable() {
            @Override
            public void run() {
                if (getBaseFile() == null || getBaseFile().length() == 0) {
                    new MergeWorker(getFile1(), getFile2()).run();
                } else {
                    new MergeWorker(getBaseFile(), getFile1(), getFile2()).run();
                }
                try {
                    synchronized (MergeIOController.this) {
                        while (!isDone) {
                            MergeIOController.this.wait();
                        }
                    }
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    private class MergeWorker extends FileLoaderWorker {

        public MergeWorker(File... files) {
            super(files);
        }
        
        @Override
        protected void processLoadedTmxs(List<ITmx> tmxs) {
            try {
                MergeController merger = new MergeController();
                if (getOutputFile() != null) {
                    merger.setQuiet(true);
                }

                ITmx merged;
                if (tmxs.size() == 2) {
                    merged = merger.merge(tmxs.get(0), tmxs.get(1));
                } else {
                    merged = merger.merge(tmxs.get(0), tmxs.get(1), tmxs.get(2));
                }
                writeOutputFile(merged);
            } catch (WriteFailedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } finally {
                synchronized (MergeIOController.this) {
                    isDone = true;
                    MergeIOController.this.notify();
                }
            }
        }
    }

    private void writeOutputFile(ITmx merged) throws WriteFailedException {
        if (merged == null) {
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
                    LocString.get("STM_CONFIRM_CANCEL_SAVE_MESSAGE"),
                    LocString.get("STM_MERGE_WINDOW_TITLE"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }
        merged.writeTo(getOutputFile());
    }
}
