/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge.util;

import gen.core.tmx14.Tu;
import gen.core.tmx14.Tuv;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aaron.madlon-kay
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
