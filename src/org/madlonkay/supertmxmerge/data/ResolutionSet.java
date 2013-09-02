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

import gen.core.tmx14.Tu;
import gen.core.tmx14.Tuv;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class ResolutionSet {
    public Set<String> toDelete;
    public Set<Tu> toAdd;
    public Map<String, Tuv> toReplace;
    
    public ResolutionSet(Set<String> toDelete, Set<Tu> toAdd, Map<String, Tuv> toReplace) {
        this.toDelete = toDelete;
        this.toAdd = toAdd;
        this.toReplace = toReplace;
    }
}
