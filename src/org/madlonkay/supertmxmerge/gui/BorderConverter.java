/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge.gui;

import javax.swing.border.Border;
import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author amake
 */
public class BorderConverter extends Converter {
    
    private Border selectedBorder;
    private Border defaultBorder;

    public BorderConverter(Border selectedBorder, Border defaultBorder) {
        this.selectedBorder = selectedBorder;
        this.defaultBorder = defaultBorder;
    }
            
    @Override
    public Object convertForward(Object value) {
        return ((Boolean) value) ? selectedBorder : defaultBorder;
    }

    @Override
    public Object convertReverse(Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
