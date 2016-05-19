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
import java.util.Set;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 * @param <T>
 */
public class DiffAnalysis<T> {
    public final Set<T> deleted;
    public final Set<T> added;
    public final Set<T> modified;
    
    public DiffAnalysis(Set<T> deleted, Set<T> added, Set<T> modified) {
        this.deleted = Collections.unmodifiableSet(deleted);
        this.added = Collections.unmodifiableSet(added);
        this.modified = Collections.unmodifiableSet(modified);
    }
}