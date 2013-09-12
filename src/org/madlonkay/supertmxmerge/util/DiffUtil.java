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
import gen.core.tmx14.Tuv;
import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.madlonkay.supertmxmerge.data.DiffSet;
import org.madlonkay.supertmxmerge.data.TmxFile;

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
            deleteComponent.setSelectionStart(styling.line1);
            deleteComponent.setSelectionEnd(styling.line1 + styling.deleted);
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
