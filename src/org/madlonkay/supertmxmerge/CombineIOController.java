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
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class CombineIOController {
    
    private final PropertyChangeSupport propertySupport;
    
    public static final String PROP_INPUTISVALID = "inputIsValid";
    
    private List<File> files = new ArrayList<File>();
    
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
    
    public boolean getInputIsValid() {
        return files.size() >= 2;
    }
    
    public void go() {
        
    }
}
