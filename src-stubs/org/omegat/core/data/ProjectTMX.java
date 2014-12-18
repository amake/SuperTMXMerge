/*
 * Copyright (C) 2012 Alex Buloichik
 *               2013 Aaron Madlon-Kay
 *
 * This stub is licensed to LGPL from the original full GPL code with
 * the copyright holders' permission.
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
package org.omegat.core.data;

import java.util.Map;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class ProjectTMX {
    
    public ProjectTMX() {
        throw new UnsupportedOperationException("This class is a stub.");
    }
    
    public Map<String, TMXEntry> getDefaults() {
        throw new UnsupportedOperationException("This class is a stub.");
    }
    
    public Map<EntryKey, TMXEntry> getAlternatives() {
        throw new UnsupportedOperationException("This class is a stub.");
    }

    public void setDefault(String source, TMXEntry entry) {
        throw new UnsupportedOperationException("This class is a stub.");
    }

    public void setAlternative(EntryKey key, TMXEntry entry) {
        throw new UnsupportedOperationException("This class is a stub.");
    }
    public void applyDefaults(Map<String, TMXEntry> newDefaults) {
        throw new UnsupportedOperationException("This class is a stub.");
    }

    public void applyAlternatives(Map<EntryKey, TMXEntry> newAlternatives) {
        throw new UnsupportedOperationException("This class is a stub.");
    }
}
