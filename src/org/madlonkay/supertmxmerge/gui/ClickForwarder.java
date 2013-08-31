/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge.gui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractButton;

/**
 *
 * @author amake
 */
public class ClickForwarder extends MouseAdapter {

    private AbstractButton target;
    
    public ClickForwarder(AbstractButton target) {
        this.target = target;
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        target.doClick();
    }    
}
