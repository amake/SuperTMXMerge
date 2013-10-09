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
package org.madlonkay.supertmxmerge.util;

import bmsi.util.Diff;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
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
    
    private static final AttributeSet STYLE_INSERTED;
    private static final AttributeSet STYLE_INSERTED_ALT;
    private static final AttributeSet STYLE_DELETED;
    private static final AttributeSet STYLE_DELETED_ALT;
    
    static {
        MutableAttributeSet inserted = new SimpleAttributeSet();
        StyleConstants.setUnderline(inserted, true);
        StyleConstants.setForeground(inserted, Color.GREEN);
        STYLE_INSERTED = inserted;
        
        MutableAttributeSet insertedAlt = new SimpleAttributeSet();
        StyleConstants.setUnderline(insertedAlt, true);
        StyleConstants.setForeground(insertedAlt, Color.BLUE);
        STYLE_INSERTED_ALT = insertedAlt;
        
        MutableAttributeSet deleted = new SimpleAttributeSet();
        StyleConstants.setStrikeThrough(deleted, true);
        StyleConstants.setForeground(deleted, Color.RED);
        STYLE_DELETED = deleted;
        
        MutableAttributeSet deletedAlt = new SimpleAttributeSet();
        StyleConstants.setStrikeThrough(deletedAlt, true);
        StyleConstants.setForeground(deletedAlt, Color.ORANGE);
        STYLE_DELETED_ALT = deletedAlt;
    }
    
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
    
    private static DiffSet generateDiffSet(ITmx tmx1, ITmx tmx2) {
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
            if (rightTuv == null) {
                toAdd.add(leftTmx.getTuMap().get(key));
                continue;
            }
            if (!leftTuv.equals(rightTuv)) {
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
                continue;
            }
            if (!leftTuv.equals(rightTuv)) {
                conflicts.add(new ConflictInfo(key, rightTmx.getSourceLanguage(), rightTuv.getLanguage(),
                        null, leftTuv.getContent(), rightTuv.getContent()));
                conflictKeys.add(key);
            }
        }
        // Deleted from left
        for (Key key : baseToLeft.deleted) {
            ITuv rightTuv = rightTmx.getTuvMap().get(key);
            if (rightTuv == null) {
                toDelete.add(key);
                continue;
            } else {
                ITuv baseTuv = baseTmx.getTuvMap().get(key);
                conflicts.add(new ConflictInfo(key, rightTmx.getSourceLanguage(), rightTuv.getLanguage(),
                        baseTuv.getContent(), null, rightTuv.getContent()));
                conflictKeys.add(key);
            }
        }
        // Deleted from right
        for (Key key : baseToRight.deleted) {
            ITuv leftTuv = leftTmx.getTuvMap().get(key);
            if (leftTuv == null) {
                toDelete.add(key);
                continue;
            } else {
                ITuv baseTuv = baseTmx.getTuvMap().get(key);
                conflicts.add(new ConflictInfo(key, rightTmx.getSourceLanguage(), baseTuv.getLanguage(),
                        baseTuv.getContent(), leftTuv.getContent(), null));
                conflictKeys.add(key);
            }
        }
        // Modified on left
        for (Key key : baseToLeft.modified) {
            ITuv baseTuv = baseTmx.getTuvMap().get(key);
            ITuv leftTuv = leftTmx.getTuvMap().get(key);
            ITuv rightTuv = rightTmx.getTuvMap().get(key);
            if (leftTuv.equals(rightTuv) || baseTuv.equals(rightTuv)) {
                toReplace.put(key, leftTuv);
                continue;
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
                continue;
            } else {
                conflicts.add(new ConflictInfo(key, baseTmx.getSourceLanguage(), baseTuv.getLanguage(),
                        baseTuv.getContent(), leftTuv.getContent(), rightTuv.getContent()));
                conflictKeys.add(key);
            }
        }
        
        return new ResolutionSet(conflicts, toDelete, toAdd, toReplace);
    }
    
    public static Diff.change getCharacterDiff(String from, String to) {
        // Don't compute diff if entire strings were added/removed.
        if (from == null || to == null) {
            return null;
        }
        
        int[] fromArray = toArray(from);
        int[] toArray = toArray(to);
        
        return new Diff(fromArray, toArray).diff_2(false);
    }
    
    private static int[] toArray(String string) {
        if (string == null) {
            return new int[] {};
        }
        int[] result = new int[string.length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (int) string.charAt(i);
        }
        return result;
    }
    
    public static void applyStyling(JTextPane deleteComponent,
            JTextPane insertComponent, Diff.change styling, boolean alternateColors) {
        if (styling == null) {
            return;
        }
        
        if (styling.deleted > 0 && deleteComponent != null) {
            deleteComponent.setSelectionStart(styling.line0);
            deleteComponent.setSelectionEnd(styling.line0 + styling.deleted);
            deleteComponent.setCharacterAttributes(
                    alternateColors ? STYLE_DELETED_ALT : STYLE_DELETED, false);
        }
        
        if (styling.inserted > 0 && insertComponent != null) {
            insertComponent.setSelectionStart(styling.line1);
            insertComponent.setSelectionEnd(styling.line1 + styling.inserted);
            insertComponent.setCharacterAttributes(
                    alternateColors ? STYLE_INSERTED_ALT : STYLE_INSERTED, false);
        }
        
        applyStyling(deleteComponent, insertComponent, styling.link, alternateColors);
    }
    
    public static void applyStyling(JTextPane deleteComponent,
            JTextPane insertComponent, Diff.change styling) {
        applyStyling(deleteComponent, insertComponent, styling, false);
    }
}
