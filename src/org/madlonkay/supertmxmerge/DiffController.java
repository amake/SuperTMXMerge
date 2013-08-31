/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge;

import gen.core.tmx14.Tuv;
import java.beans.*;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.UnmarshalException;
import org.madlonkay.supertmxmerge.data.DiffInfo;
import org.madlonkay.supertmxmerge.data.DiffSet;
import org.madlonkay.supertmxmerge.data.TmxFile;
import org.madlonkay.supertmxmerge.data.TmxInfo;
import org.madlonkay.supertmxmerge.gui.DiffWindow;
import org.madlonkay.supertmxmerge.util.DiffUtil;
import org.madlonkay.supertmxmerge.util.FileUtil;
import org.madlonkay.supertmxmerge.util.TuvUtil;

/**
 *
 * @author amake
 */
public class DiffController implements Serializable, IController {
    
    public static final String PROP_FILE1 = "file1";
    public static final String PROP_FILE2 = "file2";
    public static final String PROP_FILE1NAME = "file1Name";
    public static final String PROP_FILE2NAME = "file2Name";
    public static final String PROP_FILE1UNITCOUNT = "file1UnitCount";
    public static final String PROP_FILE2UNITCOUNT = "file2UnitCount";

    private String file1;
    private String file2;
    
    private TmxFile tmx1;
    private TmxFile tmx2;
    
    private PropertyChangeSupport propertySupport;
    
    public DiffController() {
        propertySupport = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        String oldFile1 = this.file1;
        this.file1 = file1;
        propertySupport.firePropertyChange(PROP_FILE1, oldFile1, file1);
    }
    
    public String getFile2() {
        return file2;
    }

    public void setFile2(String file2) {
        String oldFile2 = this.file2;
        this.file2 = file2;
        propertySupport.firePropertyChange(PROP_FILE2, oldFile2, file2);
    }
    
    public String getFile1Name() {
        return tmx1 != null ? (new File(tmx1.getFilePath())).getName() : "File 1 name";
    }
    
    public String getFile2Name() {
        return tmx2 != null ? (new File(tmx2.getFilePath())).getName() : "File 2 name";
    }
    
    public int getFile1UnitCount() {
        return tmx1 != null ? tmx1.size() : -1;
    }
    
    public int getFile2UnitCount() {
        return tmx2 != null ? tmx2.size() : -1;
    }
    
    private void setTmx1(TmxFile tmx) {
        tmx1 = tmx;
        propertySupport.firePropertyChange(PROP_FILE1NAME, "blah", getFile1Name());
        propertySupport.firePropertyChange(PROP_FILE1UNITCOUNT, -1, getFile1UnitCount());
    }
    
    private void setTmx2(TmxFile tmx) {
        tmx2 = tmx;
        propertySupport.firePropertyChange(PROP_FILE2NAME, "blah", getFile2Name());
        propertySupport.firePropertyChange(PROP_FILE2UNITCOUNT, -1, getFile2UnitCount());
    }
    
    @Override
    public void go() {
        try {
            setTmx1(new TmxFile(getFile1()));
            setTmx2(new TmxFile(getFile2()));
        } catch (UnmarshalException ex) {
            Logger.getLogger(MergeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        DiffSet diffs = DiffUtil.generateDiffSet(tmx1, tmx2);
        
        DiffWindow window = new DiffWindow(this);
        for (DiffInfo info : generateDiffInfos(tmx1, tmx2, diffs)) {
            window.addDiffInfo(info);
        }
        
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    @Override
    public boolean validateInput() {
        return FileUtil.validateFile(getFile1()) 
                && FileUtil.validateFile(getFile2()) && !getFile1().equals(getFile2());
    }
    
    private static List<DiffInfo> generateDiffInfos(TmxFile tmx1, TmxFile tmx2, DiffSet set) {
        List<DiffInfo> diffInfos = new ArrayList<DiffInfo>();
        for (String key : set.deleted) {
            Tuv tuv = tmx1.getTuvMap().get(key);
            diffInfos.add(new DiffInfo(key, tmx1.getSourceLanguage(),
                    TuvUtil.getLanguage(tuv), TuvUtil.getContent(tuv), null));
        }
        for (String key : set.added) {
            Tuv tuv = tmx2.getTuvMap().get(key);
            diffInfos.add(new DiffInfo(key, tmx2.getSourceLanguage(),
                    TuvUtil.getLanguage(tuv), null, TuvUtil.getContent(tuv)));
        }
        for (String key : set.modified) {
            Tuv tuv1 = tmx1.getTuvMap().get(key);
            Tuv tuv2 = tmx2.getTuvMap().get(key);
            diffInfos.add(new DiffInfo(key, tmx1.getSourceLanguage(),
                    TuvUtil.getLanguage(tuv1), TuvUtil.getContent(tuv1), TuvUtil.getContent(tuv2)));
        }
        return diffInfos;
    }
}
