/*
 * Copyright (C) 2013 Aaron Madlon-Kay <aaron@madlon-kay.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.madlonkay.supertmxmerge;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
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
        
        ITmx tmx1;
        ITmx tmx2;
        try {
            tmx1 = new JAXBTmx(getFile1());
            tmx2 = new JAXBTmx(getFile2());
        } catch (UnmarshalException ex) {
            throw new RuntimeException(ex);
        } 
        
        differ.diff(tmx1, tmx2);
    }
}
