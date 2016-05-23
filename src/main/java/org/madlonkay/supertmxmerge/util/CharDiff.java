/*
 * Copyright (C) 2014 Aaron Madlon-Kay <aaron@madlon-kay.com>.
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class CharDiff {
    
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
    
    private static Patch makePatch(String from, String to) {
        if (from == null || to == null || from.equals(to) || (from.equals("") && to.equals(""))) {
            return null;
        }
        
        List<Character> fromList = toList(from);
        List<Character> toList = toList(to);
        
        return DiffUtils.diff(fromList, toList);
    }
    
    private static List<Character> toList(String string) {
        if (string == null || string.length() == 0) {
            return Collections.emptyList();
        }
        List<Character> result = new ArrayList<Character>();
        for (Character c : string.toCharArray()) {
            result.add(c);
        }
        return result;
    }
    
    public static void applyStyling(String from, String to, JTextPane deleteComponent,
            JTextPane insertComponent, boolean alternateColors) {
        Patch patch = makePatch(from, to);
        if (patch == null) {
            return;
        }
        for (Delta d : patch.getDeltas()) {
            Delta.TYPE type = d.getType();
            if (deleteComponent != null && (type == Delta.TYPE.DELETE || type == Delta.TYPE.CHANGE)) {
                Chunk original = d.getOriginal();
                deleteComponent.setSelectionStart(original.getPosition());
                deleteComponent.setSelectionEnd(original.getPosition() + original.size());
                deleteComponent.setCharacterAttributes(
                        alternateColors ? STYLE_DELETED_ALT : STYLE_DELETED, false);
            }

            if (insertComponent != null && (type == Delta.TYPE.INSERT || type == Delta.TYPE.CHANGE)) {
                Chunk revised = d.getRevised();
                insertComponent.setSelectionStart(revised.getPosition());
                insertComponent.setSelectionEnd(revised.getPosition() + revised.size());
                insertComponent.setCharacterAttributes(
                        alternateColors ? STYLE_INSERTED_ALT : STYLE_INSERTED, false);
            }
        }
    }
    
    public static void applyStyling(String from, String to, JTextPane deleteComponent,
            JTextPane insertComponent) {
        applyStyling(from, to, deleteComponent, insertComponent, false);
    }
}
