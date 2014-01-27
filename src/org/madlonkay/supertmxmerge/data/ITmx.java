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

import java.io.File;
import java.util.Map;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 * @param <T1>
 */
public interface ITmx<T1,T2,T3> {
    
    public Map<Key, ? extends ITuv<T3>> getTuvMap();
    
    public Map<Key, ? extends ITu<T2,T3>> getTuMap();
    
    public String getSourceLanguage();
    
    public String getName();
    
    public int getSize();
    
    public Map<String, String> getMetadata();
    
    public ITmx<T1,T2,T3> applyChanges(ResolutionSet<? extends ITu<T2,T3>,? extends ITuv<T3>,T3> resolution);
    
    public void writeTo(File outputFile) throws WriteFailedException;
    
    public T1 getUnderlyingRepresentation();
}
