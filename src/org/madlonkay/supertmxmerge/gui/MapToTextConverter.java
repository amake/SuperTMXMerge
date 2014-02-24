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

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class MapToTextConverter extends Converter {

    @Override
    public Object convertForward(Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException();
        }
        Map<Object, Object> map = (Map<Object, Object>) value;
        if (map.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("<html>");
        for (Entry<Object, Object> e : new TreeMap<Object, Object>(map).entrySet()) {
            sb.append("<b>");
            sb.append(toString(e.getKey()));
            sb.append(":</b> ");
            sb.append(toString(e.getValue()));
            sb.append("<br>");
        }
        sb.append("</html>");
        return sb.toString();
    }

    @Override
    public Object convertReverse(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private String toString(Object o) {
        return o == null ? "null" : o.toString();
    }
}
