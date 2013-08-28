/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge.data;

/**
 *
 * @author aaron.madlon-kay
 */
public class MergeInfo {
    public final String sourceText;
    public final String sourceLanguage;
    
    public final String targetLanguage;
    public final String baseTuvText;
    public final String leftTuvText;
    public final String rightTuvText;
    
    public MergeInfo(String sourceText, String sourceLanguage,
            String targetLanguage, String baseTuvText, String leftTuvText, String rightTuvText) {
        this.sourceText = sourceText;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.baseTuvText = baseTuvText;
        this.leftTuvText = leftTuvText;
        this.rightTuvText = rightTuvText;
    }
}
