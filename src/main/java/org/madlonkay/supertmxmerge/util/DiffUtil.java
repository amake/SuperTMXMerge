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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.madlonkay.supertmxmerge.data.DiffAnalysis;
import org.madlonkay.supertmxmerge.data.MergeAnalysis;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class DiffUtil {
    
    public static <K,V> DiffAnalysis mapDiff(Map<K,V> before, Map<K,V> after) {
        // Deleted items
        Set<K> deleted = new HashSet<K>(before.keySet());
        deleted.removeAll(after.keySet());
        
        // Added items
        Set<K> added = new HashSet<K>(after.keySet());
        added.removeAll(before.keySet());
        
        // Modified items
        Set<K> modified = new HashSet<K>();
        for (Map.Entry<K,V> e : before.entrySet()) {
            V newItem = after.get(e.getKey());
            if (newItem == null) {
                continue;
            }
            if (!e.getValue().equals(newItem)) {
                modified.add(e.getKey());
            }
        }
        return new DiffAnalysis<K>(deleted, added, modified);
    }
    
    public static <K,V> MergeAnalysis<K,V> mapMerge(Map<K,V> base, Map<K,V> left, Map<K,V> right) {
         
        Set<K> toDelete = new HashSet<K>();
        Set<K> toAdd = new HashSet<K>();
        Map<K,V> toReplace = new HashMap<K,V>();
        Set<K> conflicts = new HashSet<K>();
        
        DiffAnalysis<K> baseToLeft = mapDiff(base, left);
        DiffAnalysis<K> baseToRight = mapDiff(base, right);
        
        // New in left
        for (K key : baseToLeft.added) {
            V leftItem = left.get(key);
            V rightItem = right.get(key);
            if (rightItem == null || leftItem.equals(rightItem)) {
                toAdd.add(key);
            } else {
                conflicts.add(key);
            }
        }
        // New in right
        for (K key : baseToRight.added) {
            if (conflicts.contains(key)) {
                continue;
            }
            V leftItem = left.get(key);
            V rightItem = right.get(key);
            if (leftItem == null) {
                toAdd.add(key);
            } else if (rightItem.equals(leftItem)) {
                // We don't add in this case because it would duplicate when we added
                // in the similar case above.
            } else {
                conflicts.add(key);
            }
        }
        // Deleted from left
        for (K key : baseToLeft.deleted) {
            if (conflicts.contains(key)) {
                continue;
            }
            V rightItem = right.get(key);
            V baseItem = base.get(key);
            if (rightItem == null || baseItem.equals(rightItem)) {
                toDelete.add(key);
            } else {
                conflicts.add(key);
            }
        }
        // Deleted from right
        for (K key : baseToRight.deleted) {
            if (conflicts.contains(key)) {
                continue;
            }
            V leftItem = left.get(key);
            V baseItem = base.get(key);
            if (leftItem == null) {
                // We don't delete in this case because it would duplicate when we deleted
                // in the similar case above.
            } else if (baseItem.equals(leftItem)) {
                toDelete.add(key);
            } else {
                conflicts.add(key);
            }
        }
        // Modified on left
        for (K key : baseToLeft.modified) {
            if (conflicts.contains(key)) {
                continue;
            }
            V baseItem = base.get(key);
            V leftItem = left.get(key);
            V rightItem = right.get(key);
            if (leftItem.equals(rightItem) || baseItem.equals(rightItem)) {
                toReplace.put(key, leftItem);
            } else {
                conflicts.add(key);
            }
        }
        // Modified on right
        for (K key : baseToRight.modified) {
            if (conflicts.contains(key)) {
                continue;
            }
            V leftItem = left.get(key);
            V rightItem = right.get(key);
            V baseItem = base.get(key);
            if (leftItem.equals(rightItem) || baseItem.equals(leftItem)) {
                toReplace.put(key, rightItem);
            } else {
                conflicts.add(key);
            }
        }
        
        return new MergeAnalysis(toDelete, toAdd, toReplace, conflicts);
    }
}
