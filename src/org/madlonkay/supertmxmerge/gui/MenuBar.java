/*
 * Copyright (C) 2014 Aaron Madlon-Kay <aaron@madlon-kay.com>.
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

package org.madlonkay.supertmxmerge.gui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.madlonkay.supertmxmerge.SuperTmxMerge;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class MenuBar extends JMenuBar {
    
    public MenuBar() {
        JMenu fileMenu = new JMenu();
        JMenuItem fileMenuNewItem = new JMenuItem();
        
        fileMenu.setText(LocString.get("file_menu")); // NOI18N

        fileMenuNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenuNewItem.setText(LocString.get("file_menu_new")); // NOI18N
        fileMenuNewItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                GuiUtil.safelyRunBlockingRoutine(new Runnable() {
                    @Override
                    public void run() {
                        SuperTmxMerge.promptForFiles();
                    }
                });
            }
        });
        fileMenu.add(fileMenuNewItem);

        add(fileMenu);
    }
}
