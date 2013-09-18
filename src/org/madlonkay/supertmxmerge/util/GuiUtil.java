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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class GuiUtil {

    public static void displayWindow(JFrame window) {
        window.pack();
        if (fixSize(window)) {
            window.setLocationRelativeTo(null);
        }
        window.setVisible(true);
    }
    
    public static boolean fixSize(Component component) {
        boolean changed = false;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int newHeight = component.getHeight();
        if (newHeight > screen.height * 0.9) {
            newHeight = (int) (screen.height * 0.9);
            changed = true;
        }
        int newWidth = component.getWidth();
        if (newWidth > screen.width * 0.9) {
            newWidth = (int) (screen.width * 0.9);
            changed = true;
        }
        
        if (changed) {
            component.setSize(newWidth, newHeight);
        }
        
        return changed;
    }
}
