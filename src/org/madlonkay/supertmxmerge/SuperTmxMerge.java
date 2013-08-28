/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge;

import org.madlonkay.supertmxmerge.data.DiffInfo;
import org.madlonkay.supertmxmerge.data.TmxInfo;
import gen.core.tmx14.Tuv;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.madlonkay.supertmxmerge.data.DiffSet;
import org.madlonkay.supertmxmerge.data.MergeInfo;
import org.madlonkay.supertmxmerge.gui.DiffWindow;
import org.madlonkay.supertmxmerge.data.TmxFile;
import org.madlonkay.supertmxmerge.gui.MergeWindow;
import org.madlonkay.supertmxmerge.util.TuvUtil;

/**
 *
 * @author aaron.madlon-kay
 */
public class SuperTmxMerge {
    
    public static void diff(String file1, String file2) {
        TmxFile tmx1 = new TmxFile(file1);
        TmxFile tmx2 = new TmxFile(file2);
        
        DiffSet diffs = generateDiffSet(tmx1, tmx2);
        
        DiffWindow window = new DiffWindow(new TmxInfo(tmx1), new TmxInfo(tmx2),
                generateDiffInfos(tmx1, tmx2, diffs));
        window.setVisible(true);
        window.pack();
    }
    
    
    
    public static void merge(String baseFile, String file1, String file2) {
        TmxFile baseTmx = new TmxFile(baseFile);
        TmxFile leftTmx = new TmxFile(file1);
        TmxFile rightTmx = new TmxFile(file2);
        
        DiffSet baseToLeft = generateDiffSet(baseTmx, leftTmx);
        DiffSet baseToRight = generateDiffSet(baseTmx, rightTmx);
        
        MergeWindow window = new MergeWindow(new TmxInfo(baseTmx), new TmxInfo(leftTmx),
                new TmxInfo(rightTmx), generateMergeInfos(baseTmx, leftTmx, rightTmx, baseToLeft, baseToRight));
        window.setVisible(true);
        window.pack();
    }
    
    private static DiffSet generateDiffSet(TmxFile tmx1, TmxFile tmx2) {
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
        return new DiffSet(deleted, added, modified);
    }
    
    private static List<DiffInfo> generateDiffInfos(TmxFile tmx1, TmxFile tmx2, DiffSet set) {
        List<DiffInfo> diffInfos = new ArrayList<>();
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
    
    private static List<MergeInfo> generateMergeInfos(TmxFile baseTmx, TmxFile leftTmx,
            TmxFile rightTmx, DiffSet baseToLeft, DiffSet baseToRight) {
        List<MergeInfo> mergeInfos = new ArrayList<>();
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
