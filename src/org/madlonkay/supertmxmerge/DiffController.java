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

import java.beans.*;
import java.io.Serializable;
import java.util.List;
import javax.swing.JOptionPane;
import org.madlonkay.supertmxmerge.data.DiffInfo;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.gui.DiffWindow;
import org.madlonkay.supertmxmerge.util.DiffUtil;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class DiffController implements Serializable {
    
    public static final String PROP_TMX1 = "tmx1";
    public static final String PROP_TMX2 = "tmx2";
    public static final String PROP_CHANGECOUNT = "changeCount";

    private ITmx tmx1;
    private ITmx tmx2;
    
    private List<DiffInfo> diffInfos;
    
    protected PropertyChangeSupport propertySupport;
    
    public DiffController() {
        propertySupport = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public ITmx getTmx1() {
        return tmx1;
    }
    
    public void setTmx1(ITmx tmx1) {
        ITmx oldTmx1 = this.tmx1;
        this.tmx1 = tmx1;
        propertySupport.firePropertyChange(PROP_TMX1, oldTmx1, tmx1);
    }
    
    public ITmx getTmx2() {
        return tmx2;
    }
    
    public void setTmx2(ITmx tmx2) {
        ITmx oldTmx2 = this.tmx2;
        this.tmx2 = tmx2;
        propertySupport.firePropertyChange(PROP_TMX2, oldTmx2, tmx2);
    }
    
    public void diff(ITmx tmx1, ITmx tmx2) {
        
        setTmx1(tmx1);
        setTmx2(tmx2);
        
        diffInfos = DiffUtil.generateDiffData(tmx1, tmx2);
        propertySupport.firePropertyChange(PROP_CHANGECOUNT, null, null);
        
        if (diffInfos.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                        LocString.get("identical_files_message"),
                        LocString.get("diff_window_title"),
                        JOptionPane.INFORMATION_MESSAGE);
        } else {
            DiffWindow window = new DiffWindow(this);
            GuiUtil.displayWindow(window);
            GuiUtil.blockOnWindow(window);
        }
        System.out.println("Exited DiffController#diff");
    }
    
    public List<DiffInfo> getDiffInfos() {
        return diffInfos;
    }

    public int getChangeCount() {
        return diffInfos.size();
    }
}
