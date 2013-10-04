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
package org.madlonkay.supertmxmerge.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class ResolutionSet {
    public final List<ConflictInfo> conflicts;
    public final Set<Key> toDelete;
    public final Set<ITu> toAdd;
    public final Map<Key, ITuv> toReplace;
    
    public ResolutionSet(List<ConflictInfo> conflicts, Set<Key> toDelete, Set<ITu> toAdd, Map<Key, ITuv> toReplace) {
        this.conflicts = conflicts;
        this.toDelete = toDelete;
        this.toAdd = toAdd;
        this.toReplace = toReplace;
    }
}
