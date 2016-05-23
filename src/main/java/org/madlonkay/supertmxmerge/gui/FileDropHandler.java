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

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
@SuppressWarnings("serial")
public class FileDropHandler extends TransferHandler {
    
    private static final Logger LOGGER = Logger.getLogger(FileDropHandler.class.getName());
    
    private IDropCallback callback;
    
    public FileDropHandler(IDropCallback callback) {
        this.callback = callback;
    }
    
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        
        List<File> files;
        try {
            Object payload = support.getTransferable()
                    .getTransferData(DataFlavor.javaFileListFlavor);
            if (!(payload instanceof List)) {
              return false;
            }
            files = (List<File>) payload;
        } catch (UnsupportedFlavorException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
        
        Component comp = support.getComponent();
        if (comp instanceof JTextComponent) {
            return handleSingleDrop(files, (JTextComponent) comp);
        } else if (comp instanceof JList) {
            return handleMultiDrop(files, (JList<File>) comp);
        }
        return false;
    }
    
    private boolean handleSingleDrop(List<File> files, JTextComponent comp) {
        if (files.size() != 1) {
            return false;
        }
        comp.setText(files.get(0).getAbsolutePath());
        callback.droppedToTarget(comp);
        return true;
    }
    
    private boolean handleMultiDrop(List<File> files, JList<File> comp) {
        if (!(comp.getModel() instanceof DefaultListModel)) {
            return false;
        }
        DefaultListModel<File> model = (DefaultListModel<File>) comp.getModel();
        for (File file : files) {
            if (!model.contains(file)) {
                model.addElement(file);
            }
        }
        callback.droppedToTarget(comp);
        return true;
    }
}
