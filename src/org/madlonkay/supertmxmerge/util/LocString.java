/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 *
 * @author aaron.madlon-kay
 */
public class LocString {
    
    public static final ResourceBundle bundle = ResourceBundle.getBundle("org/madlonkay/supertmxmerge/Strings");
    
    public static String get(String id) {
        return bundle.getString(id);
    }
    
    public static String getFormat(String id, Object var) {
        return MessageFormat.format(get(id), new Object[] {var});
    }
}
