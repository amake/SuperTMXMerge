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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.JAXB.JAXBTmx;
import org.madlonkay.supertmxmerge.data.WriteFailedException;
import org.madlonkay.supertmxmerge.util.FileUtil;
import org.madlonkay.supertmxmerge.util.GuiUtil;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class DiffIOController {
    
    private static final Logger LOGGER = Logger.getLogger(DiffIOController.class.getName());
    
    public static final String PROP_FILE1 = "file1";
    public static final String PROP_FILE2 = "file2";
    
    public static final String PROP_INPUTISVALID = "inputIsValid";
    public static final String PROP_OUTPUTFILE = "outputFile";

    
    private File file1;
    private File file2;
    
    private File outputFile;
    
    private boolean isDone;
    
    protected PropertyChangeSupport propertySupport;
    
    public DiffIOController() {
        propertySupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public File getFile1() {
        return file1;
    }

    public void setFile1(File file1) {
        File oldFile1 = this.file1;
        this.file1 = file1;
        propertySupport.firePropertyChange(PROP_FILE1, oldFile1, file1);
        propertySupport.firePropertyChange(PROP_INPUTISVALID, null, null);
    }
    
    public File getFile2() {
        return file2;
    }

    public void setFile2(File file2) {
        File oldFile2 = this.file2;
        this.file2 = file2;
        propertySupport.firePropertyChange(PROP_FILE2, oldFile2, file2);
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
    
    public boolean getInputIsValid() {
        return FileUtil.validateFile(getFile1()) 
                && FileUtil.validateFile(getFile2()) && !getFile1().equals(getFile2());
    }
    
    public void go() {
        isDone = false;
        GuiUtil.safelyRunBlockingRoutine(new Runnable() {
            @Override
            public void run() {
                new DiffWorker(getFile1(), getFile2()).run();
                try {
                    synchronized (DiffIOController.this) {
                        while (!isDone) {
                            DiffIOController.this.wait();
                        }
                    }
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    private class DiffWorker extends FileLoaderWorker {

        public DiffWorker(File... files) {
            super(files);
        }

        @Override
        protected void processLoadedTmxs(List<ITmx> tmxs) {
            try {
                DiffController differ = new DiffController();
                if (outputFile != null) {
                    ITmx outTmx = JAXBTmx.createFromDiff((JAXBTmx) tmxs.get(0), (JAXBTmx) tmxs.get(1));
                    outTmx.writeTo(outputFile);
                } else {
                    differ.diff(tmxs.get(0), tmxs.get(1));
                }
            } catch (WriteFailedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } finally {
                isDone = true;
                synchronized (DiffIOController.this) {
                    DiffIOController.this.notify();
                }
            }
        }
    }
}
