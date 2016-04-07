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
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 * @param <K>
 * @param <V>
 */
public class MergeAnalysis<K,V> {
    public final Set<K> deleted;
    public final Set<K> added;
    public final Map<K, V> modified;
    public final Set<K> conflicts;
    
    public static MergeAnalysis unmodifiableAnalysis(MergeAnalysis analysis) {
        return new MergeAnalysis(Collections.unmodifiableSet(analysis.deleted),
                Collections.unmodifiableSet(analysis.added),
                Collections.unmodifiableMap(analysis.modified),
                Collections.unmodifiableSet(analysis.conflicts));
    }
    
    public MergeAnalysis(Set<K> deleted, Set<K> added, Map<K,V> modified, Set<K> conflicts) {
        this.deleted = deleted;
        this.added = added;
        this.modified = modified;
        this.conflicts = conflicts;
    }
    
    public boolean hasConflicts() {
        return !conflicts.isEmpty();
    }
}
