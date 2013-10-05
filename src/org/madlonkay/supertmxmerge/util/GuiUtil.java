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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class GuiUtil {
    
    private static final Logger LOGGER = Logger.getLogger(GuiUtil.class.getName());

    public static void displayWindow(final JFrame window) {
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
    
    public static void blockOnWindow(final JFrame window) {
        final Object lock = new Object();
        
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                synchronized (lock) {
                    lock.notify();
                }
            }
        });
        
        synchronized (lock) {
            while (window.isVisible()) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
        
        System.out.println("Exited GuiUtil#blockOnWindow");
    }
    
    public static void safelyRunBlockingRoutine(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            new Thread(runnable).start();
        } else {
            runnable.run();
        }
    }
}
