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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import org.madlonkay.supertmxmerge.data.ConflictInfo;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.Key;
import org.madlonkay.supertmxmerge.data.ResolutionSet;
import org.madlonkay.supertmxmerge.gui.MergeWindow;
import org.madlonkay.supertmxmerge.util.DiffUtil;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class MergeController implements Serializable, ActionListener {
    public static final Logger LOGGER = Logger.getLogger(MergeController.class.getName());
    
    public static final String PROP_BASETMX = "baseTmx";
    public static final String PROP_LEFTTMX = "leftTmx";
    public static final String PROP_RIGHTTMX = "rightTmx";
    public static final String PROP_CONFLICTCOUNT = "conflictCount";
    
    public static final String PROP_CONFLICTSARERESOLVED = "conflictsAreResolved";

    private final PropertyChangeSupport propertySupport;
    
    private ITmx baseTmx;
    private ITmx leftTmx;
    private ITmx rightTmx;
        
    private final Map<Key, AbstractButton[]> selections = new HashMap<>();
    
    private ResolutionSet resolution;
    
    private boolean canCancel = true;
    private boolean quiet = false;
    
    public MergeController() {
        propertySupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public ITmx merge(ITmx baseTmx, ITmx leftTmx, ITmx rightTmx) {
        
        setBaseTmx(baseTmx);
        setLeftTmx(leftTmx);
        setRightTmx(rightTmx);
        
        resolution = DiffUtil.generateMergeData(baseTmx, leftTmx, rightTmx);
        propertySupport.firePropertyChange(PROP_CONFLICTCOUNT, null, null);
        
        boolean showDiff = false;
        
        if (!resolution.conflicts.isEmpty()) {
            // Have conflicts; show window.
            MergeWindow window = new MergeWindow(this);
            GuiUtil.displayWindow(window);
            GuiUtil.blockOnWindow(window);
        } else if (!quiet) {
            // Files merged with no conflicts.
            showDiff = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                    LocString.get("no_conflicts_message"),
                    LocString.get("merge_window_title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
        }
        
        ITmx resolved = resolve();
        
        if (showDiff) {
            DiffController differ = new DiffController();
            differ.diff(baseTmx, resolved);
        }
        
        return resolved;
    }
    
    public boolean isConflictsAreResolved() {
        if (resolution != null && resolution.conflicts.isEmpty()) {
            return true;
        }
        if (selections.isEmpty()) {
            return false;
        }
        for (Entry<Key, AbstractButton[]> e : selections.entrySet()) {
            if (!selectionMade(e.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    private boolean selectionMade(AbstractButton[] buttons) {
        for (AbstractButton b : buttons) {
            if (b.isSelected()) {
                return true;
            }
        }
        return false;
    }
    
    public void addSelection(Key key, AbstractButton[] buttons) {
        selections.put(key, buttons);
    }
    
    public List<ConflictInfo> getConflicts() {
        return resolution.conflicts;
    }

    public ITmx getBaseTmx() {
        return baseTmx;
    }

    public void setBaseTmx(ITmx baseTmx) {
        ITmx oldBaseTmx = this.baseTmx;
        this.baseTmx = baseTmx;
        propertySupport.firePropertyChange(PROP_BASETMX, oldBaseTmx, baseTmx);
    }

    public ITmx getLeftTmx() {
        return leftTmx;
    }

    public void setLeftTmx(ITmx leftTmx) {
        ITmx oldLeftTmx = this.leftTmx;
        this.leftTmx = leftTmx;
        propertySupport.firePropertyChange(PROP_LEFTTMX, oldLeftTmx, leftTmx);
    }

    public ITmx getRightTmx() {
        return rightTmx;
    }

    public void setRightTmx(ITmx rightTmx) {
        ITmx oldRightTmx = this.rightTmx;
        this.rightTmx = rightTmx;
        propertySupport.firePropertyChange(PROP_RIGHTTMX, oldRightTmx, rightTmx);
    }

    public int getConflictCount() {
        return resolution.conflicts.size();
    }
    
    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }
    
    public boolean isCanCancel() {
        return canCancel;
    }
    
    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }
    
    public boolean isQuiet() {
        return quiet;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        propertySupport.firePropertyChange(PROP_CONFLICTSARERESOLVED, null, null);
    }
    
    private ITmx resolve() {
        if (!isConflictsAreResolved()) {
            return null;
        }
        
        for (Entry<Key, AbstractButton[]> e : selections.entrySet()) {
            Key key = e.getKey();
            switch (getSelection(e.getValue())) {
                case 0:
                    dispatchKey(key, baseTmx, leftTmx, resolution);
                    break;
                case 1:
                    // No change
                    break;
                case 2:
                    dispatchKey(key, baseTmx, rightTmx, resolution);
                    break;
            }
        }
        
        return baseTmx.applyChanges(resolution);
    }
    
    private static void dispatchKey(Key key, ITmx baseTmx, ITmx thisTmx, ResolutionSet resolution) {
        if (!thisTmx.getTuvMap().containsKey(key)) {
            resolution.toDelete.add(key);
        } else if (baseTmx.getTuvMap().containsKey(key)) {
            resolution.toReplace.put(key, thisTmx.getTuvMap().get(key));
        } else {
            resolution.toAdd.add(thisTmx.getTuMap().get(key));
        }
    }
    
    private static int getSelection(AbstractButton[] buttons) {
        int n = 0;
        for (AbstractButton b : buttons) {
            if (b.isSelected()) {
                return n;
            }
            n++;
        }
        return -1;
    }
}
