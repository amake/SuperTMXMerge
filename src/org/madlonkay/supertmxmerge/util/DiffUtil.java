/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge.util;

import gen.core.tmx14.Tuv;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.madlonkay.supertmxmerge.data.DiffSet;
import org.madlonkay.supertmxmerge.data.TmxFile;

/**
 *
 * @author amake
 */
public class DiffUtil {
    public static DiffSet generateDiffSet(TmxFile tmx1, TmxFile tmx2) {
        // Deleted TUs
        Set<String> deleted = new HashSet<String>(tmx1.getTuvMap().keySet());
        deleted.removeAll(tmx2.getTuvMap().keySet());
        
        // Added TUs
        Set<String> added = new HashSet<String>(tmx2.getTuvMap().keySet());
        added.removeAll(tmx1.getTuvMap().keySet());
        
        // Modified TUs
        Set<String> modified = new HashSet<String>();
        for (Map.Entry<String, Tuv> e : tmx1.getTuvMap().entrySet()) {
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
}
