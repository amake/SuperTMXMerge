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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class LocString {
    
    private static final ResourceBundle bundle = ResourceBundle.getBundle("org/madlonkay/supertmxmerge/Strings");
    
    private static final List<ResourceBundle> moreBundles = new ArrayList<ResourceBundle>();
    
    public static String get(String id) {
        for (int i = moreBundles.size() - 1; i >= 0; i--) {
            try {
                return moreBundles.get(i).getString(id);
            } catch (MissingResourceException ex) {}
        }
        return bundle.getString(id);
    }
    
    public static String getFormat(String id, Object... var) {
        return MessageFormat.format(get(id), var);
    }
    
    public static void addBundle(ResourceBundle bundle) {
        moreBundles.add(bundle);
    }
}
