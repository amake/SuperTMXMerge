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

import gen.core.tmx14.Tu;
import gen.core.tmx14.Tuv;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class TuvUtil {
    
    public static Map<String, Tuv> generateTuvMap(List<Tu> tus, String sourceLanguage) {
        Map<String, Tuv> map = new HashMap<String, Tuv>();
        for (Tu tu : tus) {
            map.put(getContent(getSourceTuv(tu, sourceLanguage)),
                    getTargetTuv(tu, sourceLanguage));
        }
        return map;
    }
    
    public static Tuv getSourceTuv(Tu tu, String sourceLanguage) {
        for (Tuv tuv : tu.getTuv()) {
            if (sourceLanguage.equals(getLanguage(tuv))) {
                return tuv;
            }
        }
        return null;
    }
    
    public static Tuv getTargetTuv(Tu tu, String sourceLanguage) {
        for (Tuv tuv : tu.getTuv()) {
            if (!sourceLanguage.equals(getLanguage(tuv))) {
                return tuv;
            }
        }
        return null;
    }
    
    public static String getLanguage(Tuv tuv) {
        if (tuv.getLang() != null) {
            return tuv.getLang();
        }
        return tuv.getXmlLang();
    }
    
    public static String getContent(Tuv tuv) {
        return (String) tuv.getSeg().getContent().get(0);
    }
    
    public static boolean equals(Tuv tuv1, Tuv tuv2) {
        String seg1 = TuvUtil.getContent(tuv1);
        String seg2 = TuvUtil.getContent(tuv2);
        return seg1.equals(seg2);
    }
}
