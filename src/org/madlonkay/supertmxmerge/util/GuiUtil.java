/*
 * Copyright (C) 2013 Aaron Madlon-Kay <aaron@madlon-kay.com>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.madlonkay.supertmxmerge.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class GuiUtil {
    
    private static final Logger LOGGER = Logger.getLogger(GuiUtil.class.getName());

    public static void displayWindow(Window window) {
        displayWindow(window, null);
    }
    
    public static void displayWindow(Window window, Window parent) {
        window.pack();
        if (parent != null || fixSize(window)) {
            window.setLocationRelativeTo(parent);
        }
        window.setVisible(true);
    }
    
    public static void displayWindowCentered(Window window) {
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
    
    public static void closeWindow(Window window) {
        window.setVisible(false);
        window.dispose();
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
    
    public static void blockOnWindow(final Window window) {
        final Object lock = new Object();
                
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
    }
    
    public static void safelyRunBlockingRoutine(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            new Thread(runnable).start();
        } else {
            runnable.run();
        }
    }
    
    public static boolean isOSX() {
        return System.getProperty("os.name").contains("OS X");
    }
    
    public static void forwardMouseWheelEvent(JScrollPane target, MouseWheelEvent evt) {
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                new MouseWheelEvent(target, evt.getID(), evt.getWhen(),
                        evt.getModifiers(), evt.getX(), evt.getY(),
                        evt.getClickCount(), evt.isPopupTrigger(),
                        evt.getScrollType(), evt.getScrollAmount(), evt.getWheelRotation()));
    }
}
