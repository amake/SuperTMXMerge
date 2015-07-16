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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.JAXB.JAXBTmx;
import org.madlonkay.supertmxmerge.data.WriteFailedException;
import org.madlonkay.supertmxmerge.gui.ProgressWindow;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class CombineIOController {
    
    private static final Logger LOGGER = Logger.getLogger(CombineIOController.class.getName());
    
    private final PropertyChangeSupport propertySupport;
    
    public static final String PROP_INPUTISVALID = "inputIsValid";
    public static final String PROP_OUTPUTFILE = "outputFile";
    
    private List<File> files = new ArrayList<File>();
    private File outputFile;
    
    private boolean isDone;
    
    public CombineIOController() {
        propertySupport = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public List<File> getFiles() {
        return files;
    }
    
    public void addFile(File file) {
        if (!files.contains(file)) {
            files.add(file);
            propertySupport.firePropertyChange(PROP_INPUTISVALID, null, null);
        }
    }
    
    public void setFiles(Enumeration<File> files) {
        this.files = Collections.list(files);
        propertySupport.firePropertyChange(PROP_INPUTISVALID, null, null);
    }
    
    public void setFiles(File[] files) {
        this.files = Arrays.asList(files);
        propertySupport.firePropertyChange(PROP_INPUTISVALID, null, null);
    }
    
    public boolean getInputIsValid() {
        return files.size() >= 2;
    }
    
    public File getOutputFile() {
        return this.outputFile;
    }
    
    public void setOutputFile(File outputFile) {
        File oldOutputFile = this.outputFile;
        this.outputFile = outputFile;
        propertySupport.firePropertyChange(PROP_OUTPUTFILE, oldOutputFile, outputFile);
    }
    
    public void go() {
        isDone = false;
        GuiUtil.safelyRunBlockingRoutine(new Runnable() {
            @Override
            public void run() {
                new CombineWorker(getFiles()).run();
                try {
                    synchronized (CombineIOController.this) {
                        while (!isDone) {
                            CombineIOController.this.wait();
                        }
                    }
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    private class CombineWorker extends SwingWorker<ITmx, Object[]> {

        private final ProgressWindow progress;
        private final List<File> files;
        
        public CombineWorker(List<File> files) {
            progress = new ProgressWindow();
            progress.setMustPopup(true);
            progress.setMaximum(files.size());
            this.files = files;
        }

        @Override
        protected ITmx doInBackground() throws Exception {
            
            JAXBTmx combined;
            File firstFile = files.get(0);
            publish(new Object[] { 0, LocString.getFormat("STM_FILE_PROGRESS", firstFile.getName(), 1, files.size()) });
            combined = new JAXBTmx(firstFile);
            ITmx empty = JAXBTmx.newEmptyJAXBTmx(combined);

            MergeController merger = new MergeController();
            merger.setIsTwoWayMerge(true);
            
            for (int i = 1; i < files.size(); i++) {
                File file = files.get(i);
                publish(new Object[] { i, LocString.getFormat("STM_FILE_PROGRESS", file.getName(), i + 1, files.size()) });
                JAXBTmx next = new JAXBTmx(file);
                combined = (JAXBTmx) merger.merge(empty, combined, next);
                if (combined == null) {
                    // User canceled out.
                    return null;
                }
            }

            publish(new Object[] { files.size() });
            
            return combined;
        }
        
        @Override
        protected void process(List<Object[]> chunks) {
            Object[] last = chunks.get(chunks.size() - 1);
            progress.setValue((Integer) last[0]);
            if (last.length > 1) {
                progress.setMessage((String) last[1]);
            }
        }

        @Override
        protected void done() {
            ITmx result;
            try {
                result = get();
            } catch (Exception ex) {
                throw new RuntimeException(LocString.get("STM_LOAD_ERROR"), ex);
            } finally {
                GuiUtil.closeWindow(progress);
            }
            
            try {
                writeOutputFile(result);
            } catch (WriteFailedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } finally {
                synchronized (CombineIOController.this) {
                    isDone = true;
                    CombineIOController.this.notify();
                }
            }
        }
    }

    private void writeOutputFile(ITmx combined) throws WriteFailedException {
        if (combined == null) {
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
                    LocString.get("STM_COMBINE_WINDOW_TITLE"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }
        combined.writeTo(getOutputFile());
    }
}
