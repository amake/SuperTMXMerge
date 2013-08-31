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
    public static final String PROP_TMX1 = "tmx1";
    public static final String PROP_TMX2 = "tmx2";

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
    
    public TmxFile getTmx1() {
        return tmx1;
    }
    
    public void setTmx1(TmxFile tmx1) {
        TmxFile oldTmx1 = this.tmx1;
        this.tmx1 = tmx1;
        propertySupport.firePropertyChange(PROP_TMX1, oldTmx1, tmx1);
    }
    
    public TmxFile getTmx2() {
        return tmx2;
    }
    
    public void setTmx2(TmxFile tmx2) {
        TmxFile oldTmx2 = this.tmx2;
        this.tmx2 = tmx2;
        propertySupport.firePropertyChange(PROP_TMX2, oldTmx2, tmx2);
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
