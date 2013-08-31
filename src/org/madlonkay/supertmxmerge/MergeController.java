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
    public static final String PROP_ACTIONTYPE = "actionType";
    
    public static final Logger LOGGER = Logger.getLogger(MergeController.class.getName());

    private PropertyChangeSupport propertySupport;
    
    private String baseFile;
    private String leftFile;
    private String rightFile;
    
    private int actionType;

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
        TmxFile baseTmx = null;
        TmxFile leftTmx = null;
        TmxFile rightTmx = null;
        try {
            baseTmx = new TmxFile(getBaseFile());
            leftTmx = new TmxFile(getLeftFile());
            rightTmx = new TmxFile(getRightFile());
        } catch (UnmarshalException ex) {
            Logger.getLogger(MergeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        DiffSet baseToLeft = DiffUtil.generateDiffSet(baseTmx, leftTmx);
        DiffSet baseToRight = DiffUtil.generateDiffSet(baseTmx, rightTmx);
        
        MergeWindow window = new MergeWindow(new TmxInfo(baseTmx), new TmxInfo(leftTmx),
                new TmxInfo(rightTmx), generateMergeInfos(baseTmx, leftTmx, rightTmx, baseToLeft, baseToRight));
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
    
    private static List<MergeInfo> generateMergeInfos(TmxFile baseTmx, TmxFile leftTmx,
            TmxFile rightTmx, DiffSet baseToLeft, DiffSet baseToRight) {
        List<MergeInfo> mergeInfos = new ArrayList<MergeInfo>();
        // New in left
        for (String key : baseToLeft.added) {
            Tuv leftTuv = leftTmx.getTuvMap().get(key);
            Tuv rightTuv = rightTmx.getTuvMap().get(key);
            if (rightTuv == null) {
                continue;
            }
            if (!TuvUtil.equals(leftTuv, rightTuv)) {
                mergeInfos.add(new MergeInfo(key, leftTmx.getSourceLanguage(), TuvUtil.getLanguage(leftTuv),
                        null, TuvUtil.getContent(leftTuv), TuvUtil.getContent(rightTuv)));
            }
        }
        // New in right
        for (String key : baseToRight.added) {
            Tuv leftTuv = leftTmx.getTuvMap().get(key);
            if (leftTuv == null) {
                continue;
            }
            Tuv rightTuv = rightTmx.getTuvMap().get(key);
            if (!TuvUtil.equals(leftTuv, rightTuv)) {
                mergeInfos.add(new MergeInfo(key, rightTmx.getSourceLanguage(), TuvUtil.getLanguage(rightTuv),
                        null, TuvUtil.getContent(leftTuv), TuvUtil.getContent(rightTuv)));
            }
        }
        // Deleted from left
        for (String key : baseToLeft.deleted) {
            Tuv rightTuv = rightTmx.getTuvMap().get(key);
            if (rightTuv != null) {
                Tuv baseTuv = baseTmx.getTuvMap().get(key);
                mergeInfos.add(new MergeInfo(key, rightTmx.getSourceLanguage(), TuvUtil.getLanguage(rightTuv),
                        TuvUtil.getContent(baseTuv), null, TuvUtil.getContent(rightTuv)));
            }
        }
        // Deleted from right
        for (String key : baseToRight.deleted) {
            Tuv leftTuv = leftTmx.getTuvMap().get(key);
            if (leftTuv != null) {
                Tuv baseTuv = baseTmx.getTuvMap().get(key);
                mergeInfos.add(new MergeInfo(key, rightTmx.getSourceLanguage(), TuvUtil.getLanguage(baseTuv),
                        TuvUtil.getContent(baseTuv), TuvUtil.getContent(leftTuv), null));
            }
        }
        // Modified on left
        for (String key : baseToLeft.modified) {
            if (!baseToRight.modified.contains(key)) {
                continue;
            }
            Tuv leftTuv = leftTmx.getTuvMap().get(key);
            Tuv rightTuv = rightTmx.getTuvMap().get(key);
            if (!TuvUtil.equals(leftTuv, rightTuv)) {
                Tuv baseTuv = baseTmx.getTuvMap().get(key);
                mergeInfos.add(new MergeInfo(key, baseTmx.getSourceLanguage(), TuvUtil.getLanguage(baseTuv),
                        TuvUtil.getContent(baseTuv), TuvUtil.getContent(leftTuv), TuvUtil.getContent(rightTuv)));
            }
        }
        // Modified on right
        for (String key : baseToRight.modified) {
            if (!baseToLeft.modified.contains(key)) {
                continue;
            }
            Tuv leftTuv = leftTmx.getTuvMap().get(key);
            Tuv rightTuv = rightTmx.getTuvMap().get(key);
            Tuv baseTuv = baseTmx.getTuvMap().get(key);
            if (!TuvUtil.equals(leftTuv, rightTuv)) {
                mergeInfos.add(new MergeInfo(key, baseTmx.getSourceLanguage(), TuvUtil.getLanguage(baseTuv),
                        TuvUtil.getContent(baseTuv), TuvUtil.getContent(leftTuv), TuvUtil.getContent(rightTuv)));
            }
        }
        return mergeInfos;
    }
}
