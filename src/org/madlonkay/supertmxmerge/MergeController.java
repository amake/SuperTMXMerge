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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.ITuv;
import org.madlonkay.supertmxmerge.data.Key;
import org.madlonkay.supertmxmerge.data.MergeAnalysis;
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
    
    public enum BatchResolution {
        BASE, LEFT, RIGHT
    }
    
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
        
    private final Map<Key, AbstractButton[]> selections = new HashMap<Key, AbstractButton[]>();
    
    private MergeAnalysis<Key,ITuv> analysis;
    
    private boolean canCancel = true;
    private boolean quiet = false;
    private boolean isTwoWayMerge = false;
    private boolean isModal = false;
    private int listViewThreshold = 5;
    private Window parentWindow = null;
    
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

        analyze(baseTmx, leftTmx, rightTmx);
        
        boolean showDiff = false;
        
        if (analysis.hasConflicts()) {
            // Have conflicts; show window.
            Window window;
            if (isModal) {
                window = MergeWindow.newAsDialog(this, isTwoWayMerge, parentWindow);
            } else {
                window = MergeWindow.newAsFrame(this, isTwoWayMerge);
            }
            GuiUtil.displayWindow(window);
            GuiUtil.blockOnWindow(window);
        } else if (!quiet && !isTwoWayMerge) {
            // Files merged with no conflicts.
            showDiff = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                    LocString.get("STM_NO_CONFLICTS_MESSAGE"),
                    LocString.get("STM_MERGE_WINDOW_TITLE"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
        }
        
        ResolutionSet resolution = getResolution();
        if (resolution == null) {
            return null;
        }
        
        ITmx resolved = resolve(resolution);
        
        if (showDiff) {
            DiffController differ = new DiffController();
            differ.diff(baseTmx, resolved);
        }
        
        return resolved;
    }
    
    public MergeAnalysis analyze(ITmx baseTmx, ITmx leftTmx, ITmx rightTmx) {
        setBaseTmx(baseTmx);
        setLeftTmx(leftTmx);
        setRightTmx(rightTmx);
        
        MergeAnalysis<Key, ITuv> initialAnalysis = DiffUtil.mapMerge(baseTmx, leftTmx, rightTmx);
        
        // Try to pre-resolve any conflicting TUVs where the only conflict is
        // uninteresting metadata. In that case, choose the newest one.
        List<Key> preResolved = new ArrayList<Key>();
        for (Key key : initialAnalysis.conflicts) {
            ITuv baseTuv = baseTmx.get(key);
            ITuv leftTuv = leftTmx.get(key);
            ITuv rightTuv = rightTmx.get(key);
            assert(!(leftTuv == null && rightTuv == null));
            if (leftTuv == null || rightTuv == null) {
                continue;
            }
            ITuv selected;
            if (leftTuv.equivalentTo(baseTuv)) {
                selected = rightTuv;
            } else if (rightTuv.equivalentTo(baseTuv)) {
                selected = leftTuv;
            } else if (!leftTuv.equivalentTo(rightTuv)) {
                continue;
            } else {
                selected = rightTuv;
            }
            initialAnalysis.modified.put(key, selected);
            preResolved.add(key);
        }
        initialAnalysis.conflicts.removeAll(preResolved);
        
        this.analysis = MergeAnalysis.unmodifiableAnalysis(initialAnalysis);
        propertySupport.firePropertyChange(PROP_CONFLICTCOUNT, null, null);
        
        return this.analysis;
    }
    
    public ITmx resolve(ResolutionSet resolution) {
        return baseTmx.applyChanges(resolution);
    }
    
    public ITmx resolve(BatchResolution res) {
        ResolutionSet resolution = ResolutionSet.fromAnalysis(analysis, leftTmx, rightTmx);
        for (Key key : analysis.conflicts) {
            switch (res) {
                case LEFT:
                    dispatchKey(key, baseTmx, leftTmx, resolution);
                    break;
                case BASE:
                    // No change
                    break;
                case RIGHT:
                    dispatchKey(key, baseTmx, rightTmx, resolution);
                    break;
            }
        }
        return resolve(resolution);
    }
    
    public boolean isConflictsAreResolved() {
        if (analysis != null && analysis.conflicts.isEmpty()) {
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
    
    public void clearSelections() {
        selections.clear();
        actionPerformed(null);
    }
    
    public Map<Key, AbstractButton[]> getSelections() {
        return Collections.unmodifiableMap(new HashMap<Key, AbstractButton[]>(selections));
    }
    
    public List<ConflictInfo> getConflicts() {
        List<ConflictInfo> conflicts = new ArrayList<ConflictInfo>();
        for (Key key : analysis.conflicts) {
            conflicts.add(new ConflictInfo(key, baseTmx.getSourceLanguage(),
                    baseTmx.get(key), leftTmx.get(key), rightTmx.get(key)));
        }
        return conflicts;
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
        return analysis.conflicts.size();
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
    
    public void setIsTwoWayMerge(boolean isTwoWayMerge) {
        this.isTwoWayMerge = isTwoWayMerge;
    }
    
    public boolean isTwoWayMerge() {
        return isTwoWayMerge;
    }
    
    public void setIsModal(boolean isModal) {
        this.isModal = isModal;
    }
    
    public boolean isModal() {
        return isModal;
    }
    
    public void setListViewThreshold(int conflicts) {
        if (conflicts < 0) {
            throw new IllegalArgumentException("The list view threshold must be at least 0.");
        }
        this.listViewThreshold = conflicts;
    }
    
    public int getListViewThreshold() {
        return this.listViewThreshold;
    }
    
    public void setParentWindow(Window window) {
        this.parentWindow = window;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        propertySupport.firePropertyChange(PROP_CONFLICTSARERESOLVED, null, null);
    }
    
    private ResolutionSet getResolution() {
        if (!isConflictsAreResolved()) {
            return null;
        }
        
        ResolutionSet resolution = ResolutionSet.fromAnalysis(analysis, leftTmx, rightTmx);
        
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
        
        return resolution;
    }
    
    private static void dispatchKey(Key key, ITmx baseTmx, ITmx thisTmx, ResolutionSet resolution) {
        if (!thisTmx.containsKey(key)) {
            resolution.toDelete.add(key);
        } else if (baseTmx.containsKey(key)) {
            resolution.toReplace.put(key, thisTmx.get(key));
        } else {
            resolution.toAdd.put(key, thisTmx.getTu(key));
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
    
    public static class ConflictInfo {
        public final Key key;
        public final String sourceLanguage;
        public final String targetLanguage;
        public final String baseTuvText;
        public final String leftTuvText;
        public final String rightTuvText;
        public final Map<String, String> baseTuvProps;
        public final Map<String, String> leftTuvProps;
        public final Map<String, String> rightTuvProps;
    
        public ConflictInfo(Key key, String sourceLanguage, ITuv baseTuv, ITuv leftTuv, ITuv rightTuv) {
            this.key = key;
            this.sourceLanguage = sourceLanguage;
            this.targetLanguage = baseTuv != null ? baseTuv.getLanguage()
                    : leftTuv != null ? leftTuv.getLanguage()
                    : rightTuv != null ? rightTuv.getLanguage()
                    : null;
            this.baseTuvText = baseTuv == null ? null : baseTuv.getContent();
            this.leftTuvText = leftTuv == null ? null : leftTuv.getContent();
            this.rightTuvText = rightTuv == null ? null : rightTuv.getContent();
            this.baseTuvProps = baseTuv == null ? null : baseTuv.getMetadata();
            this.leftTuvProps = leftTuv == null ? null : leftTuv.getMetadata();
            this.rightTuvProps = rightTuv == null ? null : rightTuv.getMetadata();
        }
    }
}
