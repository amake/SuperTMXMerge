/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge;

import gen.core.tmx14.Tu;
import gen.core.tmx14.Tuv;

/**
 *
 * @author aaron.madlon-kay
 */
public class TuDiffInfo {
    
    public final String sourceText;
    public final String sourceLanguage;
    
    public final String targetLanguage;
    public final String tuv1Text;
    public final String tuv2Text;
    
    public TuDiffInfo(String sourceText, String sourceLanguage,
            String targetLanguage, String tuv1Text, String tuv2Text) {
        this.sourceText = sourceText;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.tuv1Text = tuv1Text;
        this.tuv2Text = tuv2Text;
    }
    
    public static TuDiffInfo createFromTus(Tu tu1, Tu tu2, String sourceLanguage) {
        String sourceText = null;
        String targetLanguage = null;
        String tuv1Text = null;
        String tuv2Text = null;
        for (Tuv tuv : tu1.getTuv()) {
            if (sourceLanguage.equals(tuv.getLang())) {
                sourceText = tuv.getSeg().getContent().get(0).toString();
            } else {
                targetLanguage = tuv.getLang();
                tuv1Text = tuv.getSeg().getContent().get(0).toString();
            }
        }
        for (Tuv tuv : tu2.getTuv()) {
            if (targetLanguage.equals(tuv.getLang())) {
                tuv2Text = tuv.getSeg().getContent().get(0).toString();
            }
        }
        return new TuDiffInfo(sourceText,
                sourceLanguage,
                targetLanguage,
                tuv1Text,
                tuv2Text);
    }
}
