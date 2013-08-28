/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge;

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
}
