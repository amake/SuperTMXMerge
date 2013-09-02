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

import gen.core.tmx14.Tu;
import gen.core.tmx14.Tuv;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import org.madlonkay.supertmxmerge.data.DiffSet;
import org.madlonkay.supertmxmerge.data.MergeInfo;
import org.madlonkay.supertmxmerge.data.ResolutionSet;
import org.madlonkay.supertmxmerge.data.TmxFile;
import org.madlonkay.supertmxmerge.gui.MergeWindow;
import org.madlonkay.supertmxmerge.util.DiffUtil;
import org.madlonkay.supertmxmerge.util.FileUtil;
import org.madlonkay.supertmxmerge.util.TuvUtil;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class MergeController implements Serializable, IController, ActionListener {
    public static final Logger LOGGER = Logger.getLogger(MergeController.class.getName());
    
    public static final String PROP_BASEFILE = "baseFile";
    public static final String PROP_LEFTFILE = "leftFile";
    public static final String PROP_RIGHTFILE = "rightFile";
    
    public static final String PROP_BASETMX = "baseTmx";
    public static final String PROP_LEFTTMX = "leftTmx";
    public static final String PROP_RIGHTTMX = "rightTmx";
    public static final String PROP_OUTPUTFILE = "outputFile";
    public static final String PROP_CONFLICTCOUNT = "conflictCount";
    
    public static final String PROP_OUTPUTISVALID = "outputIsValid";

    private PropertyChangeSupport propertySupport;
    
    private String baseFile;
    private String leftFile;
    private String rightFile;
    private String outputFile;
    
    private TmxFile baseTmx;
    private TmxFile leftTmx;
    private TmxFile rightTmx;
    
    private int conflictCount;
    
    private Map<String, AbstractButton[]> selections = new HashMap<String, AbstractButton[]>();
    
    private Set<String> toDelete = new HashSet<String>();
    private Set<Tu> toAdd = new HashSet<Tu>();
    private Map<String, Tuv> toModify = new HashMap<String, Tuv>();
    
    public MergeController() {
        propertySupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public String getBaseFile() {
        return baseFile;
    }

    public void setBaseFile(String baseFile) {
        java.lang.String oldBaseFile = this.baseFile;
        this.baseFile = baseFile;
        propertySupport.firePropertyChange(PROP_BASEFILE, oldBaseFile, baseFile);
        propertySupport.firePropertyChange(PROP_INPUTISVALID, null, null);
    }

    public String getLeftFile() {
        return leftFile;
    }

    public void setLeftFile(String leftFile) {
        java.lang.String oldLeftFile = this.leftFile;
        this.leftFile = leftFile;
        propertySupport.firePropertyChange(PROP_LEFTFILE, oldLeftFile, leftFile);
        propertySupport.firePropertyChange(PROP_INPUTISVALID, null, null);
    }

    public String getRightFile() {
        return rightFile;
    }

    public void setRightFile(String rightFile) {
        java.lang.String oldRightFile = this.rightFile;
        this.rightFile = rightFile;
        propertySupport.firePropertyChange(PROP_RIGHTFILE, oldRightFile, rightFile);
        propertySupport.firePropertyChange(PROP_INPUTISVALID, null, null);
    }
    
    @Override
    public void go() {
        try {
            setBaseTmx(new TmxFile(getBaseFile()));
            setLeftTmx(new TmxFile(getLeftFile()));
            setRightTmx(new TmxFile(getRightFile()));
        } catch (UnmarshalException ex) {
            Logger.getLogger(MergeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        MergeWindow window = new MergeWindow(this);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
    
    @Override
    public boolean getInputIsValid() {
        return FileUtil.validateFile(getBaseFile()) && FileUtil.validateFile(getLeftFile())
                && FileUtil.validateFile(getRightFile())
                && !getBaseFile().equals(getLeftFile()) && !getBaseFile().equals(getRightFile())
                && !getLeftFile().equals(getRightFile());
    }
    
    public boolean getOutputIsValid() {
        if (selections.size() < 1) {
            return false;
        }
        for (Entry<String, AbstractButton[]> e : selections.entrySet()) {
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
    
    public void addSelection(String key, AbstractButton[] buttons) {
        selections.put(key, buttons);
    }
    
    public List<MergeInfo> getMergeInfos() {
        
        DiffSet baseToLeft = DiffUtil.generateDiffSet(getBaseTmx(), getLeftTmx());
        DiffSet baseToRight = DiffUtil.generateDiffSet(getBaseTmx(), getRightTmx());
        
        List<MergeInfo> mergeInfos = new ArrayList<MergeInfo>();
        Set<String> addedKeys = new HashSet<String>();
        // New in left
        for (String key : baseToLeft.added) {
            Tuv leftTuv = getLeftTmx().getTuvMap().get(key);
            Tuv rightTuv = getRightTmx().getTuvMap().get(key);
            if (rightTuv == null) {
                toAdd.add(leftTmx.getTuMap().get(key));
                continue;
            }
            if (!TuvUtil.equals(leftTuv, rightTuv)) {
                mergeInfos.add(new MergeInfo(key, getLeftTmx().getSourceLanguage(), TuvUtil.getLanguage(leftTuv),
                        null, TuvUtil.getContent(leftTuv), TuvUtil.getContent(rightTuv)));
                addedKeys.add(key);
            }
        }
        // New in right
        for (String key : baseToRight.added) {
            if (addedKeys.contains(key)) {
                continue;
            }
            Tuv leftTuv = getLeftTmx().getTuvMap().get(key);
            Tuv rightTuv = getRightTmx().getTuvMap().get(key);
            if (leftTuv == null) {
                toAdd.add(rightTmx.getTuMap().get(key));
                continue;
            }
            if (!TuvUtil.equals(leftTuv, rightTuv)) {
                mergeInfos.add(new MergeInfo(key, getRightTmx().getSourceLanguage(), TuvUtil.getLanguage(rightTuv),
                        null, TuvUtil.getContent(leftTuv), TuvUtil.getContent(rightTuv)));
                addedKeys.add(key);
            }
        }
        // Deleted from left
        for (String key : baseToLeft.deleted) {
            Tuv rightTuv = getRightTmx().getTuvMap().get(key);
            if (rightTuv == null) {
                toDelete.add(key);
                continue;
            } else {
                Tuv baseTuv = getBaseTmx().getTuvMap().get(key);
                mergeInfos.add(new MergeInfo(key, getRightTmx().getSourceLanguage(), TuvUtil.getLanguage(rightTuv),
                        TuvUtil.getContent(baseTuv), null, TuvUtil.getContent(rightTuv)));
                addedKeys.add(key);
            }
        }
        // Deleted from right
        for (String key : baseToRight.deleted) {
            Tuv leftTuv = getLeftTmx().getTuvMap().get(key);
            if (leftTuv == null) {
                toDelete.add(key);
                continue;
            } else {
                Tuv baseTuv = getBaseTmx().getTuvMap().get(key);
                mergeInfos.add(new MergeInfo(key, getRightTmx().getSourceLanguage(), TuvUtil.getLanguage(baseTuv),
                        TuvUtil.getContent(baseTuv), TuvUtil.getContent(leftTuv), null));
                addedKeys.add(key);
            }
        }
        // Modified on left
        for (String key : baseToLeft.modified) {
            Tuv leftTuv = getLeftTmx().getTuvMap().get(key);
            Tuv rightTuv = getRightTmx().getTuvMap().get(key);
            if (TuvUtil.equals(leftTuv, rightTuv)) {
                toModify.put(key, leftTuv);
                continue;
            } else {
                Tuv baseTuv = getBaseTmx().getTuvMap().get(key);
                mergeInfos.add(new MergeInfo(key, getBaseTmx().getSourceLanguage(), TuvUtil.getLanguage(baseTuv),
                        TuvUtil.getContent(baseTuv), TuvUtil.getContent(leftTuv), TuvUtil.getContent(rightTuv)));
                addedKeys.add(key);
            }
        }
        // Modified on right
        for (String key : baseToRight.modified) {
            if (addedKeys.contains(key)) {
                continue;
            }
            Tuv leftTuv = getLeftTmx().getTuvMap().get(key);
            Tuv rightTuv = getRightTmx().getTuvMap().get(key);
            Tuv baseTuv = getBaseTmx().getTuvMap().get(key);
            if (TuvUtil.equals(leftTuv, rightTuv)) {
                toModify.put(key, rightTuv);
            } else {
                mergeInfos.add(new MergeInfo(key, getBaseTmx().getSourceLanguage(), TuvUtil.getLanguage(baseTuv),
                        TuvUtil.getContent(baseTuv), TuvUtil.getContent(leftTuv), TuvUtil.getContent(rightTuv)));
                addedKeys.add(key);
            }
        }
        setConflictCount(mergeInfos.size());
        return mergeInfos;
    }

    /**
     * @return the baseTmx
     */
    public TmxFile getBaseTmx() {
        return baseTmx;
    }

    /**
     * @param baseTmx the baseTmx to set
     */
    public void setBaseTmx(TmxFile baseTmx) {
        org.madlonkay.supertmxmerge.data.TmxFile oldBaseTmx = this.baseTmx;
        this.baseTmx = baseTmx;
        propertySupport.firePropertyChange(PROP_BASETMX, oldBaseTmx, baseTmx);
    }

    /**
     * @return the leftTmx
     */
    public TmxFile getLeftTmx() {
        return leftTmx;
    }

    /**
     * @param leftTmx the leftTmx to set
     */
    public void setLeftTmx(TmxFile leftTmx) {
        org.madlonkay.supertmxmerge.data.TmxFile oldLeftTmx = this.leftTmx;
        this.leftTmx = leftTmx;
        propertySupport.firePropertyChange(PROP_LEFTTMX, oldLeftTmx, leftTmx);
    }

    /**
     * @return the rightTmx
     */
    public TmxFile getRightTmx() {
        return rightTmx;
    }

    /**
     * @param rightTmx the rightTmx to set
     */
    public void setRightTmx(TmxFile rightTmx) {
        org.madlonkay.supertmxmerge.data.TmxFile oldRightTmx = this.rightTmx;
        this.rightTmx = rightTmx;
        propertySupport.firePropertyChange(PROP_RIGHTTMX, oldRightTmx, rightTmx);
    }

    /**
     * @return the outputFile
     */
    public String getOutputFile() {
        return outputFile;
    }

    /**
     * @param outputFile the outputFile to set
     */
    public void setOutputFile(String outputFile) {
        java.lang.String oldOutputFile = this.outputFile;
        this.outputFile = outputFile;
        propertySupport.firePropertyChange(PROP_OUTPUTFILE, oldOutputFile, outputFile);
    }

    /**
     * @return the conflictCount
     */
    public int getConflictCount() {
        return conflictCount;
    }

    /**
     * @param conflictCount the conflictCount to set
     */
    public void setConflictCount(int conflictCount) {
        int oldConflictCount = this.conflictCount;
        this.conflictCount = conflictCount;
        propertySupport.firePropertyChange(PROP_CONFLICTCOUNT, oldConflictCount, conflictCount);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        propertySupport.firePropertyChange(PROP_OUTPUTISVALID, null, null);
    }
    
    public void resolve() {
        for (Entry<String, AbstractButton[]> e : selections.entrySet()) {
            String key = e.getKey();
            switch (getSelection(e.getValue())) {
                case 0:
                    dispatchKey(key, leftTmx);
                    break;
                case 1:
                    // No change
                    break;
                case 2:
                    dispatchKey(key, rightTmx);
                    break;
            }
        }
        baseTmx.applyChanges(new ResolutionSet(toDelete, toAdd, toModify));
        try {
            baseTmx.writeTo(outputFile);
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private void dispatchKey(String key, TmxFile tmx) {
        if (!tmx.getTuvMap().containsKey(key)) {
            toDelete.add(key);
        } else if (baseTmx.getTuvMap().containsKey(key)) {
            toModify.put(key, tmx.getTuvMap().get(key));
        } else {
            toAdd.add(tmx.getTuMap().get(key));
        }
    }
    
    private int getSelection(AbstractButton[] buttons) {
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
