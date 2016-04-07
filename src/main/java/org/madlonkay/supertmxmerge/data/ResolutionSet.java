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
package org.madlonkay.supertmxmerge.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class ResolutionSet {
    public final Set<Key> toDelete;
    public final Map<Key, ITu> toAdd;
    public final Map<Key, ITuv> toReplace;
    
    public static ResolutionSet fromAnalysis(MergeAnalysis<Key,ITuv> analysis, ITmx leftTmx, ITmx rightTmx) {
        Set<Key> toDelete = new HashSet<Key>(analysis.deleted);
        Map<Key,ITu> toAdd = new HashMap<Key,ITu>();
        Map<Key,ITuv> toReplace = new HashMap<Key,ITuv>(analysis.modified);
        
        // Add
        for (Key key : analysis.added) {
            ITu leftTuv = leftTmx.getTu(key);
            if (leftTuv != null) {
                toAdd.put(key, leftTuv);
                continue;
            }
            ITu rightTuv = rightTmx.getTu(key);
            assert(rightTuv != null);
            toAdd.put(key, rightTuv);
        }
        
        return new ResolutionSet(toDelete, toAdd, toReplace);
    }
    
    public static ResolutionSet unmodifiableSet(ResolutionSet set) {
        return new ResolutionSet(Collections.unmodifiableSet(set.toDelete),
                Collections.unmodifiableMap(set.toAdd),
                Collections.unmodifiableMap(set.toReplace));
    }
    
    private ResolutionSet(Set<Key> toDelete, Map<Key, ITu> toAdd, Map<Key, ITuv> toReplace) {
        this.toDelete = toDelete;
        this.toAdd = toAdd;
        this.toReplace = toReplace;
    }
}
