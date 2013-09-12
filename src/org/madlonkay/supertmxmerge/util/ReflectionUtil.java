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
package org.madlonkay.supertmxmerge.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class ReflectionUtil {
    
    private static final Logger LOGGER = Logger.getLogger(ReflectionUtil.class.getName());
    
    public static Map<String, String> simplePropsToMap(Object object) {
        Map<String, String> result = new HashMap<String, String>();
        
        for (Method m : object.getClass().getMethods()) {
            if (m.getName().startsWith("get") && m.getParameterTypes().length == 0 &&
                    (m.getReturnType().isPrimitive() || m.getReturnType().equals(String.class))) {
                try {
                    Object value = m.invoke(object, (Object[])null);
                    result.put(m.getName().substring("get".length()),
                            value == null ? "null" : value.toString());
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }
}
