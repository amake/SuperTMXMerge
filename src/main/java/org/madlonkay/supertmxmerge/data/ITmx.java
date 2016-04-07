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
 */
public interface ITmx extends Map<Key,ITuv> {
        
    public ITu getTu(Key key);
    
    public String getSourceLanguage();
    
    public String getName();
    
    public int getSize();
    
    public Map<String, String> getMetadata();
    
    public ITmx applyChanges(ResolutionSet resolution);
    
    public void writeTo(File outputFile) throws WriteFailedException;
    
    public Object getUnderlyingRepresentation();
}
