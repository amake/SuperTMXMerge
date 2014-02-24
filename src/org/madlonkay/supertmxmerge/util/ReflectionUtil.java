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
package org.madlonkay.supertmxmerge.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
        Method[] methods = object.getClass().getMethods();
        if (methods.length == 0) {
            return Collections.EMPTY_MAP;
        }
        
        Map<String, String> result = new HashMap<String, String>();
        
        for (Method m : methods) {
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
    
    public static Map<String, String> simpleMembersToMap(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        if (fields.length == 0) {
            return Collections.EMPTY_MAP;
        }
        
        Map<String, String> result = new HashMap<String, String>();
        
        for (Field f : fields) {
            if (f.getType().isPrimitive() || f.getType().equals(String.class)) {
                try {
                    Object value = f.get(object);
                    result.put(f.getName(),
                            value == null ? "null" : value.toString());
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    public static Map<String, String> listPropsToMap(List<Object> list) {
        if (list.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        
        Map<String, String> result = new HashMap<String, String>();
        Map<Class, Integer> count = new HashMap<Class, Integer>();
        
        for (Object o : list) {
            try {
                Method m = o.getClass().getDeclaredMethod("getContent", (Class<?>[]) null);
                if (m == null) {
                    throw new RuntimeException("TUV contained item that didn't respond to getContent().");
                }
                if (!m.getReturnType().equals(String.class)) {
                    continue;
                }
                Integer n = count.get(o.getClass());
                if (n == null) {
                    n = 1;
                }
                result.put(o.getClass().getSimpleName() + n, (String) m.invoke(o, (Object[]) null));
                count.put(o.getClass(), n + 1);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
}
