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
package org.madlonkay.supertmxmerge.data;

import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class Report {
    public final int conflicts;
    public final int added;
    public final int deleted;
    public final int modified;

    public Report(MergeAnalysis analysis, ResolutionSet resolution) {
        this.conflicts = analysis.conflicts.size();
        this.added = resolution.toAdd.size();
        this.deleted = resolution.toDelete.size();
        this.modified = resolution.toReplace.size();
    }
    
    public Report(MergeAnalysis analysis, DiffAnalysis diff) {
        this.conflicts = analysis.conflicts.size();
        this.added = diff.added.size();
        this.deleted = diff.deleted.size();
        this.modified = diff.modified.size();
    }

    @Override
    public String toString() {
        return LocString.getFormat("STM_REPORT_FORMAT", conflicts, added, deleted, modified);
    }
}
