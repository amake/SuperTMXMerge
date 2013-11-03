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

import java.util.List;
import javax.swing.ToolTipManager;
import org.madlonkay.supertmxmerge.DiffController;
import org.madlonkay.supertmxmerge.data.DiffInfo;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

    /**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class DiffWindow extends javax.swing.JFrame {
   
    private final ProgressWindow progress;
    
    /**
     * Creates new form DiffWindow
     */
    public DiffWindow(DiffController controller) {
        progress = new ProgressWindow();
        
        this.controller = controller;
        initComponents();
        
        // Keep tooltips open. Via:
        // http://www.rgagnon.com/javadetails/java-0528.html
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        
        initContent();
    }
    
    private void initContent() {
        List<DiffInfo> infos = controller.getDiffInfos();
        progress.setMaximum(infos.size());
        int n = 1;
        for (DiffInfo info : infos) {
            progress.setValue(n);
            progress.setMessage(LocString.getFormat("diff_progress", n, infos.size()));
            diffsPanel.add(new DiffCell(n, info));
            n++;
        }
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
        unitCountConverter = new LocStringConverter("number_of_units", "number_of_units_singular");
        changeCountConverter = new LocStringConverter("number_of_changes", "number_of_changes_singular");
        mapToTextConverter = new org.madlonkay.supertmxmerge.gui.MapToTextConverter();
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
        jPanel1 = new javax.swing.JPanel();
        saveAsButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(LocString.get("diff_window_title")); // NOI18N
        setLocationByPlatform(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel2.setLayout(new java.awt.GridLayout(2, 2, 10, 0));

        file1Label.setFont(file1Label.getFont().deriveFont(file1Label.getFont().getStyle() | java.awt.Font.BOLD, file1Label.getFont().getSize()+2));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${tmx1.name}"), file1Label, org.jdesktop.beansbinding.BeanProperty.create("text"), "file1Name");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, controller, org.jdesktop.beansbinding.ELProperty.create("${tmx1.metadata}"), file1Label, org.jdesktop.beansbinding.BeanProperty.create("toolTipText"), "file1Metadata");
        binding.setSourceNullValue(LocString.get("tmx_details_unavailable")); // NOI18N
        binding.setConverter(mapToTextConverter);
        bindingGroup.addBinding(binding);

        jPanel2.add(file1Label);

        file2Label.setFont(file2Label.getFont().deriveFont(file2Label.getFont().getStyle() | java.awt.Font.BOLD, file2Label.getFont().getSize()+2));
        file2Label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${tmx2.name}"), file2Label, org.jdesktop.beansbinding.BeanProperty.create("text"), "file2Name");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${tmx2.metadata}"), file2Label, org.jdesktop.beansbinding.BeanProperty.create("toolTipText"), "tmx2Metadata");
        binding.setSourceNullValue(LocString.get("tmx_details_unavailable")); // NOI18N
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

        getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        diffsPanel.setLayout(new javax.swing.BoxLayout(diffsPanel, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(diffsPanel);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        saveAsButton.setText(LocString.get("save_as_button")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_ONCE, controller, org.jdesktop.beansbinding.ELProperty.create("${canSaveDiff}"), saveAsButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "canSaveDiff");
        bindingGroup.addBinding(binding);

        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsButtonActionPerformed(evt);
            }
        });
        jPanel1.add(saveAsButton, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        GuiUtil.closeWindow(progress);
    }//GEN-LAST:event_formComponentShown

    private void saveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
        getController().saveAs();
    }//GEN-LAST:event_saveAsButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.madlonkay.supertmxmerge.gui.LocStringConverter changeCountConverter;
    private javax.swing.JLabel changeCountLabel;
    private org.madlonkay.supertmxmerge.DiffController controller;
    private org.madlonkay.supertmxmerge.gui.ReasonablySizedPanel diffsPanel;
    private javax.swing.JLabel file1Label;
    private javax.swing.JLabel file1TextUnits;
    private javax.swing.JLabel file2Label;
    private javax.swing.JLabel file2TextUnits;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private org.madlonkay.supertmxmerge.gui.MapToTextConverter mapToTextConverter;
    private javax.swing.JButton saveAsButton;
    private org.madlonkay.supertmxmerge.gui.LocStringConverter unitCountConverter;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
