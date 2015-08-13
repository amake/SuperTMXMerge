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

import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import org.madlonkay.supertmxmerge.DiffController;
import org.madlonkay.supertmxmerge.DiffController.DiffInfo;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

    /**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class DiffWindow extends javax.swing.JPanel {
   
    public static JFrame newAsFrame(DiffController controller) {
        JFrame frame = new MenuFrame(LocString.get("STM_DIFF_WINDOW_TITLE"));
        frame.setContentPane(new DiffWindow(frame, controller));
        return frame;
    }
    
    private final Window window;
    
    /**
     * Creates new form DiffWindow
     * @param window
     * @param controller
     */
    public DiffWindow(Window window, DiffController controller) {
        
        this.window = window;
        
        this.controller = controller;
        initComponents();
        
        if (window instanceof MenuFrame) {
            saveAsButton.setVisible(false);
            ((MenuFrame) window).addFileMenuItem(saveAsMenuItem);
        }
        
        // Keep tooltips open. Via:
        // http://www.rgagnon.com/javadetails/java-0528.html
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        
        initContent();
    }
    
    private void initContent() {
        SwingWorker worker = new SwingWorker<List<DiffCell>, DiffCell>() {
            
            @Override
            protected List<DiffCell> doInBackground() throws Exception {
                List<DiffCell> result = new ArrayList<DiffCell>();
                
                List<DiffInfo> diffs = controller.getDiffInfos();
                int n = 1;
                for (DiffInfo info : diffs) {
                    DiffCell cell = new DiffCell(n, info);
                    result.add(cell);
                    publish(cell);
                    setProgress(100 * n / diffs.size());
                    n++;
                }
                return result;
            }

            @Override
            protected void process(List<DiffCell> chunks) {
                for (DiffCell cell : chunks) {
                    diffsPanel.add(cell);
                }
                diffsPanel.revalidate();
            }

            @Override
            protected void done() {
                buttonPanel.setVisible(false);
            }
        };
        
        worker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    jProgressBar1.setValue((Integer) evt.getNewValue());
                }
            }
        });
        
        worker.execute();
        
        // Beansbinding is broken now for some reason, so set this manually.
        file1Label.setText(controller.getTmx1().getName());
        file2Label.setText(controller.getTmx2().getName());
        file1TextUnits.setText(unitCountConverter.convert(controller.getTmx1().getSize()));
        file2TextUnits.setText(unitCountConverter.convert(controller.getTmx2().getSize()));
        file1Label.setToolTipText(MapToTextConverter.mapToHtml(controller.getTmx1().getMetadata()));
        file2Label.setToolTipText(MapToTextConverter.mapToHtml(controller.getTmx2().getMetadata()));
        saveAsButton.setEnabled(controller.canSaveDiff());
        
        GuiUtil.sizeForScreen(this);
    }
    
    private DiffController getController() {
        return controller;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        controller = getController();
        unitCountConverter = new LocStringConverter("STM_NUMBER_OF_UNITS", "STM_NUMBER_OF_UNITS_SINGULAR");
        changeCountConverter = new LocStringConverter("STM_NUMBER_OF_CHANGES", "STM_NUMBER_OF_CHANGES_SINGULAR");
        mapToTextConverter = new org.madlonkay.supertmxmerge.gui.MapToTextConverter();
        saveAsMenuItem = new javax.swing.JMenuItem();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        file1Label = new javax.swing.JLabel();
        file2Label = new javax.swing.JLabel();
        file1TextUnits = new javax.swing.JLabel();
        file2TextUnits = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        changeCountLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        diffsPanel = new org.madlonkay.supertmxmerge.gui.ReasonablySizedPanel();
        buttonPanel = new javax.swing.JPanel();
        saveAsButton = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();

        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText(LocString.get("STM_FILE_MENU_SAVEAS")); // NOI18N
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsActionPerformed(evt);
            }
        });

        setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("Button.disabledText")));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel2.setLayout(new java.awt.GridLayout(2, 2, 10, 0));

        file1Label.setFont(file1Label.getFont().deriveFont(file1Label.getFont().getStyle() | java.awt.Font.BOLD, file1Label.getFont().getSize()+2));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${tmx1.name}"), file1Label, org.jdesktop.beansbinding.BeanProperty.create("text"), "file1Name");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, controller, org.jdesktop.beansbinding.ELProperty.create("${tmx1.metadata}"), file1Label, org.jdesktop.beansbinding.BeanProperty.create("toolTipText"), "file1Metadata");
        binding.setSourceNullValue(LocString.get("STM_TMX_DETAILS_UNAVAILABLE")); // NOI18N
        binding.setConverter(mapToTextConverter);
        bindingGroup.addBinding(binding);

        jPanel2.add(file1Label);

        file2Label.setFont(file2Label.getFont().deriveFont(file2Label.getFont().getStyle() | java.awt.Font.BOLD, file2Label.getFont().getSize()+2));
        file2Label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${tmx2.name}"), file2Label, org.jdesktop.beansbinding.BeanProperty.create("text"), "file2Name");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${tmx2.metadata}"), file2Label, org.jdesktop.beansbinding.BeanProperty.create("toolTipText"), "tmx2Metadata");
        binding.setSourceNullValue(LocString.get("STM_TMX_DETAILS_UNAVAILABLE")); // NOI18N
        binding.setConverter(mapToTextConverter);
        bindingGroup.addBinding(binding);

        jPanel2.add(file2Label);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${tmx1.size}"), file1TextUnits, org.jdesktop.beansbinding.BeanProperty.create("text"), "file1UnitCount");
        binding.setConverter(unitCountConverter);
        bindingGroup.addBinding(binding);

        jPanel2.add(file1TextUnits);

        file2TextUnits.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${tmx2.size}"), file2TextUnits, org.jdesktop.beansbinding.BeanProperty.create("text"), "file2UnitCount");
        binding.setConverter(unitCountConverter);
        bindingGroup.addBinding(binding);

        jPanel2.add(file2TextUnits);

        jPanel3.add(jPanel2);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${changeCount}"), changeCountLabel, org.jdesktop.beansbinding.BeanProperty.create("text"), "changeCount");
        binding.setConverter(changeCountConverter);
        bindingGroup.addBinding(binding);

        jPanel4.add(changeCountLabel);

        jPanel3.add(jPanel4);

        add(jPanel3, java.awt.BorderLayout.NORTH);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        diffsPanel.setLayout(new javax.swing.BoxLayout(diffsPanel, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(diffsPanel);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        buttonPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("Button.disabledText")), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        buttonPanel.setLayout(new java.awt.BorderLayout());

        saveAsButton.setText(LocString.get("STM_SAVE_AS_BUTTON")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_ONCE, controller, org.jdesktop.beansbinding.ELProperty.create("${canSaveDiff}"), saveAsButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "canSaveDiff");
        bindingGroup.addBinding(binding);

        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsActionPerformed(evt);
            }
        });
        buttonPanel.add(saveAsButton, java.awt.BorderLayout.EAST);
        buttonPanel.add(jProgressBar1, java.awt.BorderLayout.CENTER);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void saveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsActionPerformed
        getController().saveAs();
    }//GEN-LAST:event_saveAsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private org.madlonkay.supertmxmerge.gui.LocStringConverter changeCountConverter;
    private javax.swing.JLabel changeCountLabel;
    private org.madlonkay.supertmxmerge.DiffController controller;
    private org.madlonkay.supertmxmerge.gui.ReasonablySizedPanel diffsPanel;
    private javax.swing.JLabel file1Label;
    private javax.swing.JLabel file1TextUnits;
    private javax.swing.JLabel file2Label;
    private javax.swing.JLabel file2TextUnits;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.madlonkay.supertmxmerge.gui.MapToTextConverter mapToTextConverter;
    private javax.swing.JButton saveAsButton;
    private javax.swing.JMenuItem saveAsMenuItem;
    private org.madlonkay.supertmxmerge.gui.LocStringConverter unitCountConverter;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
