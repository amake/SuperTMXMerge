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

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import org.madlonkay.supertmxmerge.MergeController;
import org.madlonkay.supertmxmerge.data.ConflictInfo;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

    /**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class MergeWindow extends javax.swing.JPanel {
    
    public static JFrame newAsFrame(final MergeController controller, boolean isTwoWayMerge) {
        MenuFrame frame = new MenuFrame(LocString.get("merge_window_title"));
        frame.setContentPane(new MergeWindow(frame, controller, isTwoWayMerge));
        frame.pack();
        return frame;
    }
    
    public static JDialog newAsDialog(final MergeController controller, boolean isTwoWayMerge) {
        final JDialog dialog = new JDialog((JFrame)null, LocString.get("merge_window_title"),
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.add(new MergeWindow(dialog, controller, isTwoWayMerge));
        return dialog;
    }

    private final Window window;
    private final ProgressWindow progress;
    
    private final List<JRadioButton> leftRadioButtons = new ArrayList<JRadioButton>();
    private final List<JRadioButton> rightRadioButtons = new ArrayList<JRadioButton>();
    private final List<JRadioButton> centerRadioButtons = new ArrayList<JRadioButton>();
    
    private boolean isTwoWayMerge = false;
        
    /**
     * Creates new form MergeWindow
     * @param window
     * @param controller
     * @param isTwoWayMerge
     */
    public MergeWindow(final Window window, final MergeController controller, boolean isTwoWayMerge) {
        progress = new ProgressWindow();

        this.window = window;
        window.setLocationByPlatform(true);
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                if (!controller.isConflictsAreResolved()) {
                    if (!controller.isCanCancel()) {
                        // Don't let user exit without resolving.
                        return;
                    }
                    int response = JOptionPane.showConfirmDialog(window,
                        LocString.get("confirm_close_message"),
                        LocString.get("merge_window_title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    if (response != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                window.dispose();
            }
        });
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        GuiUtil.closeWindow(progress);
                    }
                });
            }
        });
        
        this.controller = controller;
        this.isTwoWayMerge = isTwoWayMerge;
        initComponents();
        
        // Keep tooltips open. Via:
        // http://www.rgagnon.com/javadetails/java-0528.html
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        
        initContent();
        
        allBaseButton.setVisible(!isTwoWayMerge);
        centerFilename.setVisible(!isTwoWayMerge);
        centerTextUnits.setVisible(!isTwoWayMerge);
    }
    
    private void initContent() {
        List<ConflictInfo> conflicts = controller.getConflicts();
        progress.setMaximum(conflicts.size());     
        int n = 1;
        for (ConflictInfo info : conflicts) {
            progress.setValue(n);
            progress.setMessage(LocString.getFormat("merge_progress", n, conflicts.size()));
            addMergeInfo(n, info);
            n++;
        }
    }
    
    private void addMergeInfo(int itemNumber, ConflictInfo info) {
        MergeCell cell = new MergeCell(itemNumber, info, jScrollPane1);
        cell.setIsTwoWayMerge(isTwoWayMerge);
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
        controller.addSelection(info.key, buttons);
        conflictInfoPanel.add(cell);
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
        mapToTextConverter = new org.madlonkay.supertmxmerge.gui.MapToTextConverter();
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
        instructionsLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        allLeftButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        allBaseButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        allRightButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        conflictInfoPanel = new org.madlonkay.supertmxmerge.gui.ReasonablySizedPanel();
        jPanel3 = new javax.swing.JPanel();
        doneButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel2.setLayout(new java.awt.GridLayout(2, 3, 10, 0));

        leftFilename.setFont(leftFilename.getFont().deriveFont(leftFilename.getFont().getStyle() | java.awt.Font.BOLD, leftFilename.getFont().getSize()+2));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${leftTmx.name}"), leftFilename, org.jdesktop.beansbinding.BeanProperty.create("text"), "leftFileName");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${leftTmx.metadata}"), leftFilename, org.jdesktop.beansbinding.BeanProperty.create("toolTipText"), "leftTmxMetadata");
        binding.setSourceNullValue(LocString.get("tmx_details_unavailable")); // NOI18N
        binding.setConverter(mapToTextConverter);
        bindingGroup.addBinding(binding);

        jPanel2.add(leftFilename);

        centerFilename.setFont(centerFilename.getFont().deriveFont(centerFilename.getFont().getStyle() | java.awt.Font.BOLD, centerFilename.getFont().getSize()+2));
        centerFilename.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${baseTmx.name}"), centerFilename, org.jdesktop.beansbinding.BeanProperty.create("text"), "baseFileName");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${baseTmx.metadata}"), centerFilename, org.jdesktop.beansbinding.BeanProperty.create("toolTipText"), "centerTmxMetadata");
        binding.setSourceNullValue(LocString.get("tmx_details_unavailable")); // NOI18N
        binding.setConverter(mapToTextConverter);
        bindingGroup.addBinding(binding);

        jPanel2.add(centerFilename);

        rightFilename.setFont(rightFilename.getFont().deriveFont(rightFilename.getFont().getStyle() | java.awt.Font.BOLD, rightFilename.getFont().getSize()+2));
        rightFilename.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${rightTmx.name}"), rightFilename, org.jdesktop.beansbinding.BeanProperty.create("text"), "rightFileName");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${rightTmx.metadata}"), rightFilename, org.jdesktop.beansbinding.BeanProperty.create("toolTipText"), "rightTmxMetadata");
        binding.setSourceNullValue(LocString.get("tmx_details_unavailable")); // NOI18N
        binding.setConverter(mapToTextConverter);
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

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel6.setLayout(new java.awt.GridLayout(0, 1, 0, 4));

        conflictCountLabel.setForeground(new java.awt.Color(255, 0, 0));
        conflictCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        conflictCountLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${conflictCount}"), conflictCountLabel, org.jdesktop.beansbinding.BeanProperty.create("text"), "conflictCount");
        binding.setConverter(conflictCountConverter);
        bindingGroup.addBinding(binding);

        jPanel6.add(conflictCountLabel);

        instructionsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        instructionsLabel.setText(LocString.get("merge_window_directions")); // NOI18N
        jPanel6.add(instructionsLabel);

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

        add(jPanel4, java.awt.BorderLayout.NORTH);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        conflictInfoPanel.setLayout(new javax.swing.BoxLayout(conflictInfoPanel, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(conflictInfoPanel);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel3.setLayout(new java.awt.BorderLayout());

        doneButton.setText(LocString.get("done_button")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${conflictsAreResolved}"), doneButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "conflictsResolved");
        bindingGroup.addBinding(binding);

        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });
        jPanel3.add(doneButton, java.awt.BorderLayout.EAST);

        add(jPanel3, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void useAllLeft(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useAllLeft
        activateAllButtons(leftRadioButtons);
    }//GEN-LAST:event_useAllLeft

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed
        getToolkit().getSystemEventQueue().postEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_doneButtonActionPerformed

    private void useAllBase(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useAllBase
        activateAllButtons(centerRadioButtons);
    }//GEN-LAST:event_useAllBase

    private void useAllRight(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useAllRight
        activateAllButtons(rightRadioButtons);
    }//GEN-LAST:event_useAllRight

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allBaseButton;
    private javax.swing.JButton allLeftButton;
    private javax.swing.JButton allRightButton;
    private javax.swing.JLabel centerFilename;
    private javax.swing.JLabel centerTextUnits;
    private org.madlonkay.supertmxmerge.gui.LocStringConverter conflictCountConverter;
    private javax.swing.JLabel conflictCountLabel;
    private org.madlonkay.supertmxmerge.gui.ReasonablySizedPanel conflictInfoPanel;
    private org.madlonkay.supertmxmerge.MergeController controller;
    private javax.swing.JButton doneButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel instructionsLabel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel leftFilename;
    private javax.swing.JLabel leftTextUnits;
    private org.madlonkay.supertmxmerge.gui.MapToTextConverter mapToTextConverter;
    private javax.swing.JLabel rightFilename;
    private javax.swing.JLabel rightTextUnits;
    private org.madlonkay.supertmxmerge.gui.SaveButtonConverter saveButtonConverter;
    private org.madlonkay.supertmxmerge.gui.LocStringConverter unitCountConverter;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
