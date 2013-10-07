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
package org.omegat.core.data;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class ProjectTMX {
    
    Map<String, TMXEntry> defaults;

    Map<EntryKey, TMXEntry> alternatives;
    
    public ProjectTMX() {
        throw new UnsupportedOperationException("This class is a stub.");
    }
    
    public Collection<TMXEntry> getDefaults() {
        throw new UnsupportedOperationException("This class is a stub.");
    }
    
    public Collection<TMXEntry> getAlternatives() {
        throw new UnsupportedOperationException("This class is a stub.");
    }
}
