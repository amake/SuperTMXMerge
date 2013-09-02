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
package org.madlonkay.supertmxmerge.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import org.madlonkay.supertmxmerge.MergeController;
import org.madlonkay.supertmxmerge.data.MergeInfo;
import org.madlonkay.supertmxmerge.util.FileUtil;
import org.madlonkay.supertmxmerge.util.LocString;

    /**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class MergeWindow extends javax.swing.JFrame {

    private List<JRadioButton> leftRadioButtons = new ArrayList<JRadioButton>();
    private List<JRadioButton> rightRadioButtons = new ArrayList<JRadioButton>();
    private List<JRadioButton> centerRadioButtons = new ArrayList<JRadioButton>();
    
    /**
     * Creates new form MergeWindow
     */
    public MergeWindow(MergeController controller) {
        this.controller = controller;
        initComponents();
        
        int n = 1;
        for (MergeInfo info : controller.getMergeInfos()) {
            addMergeInfo(n, info);
            n++;
        }
    }
    
    private void addMergeInfo(int itemNumber, MergeInfo info) {
        MergeCell cell = new MergeCell(itemNumber, info);
        JRadioButton[] buttons = {
                cell.getLeftButton(),
                cell.getCenterButton(),
                cell.getRightButton()
            };
        leftRadioButtons.add(buttons[0]);
        centerRadioButtons.add(buttons[1]);
        rightRadioButtons.add(buttons[2]);
        for (JRadioButton button : buttons) {
            button.addActionListener(controller);
        }
        controller.addSelection(info.sourceText, buttons);
        mergeInfoPanel.add(cell);
    }
    
    private void activateAllButtons(List<JRadioButton> buttons) {
        for (JRadioButton b : buttons) {
            b.setSelected(true);
        }
        controller.actionPerformed(null);
    }
    
    private MergeController getController() {
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
        saveButtonConverter = new org.madlonkay.supertmxmerge.gui.SaveButtonConverter();
        conflictCountConverter = new LocStringConverter("number_of_conflicts", "number_of_conflicts_singular");
        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        leftFilename = new javax.swing.JLabel();
        centerFilename = new javax.swing.JLabel();
        rightFilename = new javax.swing.JLabel();
        leftTextUnits = new javax.swing.JLabel();
        centerTextUnits = new javax.swing.JLabel();
        rightTextUnits = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        conflictCountLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        allLeftButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        allBaseButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        allRightButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        mergeInfoPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        discardButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(LocString.get("merge_window_title")); // NOI18N

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel2.setLayout(new java.awt.GridLayout(2, 3));

        leftFilename.setToolTipText("");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${leftTmx.fileName}"), leftFilename, org.jdesktop.beansbinding.BeanProperty.create("text"), "leftFileName");
        bindingGroup.addBinding(binding);

        jPanel2.add(leftFilename);

        centerFilename.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${baseTmx.fileName}"), centerFilename, org.jdesktop.beansbinding.BeanProperty.create("text"), "baseFileName");
        bindingGroup.addBinding(binding);

        jPanel2.add(centerFilename);

        rightFilename.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${rightTmx.fileName}"), rightFilename, org.jdesktop.beansbinding.BeanProperty.create("text"), "rightFileName");
        bindingGroup.addBinding(binding);

        jPanel2.add(rightFilename);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${leftTmx.size}"), leftTextUnits, org.jdesktop.beansbinding.BeanProperty.create("text"), "leftFileUnitCount");
        binding.setConverter(unitCountConverter);
        bindingGroup.addBinding(binding);

        jPanel2.add(leftTextUnits);

        centerTextUnits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${baseTmx.size}"), centerTextUnits, org.jdesktop.beansbinding.BeanProperty.create("text"), "baseFileUnitCount");
        binding.setConverter(unitCountConverter);
        bindingGroup.addBinding(binding);

        jPanel2.add(centerTextUnits);

        rightTextUnits.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${rightTmx.size}"), rightTextUnits, org.jdesktop.beansbinding.BeanProperty.create("text"), "rightFileUnitCount");
        binding.setConverter(unitCountConverter);
        bindingGroup.addBinding(binding);

        jPanel2.add(rightTextUnits);

        jPanel4.add(jPanel2);

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        conflictCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        conflictCountLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${conflictCount}"), conflictCountLabel, org.jdesktop.beansbinding.BeanProperty.create("text"), "conflictCount");
        binding.setConverter(conflictCountConverter);
        bindingGroup.addBinding(binding);

        jPanel6.add(conflictCountLabel);

        jPanel4.add(jPanel6);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        allLeftButton.setText(LocString.get("choose_all_left")); // NOI18N
        allLeftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useAllLeft(evt);
            }
        });
        jPanel5.add(allLeftButton);
        jPanel5.add(filler1);

        allBaseButton.setText(LocString.get("choose_all_center")); // NOI18N
        allBaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useAllBase(evt);
            }
        });
        jPanel5.add(allBaseButton);
        jPanel5.add(filler2);

        allRightButton.setText(LocString.get("choose_all_right")); // NOI18N
        allRightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useAllRight(evt);
            }
        });
        jPanel5.add(allRightButton);

        jPanel4.add(jPanel5);

        getContentPane().add(jPanel4, java.awt.BorderLayout.NORTH);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        mergeInfoPanel.setLayout(new javax.swing.BoxLayout(mergeInfoPanel, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(mergeInfoPanel);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, controller, org.jdesktop.beansbinding.ELProperty.create("${outputIsValid}"), saveButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${outputFile}"), saveButton, org.jdesktop.beansbinding.BeanProperty.create("text"), "saveButton");
        binding.setConverter(saveButtonConverter);
        bindingGroup.addBinding(binding);

        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jPanel1.add(saveButton);

        discardButton.setText(LocString.get("discard_button")); // NOI18N
        discardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discardButtonActionPerformed(evt);
            }
        });
        jPanel1.add(discardButton);

        jPanel3.add(jPanel1, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void useAllLeft(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useAllLeft
        activateAllButtons(leftRadioButtons);
    }//GEN-LAST:event_useAllLeft

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (!FileUtil.validateFile(controller.getOutputFile())) {
            if (jFileChooser1.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String file = jFileChooser1.getSelectedFile().getCanonicalPath();
                    controller.setOutputFile(file);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                return;
            }
        }
        controller.resolve();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void useAllBase(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useAllBase
        activateAllButtons(centerRadioButtons);
    }//GEN-LAST:event_useAllBase

    private void useAllRight(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useAllRight
        activateAllButtons(rightRadioButtons);
    }//GEN-LAST:event_useAllRight

    private void discardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardButtonActionPerformed
        dispose();
        System.exit(0);
    }//GEN-LAST:event_discardButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allBaseButton;
    private javax.swing.JButton allLeftButton;
    private javax.swing.JButton allRightButton;
    private javax.swing.JLabel centerFilename;
    private javax.swing.JLabel centerTextUnits;
    private org.madlonkay.supertmxmerge.gui.LocStringConverter conflictCountConverter;
    private javax.swing.JLabel conflictCountLabel;
    private org.madlonkay.supertmxmerge.MergeController controller;
    private javax.swing.JButton discardButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel leftFilename;
    private javax.swing.JLabel leftTextUnits;
    private javax.swing.JPanel mergeInfoPanel;
    private javax.swing.JLabel rightFilename;
    private javax.swing.JLabel rightTextUnits;
    private javax.swing.JButton saveButton;
    private org.madlonkay.supertmxmerge.gui.SaveButtonConverter saveButtonConverter;
    private org.madlonkay.supertmxmerge.gui.LocStringConverter unitCountConverter;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
