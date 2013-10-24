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
package org.madlonkay.supertmxmerge.gui;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class FileDropHandler extends TransferHandler {
    
    private static final Logger LOGGER = Logger.getLogger(FileDropHandler.class.getName());
    
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        
        Component comp = support.getComponent();
        if (!(comp instanceof JTextComponent)) {
            return false;
        }
        JTextComponent textArea = (JTextComponent) comp;

        try {
            Object payload = support.getTransferable()
                    .getTransferData(DataFlavor.javaFileListFlavor);
            if (!(payload instanceof List)) {
              return false;
            }
            List<File> files = (List<File>) payload;
            if (files.size() > 1) {
                return false;
            }
            File file = files.get(0);
            //if (!file.getName().endsWith(".tmx")) {
            //    return false;
            //}
            String filePath = file.getCanonicalPath();
            textArea.setText(filePath);
        } catch (UnsupportedFlavorException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
}
