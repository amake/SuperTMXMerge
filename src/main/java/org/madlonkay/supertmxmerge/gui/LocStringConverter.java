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
package org.madlonkay.supertmxmerge.gui;

import org.jdesktop.beansbinding.Converter;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class LocStringConverter extends Converter {

    private final String id;
    private final String idSingular;
    
    /**
     * Empty constructor to make NetBeans' design view happy.
     * Do not use.
     */
    public LocStringConverter() {
        this.id = null;
        this.idSingular = null;
    }
    
    public LocStringConverter(String id, String idSingular) {
        this.id = id;
        this.idSingular = idSingular;
    }
    
    @Override
    public Object convertForward(Object value) {
        return convert(value);
    }

    @Override
    public Object convertReverse(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public String convert(Object value) {
        if (value instanceof Integer && ((Integer) value) == 1) {
            return LocString.get(idSingular);
        }
        if (value instanceof Object[]) {
            return LocString.getFormat(id, (Object[]) value);
        }
        return LocString.getFormat(id, value);
    }
}
