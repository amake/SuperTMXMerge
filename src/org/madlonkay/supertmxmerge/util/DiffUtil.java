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
package org.madlonkay.supertmxmerge.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.madlonkay.supertmxmerge.data.ConflictInfo;
import org.madlonkay.supertmxmerge.data.DiffInfo;
import org.madlonkay.supertmxmerge.data.DiffSet;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.ITu;
import org.madlonkay.supertmxmerge.data.ITuv;
import org.madlonkay.supertmxmerge.data.Key;
import org.madlonkay.supertmxmerge.data.ResolutionSet;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class DiffUtil {
    
    public static final String DIFF_PROP_TYPE = "x-diff-type";
    public static final String DIFF_PROP_VALUE_ADDED = "added";
    public static final String DIFF_PROP_VALUE_DELETED = "deleted";
    public static final String DIFF_PROP_VALUE_MODIFIED = "modified";
    
    public static final String DIFF_PROP_MODIFIED_TYPE = "x-diff-modified";
    public static final String DIFF_PROP_MODIFIED_VALUE_BEFORE = "before";
    public static final String DIFF_PROP_MODIFIED_VALUE_AFTER = "after";
    
    public static List<DiffInfo> generateDiffData(ITmx tmx1, ITmx tmx2) {
        
        List<DiffInfo> diffInfos = new ArrayList<DiffInfo>();
        
        DiffSet set = generateDiffSet(tmx1, tmx2);
        
        for (Key key : set.deleted) {
            ITuv tuv = tmx1.getTuvMap().get(key);
            diffInfos.add(new DiffInfo(key, tmx1.getSourceLanguage(),
                    tuv.getLanguage(), tuv.getContent(), null));
        }
        for (Key key : set.added) {
            ITuv tuv = tmx2.getTuvMap().get(key);
            diffInfos.add(new DiffInfo(key, tmx2.getSourceLanguage(),
                    tuv.getLanguage(), null, tuv.getContent()));
        }
        for (Key key : set.modified) {
            ITuv tuv1 = tmx1.getTuvMap().get(key);
            ITuv tuv2 = tmx2.getTuvMap().get(key);
            diffInfos.add(new DiffInfo(key, tmx1.getSourceLanguage(),
                    tuv1.getLanguage(), tuv1.getContent(), tuv2.getContent()));
        }
        
        return diffInfos;
    }
    
    public static DiffSet generateDiffSet(ITmx tmx1, ITmx tmx2) {
        // Deleted TUs
        Set<Key> deleted = new HashSet<Key>(tmx1.getTuvMap().keySet());
        deleted.removeAll(tmx2.getTuvMap().keySet());
        
        // Added TUs
        Set<Key> added = new HashSet<Key>(tmx2.getTuvMap().keySet());
        added.removeAll(tmx1.getTuvMap().keySet());
        
        // Modified TUs
        Set<Key> modified = new HashSet<Key>();
        for (Map.Entry<Key, ITuv> e : tmx1.getTuvMap().entrySet()) {
            ITuv newTuv = tmx2.getTuvMap().get(e.getKey());
            if (newTuv == null) {
                continue;
            }
            if (!e.getValue().equals(newTuv)) {
                modified.add(e.getKey());
            }
        }
        return new DiffSet(deleted, added, modified);
    }
    
    public static ResolutionSet generateMergeData(ITmx baseTmx, ITmx leftTmx, ITmx rightTmx) {
        
        List<ConflictInfo> conflicts = new ArrayList<ConflictInfo>();
        
        HashSet<Key> toDelete = new HashSet<Key>();
        HashSet<ITu> toAdd = new HashSet<ITu>();
        HashMap<Key, ITuv> toReplace = new HashMap<Key, ITuv>();
        
        DiffSet baseToLeft = generateDiffSet(baseTmx, leftTmx);
        DiffSet baseToRight = generateDiffSet(baseTmx, rightTmx);
        
        Set<Key> conflictKeys = new HashSet<Key>();
        // New in left
        for (Key key : baseToLeft.added) {
            ITuv leftTuv = leftTmx.getTuvMap().get(key);
            ITuv rightTuv = rightTmx.getTuvMap().get(key);
            if (rightTuv == null || leftTuv.equals(rightTuv)) {
                toAdd.add(leftTmx.getTuMap().get(key));
            } else {
                conflicts.add(new ConflictInfo(key, leftTmx.getSourceLanguage(), leftTuv.getLanguage(),
                        null, leftTuv.getContent(), rightTuv.getContent()));
                conflictKeys.add(key);
            }
        }
        // New in right
        for (Key key : baseToRight.added) {
            if (conflictKeys.contains(key)) {
                continue;
            }
            ITuv leftTuv = leftTmx.getTuvMap().get(key);
            ITuv rightTuv = rightTmx.getTuvMap().get(key);
            if (leftTuv == null) {
                toAdd.add(rightTmx.getTuMap().get(key));
            } else if (rightTuv.equals(leftTuv)) {
                // We don't add in this case because it would duplicate when we added
                // in the similar case above.
            } else {
                conflicts.add(new ConflictInfo(key, rightTmx.getSourceLanguage(), rightTuv.getLanguage(),
                        null, leftTuv.getContent(), rightTuv.getContent()));
                conflictKeys.add(key);
            }
        }
        // Deleted from left
        for (Key key : baseToLeft.deleted) {
            if (conflictKeys.contains(key)) {
                continue;
            }
            ITuv rightTuv = rightTmx.getTuvMap().get(key);
            ITuv baseTuv = baseTmx.getTuvMap().get(key);
            if (rightTuv == null || baseTuv.equals(rightTuv)) {
                toDelete.add(key);
            } else {
                conflicts.add(new ConflictInfo(key, rightTmx.getSourceLanguage(), rightTuv.getLanguage(),
                        baseTuv.getContent(), null, rightTuv.getContent()));
                conflictKeys.add(key);
            }
        }
        // Deleted from right
        for (Key key : baseToRight.deleted) {
            if (conflictKeys.contains(key)) {
                continue;
            }
            ITuv leftTuv = leftTmx.getTuvMap().get(key);
            ITuv baseTuv = baseTmx.getTuvMap().get(key);
            if (leftTuv == null) {
                // We don't delete in this case because it would duplicate when we deleted
                // in the similar case above.
            } else if (baseTuv.equals(leftTuv)) {
                toDelete.add(key);
            } else {
                conflicts.add(new ConflictInfo(key, rightTmx.getSourceLanguage(), baseTuv.getLanguage(),
                        baseTuv.getContent(), leftTuv.getContent(), null));
                conflictKeys.add(key);
            }
        }
        // Modified on left
        for (Key key : baseToLeft.modified) {
            if (conflictKeys.contains(key)) {
                continue;
            }
            ITuv baseTuv = baseTmx.getTuvMap().get(key);
            ITuv leftTuv = leftTmx.getTuvMap().get(key);
            ITuv rightTuv = rightTmx.getTuvMap().get(key);
            if (leftTuv.equals(rightTuv) || baseTuv.equals(rightTuv)) {
                toReplace.put(key, leftTuv);
            } else {
                conflicts.add(new ConflictInfo(key, baseTmx.getSourceLanguage(), baseTuv.getLanguage(),
                        baseTuv.getContent(), leftTuv.getContent(), rightTuv.getContent()));
                conflictKeys.add(key);
            }
        }
        // Modified on right
        for (Key key : baseToRight.modified) {
            if (conflictKeys.contains(key)) {
                continue;
            }
            ITuv leftTuv = leftTmx.getTuvMap().get(key);
            ITuv rightTuv = rightTmx.getTuvMap().get(key);
            ITuv baseTuv = baseTmx.getTuvMap().get(key);
            if (leftTuv.equals(rightTuv) || baseTuv.equals(leftTuv)) {
                toReplace.put(key, rightTuv);
            } else {
                conflicts.add(new ConflictInfo(key, baseTmx.getSourceLanguage(), baseTuv.getLanguage(),
                        baseTuv.getContent(), leftTuv.getContent(), rightTuv.getContent()));
                conflictKeys.add(key);
            }
        }
        
        return new ResolutionSet(conflicts, toDelete, toAdd, toReplace);
    }
}
