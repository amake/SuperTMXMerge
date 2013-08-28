/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge.data;

import java.io.File;

/**
 *
 * @author aaron.madlon-kay
 */
public class TmxInfo {
    
    public final String name;
    public final int textUnits;
    
    public TmxInfo (String name, int textUnits) {
        this.name = name;
        this.textUnits = textUnits;
    }
    
    public TmxInfo (TmxFile tmx) {
        this.name = (new File(tmx.getFilePath())).getName();
        this.textUnits = tmx.size();
    }
}
