/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge;

import gen.core.tmx14.Tuv;
import java.beans.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.UnmarshalException;
import org.madlonkay.supertmxmerge.data.DiffSet;
import org.madlonkay.supertmxmerge.data.MergeInfo;
import org.madlonkay.supertmxmerge.data.TmxFile;
import org.madlonkay.supertmxmerge.gui.MergeWindow;
import org.madlonkay.supertmxmerge.util.DiffUtil;
import org.madlonkay.supertmxmerge.util.FileUtil;
import org.madlonkay.supertmxmerge.util.TuvUtil;

/**
 *
 * @author amake
 */
public class MergeController implements Serializable, IController {
    public static final String PROP_BASEFILE = "baseFile";
    public static final String PROP_LEFTFILE = "leftFile";
    public static final String PROP_RIGHTFILE = "rightFile";
    
    public static final Logger LOGGER = Logger.getLogger(MergeController.class.getName());
    public static final String PROP_BASETMX = "PROP_BASETMX";
    public static final String PROP_LEFTTMX = "PROP_LEFTTMX";
    public static final String PROP_RIGHTTMX = "PROP_RIGHTTMX";

    private PropertyChangeSupport propertySupport;
    
    private String baseFile;
    private String leftFile;
    private String rightFile;
    
    private TmxFile baseTmx;
    private TmxFile leftTmx;
    private TmxFile rightTmx;
    
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
    }

    public String getLeftFile() {
        return leftFile;
    }

    public void setLeftFile(String leftFile) {
        java.lang.String oldLeftFile = this.leftFile;
        this.leftFile = leftFile;
        propertySupport.firePropertyChange(PROP_LEFTFILE, oldLeftFile, leftFile);
    }

    public String getRightFile() {
        return rightFile;
    }

    public void setRightFile(String rightFile) {
        java.lang.String oldRightFile = this.rightFile;
        this.rightFile = rightFile;
        propertySupport.firePropertyChange(PROP_RIGHTFILE, oldRightFile, rightFile);
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
    public boolean validateInput() {
        return FileUtil.validateFile(getBaseFile()) && FileUtil.validateFile(getLeftFile())
                && FileUtil.validateFile(getRightFile())
                && !getBaseFile().equals(getLeftFile()) && !getBaseFile().equals(getRightFile())
                && !getLeftFile().equals(getRightFile());
    }
    
    public List<MergeInfo> getMergeInfos() {
        
        DiffSet baseToLeft = DiffUtil.generateDiffSet(getBaseTmx(), getLeftTmx());
        DiffSet baseToRight = DiffUtil.generateDiffSet(getBaseTmx(), getRightTmx());
        
        List<MergeInfo> mergeInfos = new ArrayList<MergeInfo>();
        // New in left
        for (String key : baseToLeft.added) {
            Tuv leftTuv = getLeftTmx().getTuvMap().get(key);
            Tuv rightTuv = getRightTmx().getTuvMap().get(key);
            if (rightTuv == null) {
                continue;
            }
            if (!TuvUtil.equals(leftTuv, rightTuv)) {
                mergeInfos.add(new MergeInfo(key, getLeftTmx().getSourceLanguage(), TuvUtil.getLanguage(leftTuv),
                        null, TuvUtil.getContent(leftTuv), TuvUtil.getContent(rightTuv)));
            }
        }
        // New in right
        for (String key : baseToRight.added) {
            Tuv leftTuv = getLeftTmx().getTuvMap().get(key);
            if (leftTuv == null) {
                continue;
            }
            Tuv rightTuv = getRightTmx().getTuvMap().get(key);
            if (!TuvUtil.equals(leftTuv, rightTuv)) {
                mergeInfos.add(new MergeInfo(key, getRightTmx().getSourceLanguage(), TuvUtil.getLanguage(rightTuv),
                        null, TuvUtil.getContent(leftTuv), TuvUtil.getContent(rightTuv)));
            }
        }
        // Deleted from left
        for (String key : baseToLeft.deleted) {
            Tuv rightTuv = getRightTmx().getTuvMap().get(key);
            if (rightTuv != null) {
                Tuv baseTuv = getBaseTmx().getTuvMap().get(key);
                mergeInfos.add(new MergeInfo(key, getRightTmx().getSourceLanguage(), TuvUtil.getLanguage(rightTuv),
                        TuvUtil.getContent(baseTuv), null, TuvUtil.getContent(rightTuv)));
            }
        }
        // Deleted from right
        for (String key : baseToRight.deleted) {
            Tuv leftTuv = getLeftTmx().getTuvMap().get(key);
            if (leftTuv != null) {
                Tuv baseTuv = getBaseTmx().getTuvMap().get(key);
                mergeInfos.add(new MergeInfo(key, getRightTmx().getSourceLanguage(), TuvUtil.getLanguage(baseTuv),
                        TuvUtil.getContent(baseTuv), TuvUtil.getContent(leftTuv), null));
            }
        }
        // Modified on left
        for (String key : baseToLeft.modified) {
            if (!baseToRight.modified.contains(key)) {
                continue;
            }
            Tuv leftTuv = getLeftTmx().getTuvMap().get(key);
            Tuv rightTuv = getRightTmx().getTuvMap().get(key);
            if (!TuvUtil.equals(leftTuv, rightTuv)) {
                Tuv baseTuv = getBaseTmx().getTuvMap().get(key);
                mergeInfos.add(new MergeInfo(key, getBaseTmx().getSourceLanguage(), TuvUtil.getLanguage(baseTuv),
                        TuvUtil.getContent(baseTuv), TuvUtil.getContent(leftTuv), TuvUtil.getContent(rightTuv)));
            }
        }
        // Modified on right
        for (String key : baseToRight.modified) {
            if (!baseToLeft.modified.contains(key)) {
                continue;
            }
            Tuv leftTuv = getLeftTmx().getTuvMap().get(key);
            Tuv rightTuv = getRightTmx().getTuvMap().get(key);
            Tuv baseTuv = getBaseTmx().getTuvMap().get(key);
            if (!TuvUtil.equals(leftTuv, rightTuv)) {
                mergeInfos.add(new MergeInfo(key, getBaseTmx().getSourceLanguage(), TuvUtil.getLanguage(baseTuv),
                        TuvUtil.getContent(baseTuv), TuvUtil.getContent(leftTuv), TuvUtil.getContent(rightTuv)));
            }
        }
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
}
