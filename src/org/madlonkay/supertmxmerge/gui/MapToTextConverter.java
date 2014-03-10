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
import java.util.MissingResourceException;
import java.util.TreeMap;
import org.jdesktop.beansbinding.Converter;
import org.madlonkay.supertmxmerge.util.LocString;

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
        Map<?, ?> map = (Map<?, ?>) value;
        return mapToHtml(map);
    }
    
    public static String mapToHtml(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("<html>");
        for (Entry<Object, Object> e : new TreeMap<Object, Object>(map).entrySet()) {
            sb.append("<b>");
            sb.append(localize(toString(e.getKey())));
            sb.append(":</b> ");
            sb.append(toString(e.getValue()));
            sb.append("<br>");
        }
        sb.append("</html>");
        return sb.toString();
    }
    
    public static String mapToPlainText(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Entry<Object, Object> e : new TreeMap<Object, Object>(map).entrySet()) {
            sb.append(" - ");
            sb.append(localize(toString(e.getKey())));
            sb.append(": ");
            sb.append(toString(e.getValue()));
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public Object convertReverse(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private static String localize(String string) {
        try {
            return LocString.get("STM_METADATA_" + string.toUpperCase());
        } catch (MissingResourceException ex) {
            return string;
        }
    }
    
    private static String toString(Object o) {
        return o == null ? "null" : o.toString();
    }
}
