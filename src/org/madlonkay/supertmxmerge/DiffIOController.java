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
import javax.swing.ProgressMonitor;
import javax.xml.bind.UnmarshalException;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.JAXB.JAXBTmx;
import org.madlonkay.supertmxmerge.util.FileUtil;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class DiffIOController {
    
    public static final String PROP_FILE1 = "file1";
    public static final String PROP_FILE2 = "file2";
    
    public static final String PROP_INPUTISVALID = "inputIsValid";
    
    private File file1;
    private File file2;
    
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
    
    public boolean getInputIsValid() {
        return FileUtil.validateFile(getFile1()) 
                && FileUtil.validateFile(getFile2()) && !getFile1().equals(getFile2());
    }
    
    public void go() {
        
        DiffController differ = new DiffController();
        
        ProgressMonitor progress = new ProgressMonitor(null, "Loading files...", "", 0, 2);
        
        ITmx tmx1;
        ITmx tmx2;
        try {
            progress.setNote(getFile1().getName());
            tmx1 = new JAXBTmx(getFile1());
            progress.setProgress(1);
            progress.setNote(getFile2().getName());
            tmx2 = new JAXBTmx(getFile2());
            progress.setProgress(2);
        } catch (UnmarshalException ex) {
            throw new RuntimeException(ex);
        }
        progress.close();
        
        differ.diff(tmx1, tmx2);
    }
}
