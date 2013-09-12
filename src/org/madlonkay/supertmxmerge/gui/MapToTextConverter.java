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
package org.madlonkay.supertmxmerge.gui;

import java.util.Map;
import java.util.Map.Entry;
import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class MapToTextConverter extends Converter {

    @Override
    public Object convertForward(Object value) {
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder("<html>");
        for (Entry<String, String> e : ((Map<String,String>) value).entrySet()) {
            sb.append("<b>");
            sb.append(e.getKey());
            sb.append(":</b> ");
            sb.append(e.getValue());
            sb.append("<br>");
        }
        sb.append("</html>");
        return sb.toString();
    }

    @Override
    public Object convertReverse(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
