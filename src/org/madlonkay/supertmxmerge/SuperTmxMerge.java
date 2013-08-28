/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge;

import gen.core.tmx14.Tuv;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.madlonkay.supertmxmerge.gui.DiffWindow;
import org.madlonkay.supertmxmerge.util.TmxFile;
import org.madlonkay.supertmxmerge.util.TuvUtil;

/**
 *
 * @author aaron.madlon-kay
 */
public class SuperTmxMerge {
    
    public static void diff(String file1, String file2) {
        TmxFile tmx1 = new TmxFile(file1);
        TmxFile tmx2 = new TmxFile(file2);
        
        // Deleted TUs
        Set<String> deleted = new HashSet<>(tmx1.getTuvMap().keySet());
        deleted.removeAll(tmx2.getTuvMap().keySet());
        
        // Added TUs
        Set<String> added = new HashSet<>(tmx2.getTuvMap().keySet());
        added.removeAll(tmx1.getTuvMap().keySet());
        
        // Modified TUs
        Set<String> modified = new HashSet<>();
        for (Entry<String, Tuv> e : tmx1.getTuvMap().entrySet()) {
            Tuv newTuv = tmx2.getTuvMap().get(e.getKey());
            if (newTuv == null) {
                continue;
            }
            if (!TuvUtil.equals(e.getValue(), newTuv)) {
                modified.add(e.getKey());
            }
        }
        DiffWindow window = new DiffWindow(new TmxInfo(tmx1), new TmxInfo(tmx2),
                generateDiffInfos(tmx1, tmx2, deleted, added, modified));
        window.setVisible(true);
        window.pack();
    }
    
    private static List<TuDiffInfo> generateDiffInfos(TmxFile tmx1, TmxFile tmx2,
            Set<String> deleted, Set<String> added, Set<String> modified) {
        List<TuDiffInfo> diffInfos = new ArrayList<>();
        for (String key : deleted) {
            Tuv tuv = tmx1.getTuvMap().get(key);
            diffInfos.add(new TuDiffInfo(key, tmx1.getSourceLanguage(),
                    TuvUtil.getLanguage(tuv), TuvUtil.getContent(tuv), null));
        }
        for (String key : added) {
            Tuv tuv = tmx2.getTuvMap().get(key);
            diffInfos.add(new TuDiffInfo(key, tmx2.getSourceLanguage(),
                    TuvUtil.getLanguage(tuv), null, TuvUtil.getContent(tuv)));
        }
        for (String key : modified) {
            Tuv tuv1 = tmx1.getTuvMap().get(key);
            Tuv tuv2 = tmx2.getTuvMap().get(key);
            diffInfos.add(new TuDiffInfo(key, tmx1.getSourceLanguage(),
                    TuvUtil.getLanguage(tuv1), TuvUtil.getContent(tuv1), TuvUtil.getContent(tuv2)));
        }
        return diffInfos;
    }
}
