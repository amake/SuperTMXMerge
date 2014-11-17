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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class ResolutionSet<TuType extends ITu, TuvType extends ITuv> {
    public final Set<Key> toDelete;
    public final Map<Key, TuType> toAdd;
    public final Map<Key, TuvType> toReplace;
    
    public static <TmxType extends ITmx<?,TuType,TuvType>,TuvType extends ITuv,TuType extends ITu> ResolutionSet<TuType,TuvType> fromAnalysis(MergeAnalysis<Key,TuvType> analysis, TmxType leftTmx, TmxType rightTmx) {
        Set<Key> toDelete = new HashSet<Key>(analysis.deleted);
        Map<Key,TuType> toAdd = new HashMap<Key,TuType>();
        Map<Key,TuvType> toReplace = new HashMap<Key,TuvType>(analysis.modified);
        
        // Add
        for (Key key : analysis.added) {
            TuType leftTuv = leftTmx.getTu(key);
            if (leftTuv != null) {
                toAdd.put(key, leftTuv);
                continue;
            }
            TuType rightTuv = rightTmx.getTu(key);
            assert(rightTuv != null);
            toAdd.put(key, rightTuv);
        }
        
        return new ResolutionSet(toDelete, toAdd, toReplace);
    }
    
    private ResolutionSet(Set<Key> toDelete, Map<Key, TuType> toAdd, Map<Key, TuvType> toReplace) {
        this.toDelete = toDelete;
        this.toAdd = toAdd;
        this.toReplace = toReplace;
    }
}
