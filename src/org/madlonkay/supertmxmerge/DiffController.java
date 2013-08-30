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

    private String file1;
    private String file2;
    
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
    
    @Override
    public void go() {
        
        TmxFile tmx1 = null;
        TmxFile tmx2 = null;
        try {
            tmx1 = new TmxFile(getFile1());
            tmx2 = new TmxFile(getFile2());
        } catch (UnmarshalException ex) {
            Logger.getLogger(MergeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        DiffSet diffs = DiffUtil.generateDiffSet(tmx1, tmx2);
        
        DiffWindow window = new DiffWindow(new TmxInfo(tmx1), new TmxInfo(tmx2),
                generateDiffInfos(tmx1, tmx2, diffs));
        window.setVisible(true);
        window.pack();
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
