/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge.gui;

import org.jdesktop.beansbinding.Converter;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author amake
 */
public class UnitCountConverter extends Converter {

    @Override
    public Object convertForward(Object value) {
        return LocString.getFormat("number_of_units", value);
    }

    @Override
    public Object convertReverse(Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
