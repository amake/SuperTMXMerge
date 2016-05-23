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
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;

import org.madlonkay.supertmxmerge.MergeController;
import org.madlonkay.supertmxmerge.MergeController.ConflictInfo;
import org.madlonkay.supertmxmerge.data.Key;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

    /**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
@SuppressWarnings("serial")
public class MergeWindow extends javax.swing.JPanel {
    
    public static JFrame newAsFrame(final MergeController controller, boolean isTwoWayMerge) {
        MenuFrame frame = new MenuFrame(LocString.get("STM_MERGE_WINDOW_TITLE"));
        frame.setContentPane(new MergeWindow(frame, controller, isTwoWayMerge));
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        return frame;
    }
    
    public static JDialog newAsDialog(final MergeController controller, boolean isTwoWayMerge, Window parentWindow) {
        final JDialog dialog = new JDialog(parentWindow, LocString.get("STM_MERGE_WINDOW_TITLE"),
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.add(new MergeWindow(dialog, controller, isTwoWayMerge));
        return dialog;
    }

    private final Window window;
    
    private final List<AbstractButton> leftRadioButtons = new ArrayList<AbstractButton>();
    private final List<AbstractButton> rightRadioButtons = new ArrayList<AbstractButton>();
    private final List<AbstractButton> centerRadioButtons = new ArrayList<AbstractButton>();
    
    private boolean isTwoWayMerge = false;
    private boolean isDetailMode = false;
    private List<ConflictInfo> conflicts = null;
    private List<MergeCell> detailModeCells = new ArrayList<MergeCell>();
    private int detailModeIndex = -1;
    
    /**
     * Creates new form MergeWindow
     * @param window
     * @param controller
     * @param isTwoWayMerge
     */
    public MergeWindow(final Window window, final MergeController controller, boolean isTwoWayMerge) {
        initComponents();
        
        this.window = window;
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                if (!controller.isConflictsAreResolved()) {
                    if (!controller.isCanCancel()) {
                        // Don't let user exit without resolving.
                        return;
                    }
                    int response = JOptionPane.showConfirmDialog(window,
                        LocString.get("STM_CONFIRM_CLOSE_MESSAGE"),
                        LocString.get("STM_MERGE_WINDOW_TITLE"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    if (response != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                window.dispose();
            }
        });
        
        this.controller = controller;
        this.isTwoWayMerge = isTwoWayMerge;
        
        // Keep tooltips open. Via:
        // http://www.rgagnon.com/javadetails/java-0528.html
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        
        this.conflicts = controller.getConflicts();
        this.isDetailMode = conflicts.size() <= controller.getListViewThreshold();
        
        initContent();
    }
    
    private void initContent() {
        detailModeCells.clear();
        detailModeIndex = -1;
        
        centerRadioButtons.clear();
        leftRadioButtons.clear();
        rightRadioButtons.clear();
        
        Map<Key, AbstractButton[]> presets = controller.getSelections();
        controller.clearSelections();
        
        conflictInfoPanel.removeAll();
        
        jProgressBar1.setVisible(true);
        jProgressBar1.setValue(0);
        
        if (isDetailMode) {
            conflictInfoPanel.setLayout(new GridLayout(1, 1));
            currentConflict.setVisible(true);
            modeSwtichButton.setText(LocString.get("STM_LIST_VIEW_BUTTON"));
        } else {
            conflictInfoPanel.setLayout(new BoxLayout(conflictInfoPanel, BoxLayout.PAGE_AXIS));
            nextButton.setVisible(false);
            backButton.setVisible(false);
            doneButton.setVisible(true);
            doneButton.setEnabled(controller.isConflictsAreResolved());
            currentConflict.setVisible(false);
            modeSwtichButton.setText(LocString.get("STM_DETAIL_VIEW_BUTTON"));
        }
        
        new InitWorker(presets).execute();
        
        // Beansbinding is broken now for some reason, so set this manually.
        leftFilename.setText(controller.getLeftTmx().getName());
        centerFilename.setText(controller.getBaseTmx().getName());
        rightFilename.setText(controller.getRightTmx().getName());
        leftTextUnits.setText(unitCountConverter.convert(controller.getLeftTmx().getSize()));
        centerTextUnits.setText(unitCountConverter.convert(controller.getBaseTmx().getSize()));
        rightTextUnits.setText(unitCountConverter.convert(controller.getRightTmx().getSize()));
        leftFilename.setToolTipText(MapToTextConverter.mapToHtml(controller.getLeftTmx().getMetadata()));
        centerFilename.setToolTipText(MapToTextConverter.mapToHtml(controller.getBaseTmx().getMetadata()));
        rightFilename.setToolTipText(MapToTextConverter.mapToHtml(controller.getRightTmx().getMetadata()));
        conflictCountLabel.setText(conflictCountConverter.convert(controller.getConflictCount()));
        
        allBaseButton.setVisible(!isTwoWayMerge);
        centerFilename.setVisible(!isTwoWayMerge);
        centerTextUnits.setVisible(!isTwoWayMerge);
        
        GuiUtil.sizeForScreen(this);
    }
    
    private void addMergeCell(MergeCell cell) {
        if (isDetailMode) {
            detailModeCells.add(cell);
            if (detailModeIndex < 0) {
                nextButtonActionPerformed(null);
            }
        } else {
            conflictInfoPanel.add(cell);
        }
    }
    
    private void swapCells(int before, int after) {
        if (before == after) {
            return;
        }
        if (before > -1 && before < conflicts.size()) {
            MergeCell current = detailModeCells.get(before);
            conflictInfoPanel.remove(current);
        }
        if (after > -1 && after < conflicts.size()) {
            MergeCell newCell = detailModeCells.get(after);
            conflictInfoPanel.add(newCell);
            newCell.validate();
            newCell.repaint();
        }
    }
    
    private void updateDetailModeState() {
        currentConflict.setText(LocString.getFormat("STM_DETAIL_VIEW_LOCATION", detailModeIndex + 1, conflicts.size()));
        backButton.setVisible(conflicts.size() > 1);
        backButton.setEnabled(detailModeIndex != 0);
        nextButton.setVisible(conflicts.size() > 1 && detailModeIndex < conflicts.size() - 1);
        nextButton.setEnabled(detailModeCells.get(detailModeIndex).isSelectionPerformed());
        doneButton.setVisible(detailModeIndex == conflicts.size() - 1);
        doneButton.setEnabled(controller.isConflictsAreResolved());
    }
    
    private void activateAllButtons(List<AbstractButton> buttons) {
        for (AbstractButton b : buttons) {
            b.setSelected(true);
        }
        controller.actionPerformed(null);
        doneButton.setEnabled(controller.isConflictsAreResolved());
        if (isDetailMode) {
            updateDetailModeState();
        }
    }
    
    private MergeController getController() {
        return controller;
    }
    
    private ActionListener mergeSelectionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            nextButton.setEnabled(true);
            doneButton.setEnabled(controller.isConflictsAreResolved());
        }
    };
    
    private class InitWorker extends SwingWorker<List<MergeCell>, MergeCell> {
        
        private final Map<Key, AbstractButton[]> presets;

        public InitWorker(Map<Key, AbstractButton[]> presets) {
            this.presets = presets;
            addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("progress".equals(evt.getPropertyName())) {
                        jProgressBar1.setValue((Integer) evt.getNewValue());
                    }
                }
            });
        }
        
        @Override
        protected List<MergeCell> doInBackground() throws Exception {
            List<MergeCell> result = new ArrayList<MergeCell>();

            int n = 1;
            for (ConflictInfo info : conflicts) {
                MergeCell cell = new MergeCell(n, info, isDetailMode);
                cell.setIsTwoWayMerge(isTwoWayMerge);
                result.add(cell);
                publish(cell);
                setProgress(100 * n / conflicts.size());
                n++;
            }
            return result;
        }
        
        @Override
        protected void process(List<MergeCell> chunks) {
            for (MergeCell cell : chunks) {
                AbstractButton[] buttons = cell.getButtons();
                leftRadioButtons.add(buttons[0]);
                centerRadioButtons.add(buttons[1]);
                rightRadioButtons.add(buttons[2]);
                if (presets.size() > 0) {
                    AbstractButton[] preset = presets.get(cell.getKey());
                    if (preset != null) {
                        for (int i = 0; i < preset.length; i++) {
                            if (preset[i].isSelected()) {
                                buttons[i].setSelected(true);
                                break;
                            }
                        }
                    }
                }
                for (AbstractButton button : buttons) {
                    button.addActionListener(mergeSelectionListener);
                }
                controller.addSelection(cell.getKey(), buttons);
                addMergeCell(cell);
            }
            conflictInfoPanel.revalidate();
        }
        
        @Override
        protected void done() {
            jProgressBar1.setVisible(false);
            doneButton.setEnabled(controller.isConflictsAreResolved());
        }
    };
    
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
        saveButtonConverter = new org.madlonkay.supertmxmerge.gui.SaveButtonConverter();
        conflictCountConverter = new LocStringConverter("STM_NUMBER_OF_CONFLICTS", "STM_NUMBER_OF_CONFLICTS_SINGULAR");
        mapToTextConverter = new org.madlonkay.supertmxmerge.gui.MapToTextConverter();
        topPanel = new javax.swing.JPanel();
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
        bottomPanel = new javax.swing.JPanel();
        modeSwtichButton = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        bottomButtonsPanel = new javax.swing.JPanel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0));
        currentConflict = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0));
        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        doneButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel2.setLayout(new java.awt.GridLayout(2, 3, 10, 0));

        leftFilename.setFont(leftFilename.getFont().deriveFont(leftFilename.getFont().getStyle() | java.awt.Font.BOLD, leftFilename.getFont().getSize()+2));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${leftTmx.name}"), leftFilename, org.jdesktop.beansbinding.BeanProperty.create("text"), "leftFileName");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${leftTmx.metadata}"), leftFilename, org.jdesktop.beansbinding.BeanProperty.create("toolTipText"), "leftTmxMetadata");
        binding.setSourceNullValue(LocString.get("STM_TMX_DETAILS_UNAVAILABLE")); // NOI18N
        binding.setConverter(mapToTextConverter);
        bindingGroup.addBinding(binding);

        jPanel2.add(leftFilename);

        centerFilename.setFont(centerFilename.getFont().deriveFont(centerFilename.getFont().getStyle() | java.awt.Font.BOLD, centerFilename.getFont().getSize()+2));
        centerFilename.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${baseTmx.name}"), centerFilename, org.jdesktop.beansbinding.BeanProperty.create("text"), "baseFileName");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${baseTmx.metadata}"), centerFilename, org.jdesktop.beansbinding.BeanProperty.create("toolTipText"), "centerTmxMetadata");
        binding.setSourceNullValue(LocString.get("STM_TMX_DETAILS_UNAVAILABLE")); // NOI18N
        binding.setConverter(mapToTextConverter);
        bindingGroup.addBinding(binding);

        jPanel2.add(centerFilename);

        rightFilename.setFont(rightFilename.getFont().deriveFont(rightFilename.getFont().getStyle() | java.awt.Font.BOLD, rightFilename.getFont().getSize()+2));
        rightFilename.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${rightTmx.name}"), rightFilename, org.jdesktop.beansbinding.BeanProperty.create("text"), "rightFileName");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${rightTmx.metadata}"), rightFilename, org.jdesktop.beansbinding.BeanProperty.create("toolTipText"), "rightTmxMetadata");
        binding.setSourceNullValue(LocString.get("STM_TMX_DETAILS_UNAVAILABLE")); // NOI18N
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

        topPanel.add(jPanel2);

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
        instructionsLabel.setText(LocString.get("STM_MERGE_WINDOW_DIRECTIONS")); // NOI18N
        jPanel6.add(instructionsLabel);

        topPanel.add(jPanel6);

        jPanel5.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.SystemColor.controlShadow), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        allLeftButton.setText(LocString.get("STM_USE_ALL_MINE_BUTTON")); // NOI18N
        allLeftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useAllLeft(evt);
            }
        });
        jPanel5.add(allLeftButton);
        jPanel5.add(filler1);

        allBaseButton.setText(LocString.get("STM_USE_ALL_BASE_BUTTON")); // NOI18N
        allBaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useAllBase(evt);
            }
        });
        jPanel5.add(allBaseButton);
        jPanel5.add(filler2);

        allRightButton.setText(LocString.get("STM_USE_ALL_THEIRS_BUTTON")); // NOI18N
        allRightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useAllRight(evt);
            }
        });
        jPanel5.add(allRightButton);

        topPanel.add(jPanel5);

        add(topPanel, java.awt.BorderLayout.NORTH);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        conflictInfoPanel.setLayout(null);
        jScrollPane1.setViewportView(conflictInfoPanel);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        bottomPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, java.awt.SystemColor.controlShadow), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        bottomPanel.setLayout(new java.awt.BorderLayout());

        modeSwtichButton.setText(LocString.get("STM_LIST_VIEW_BUTTON")); // NOI18N
        modeSwtichButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeSwtichButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(modeSwtichButton, java.awt.BorderLayout.WEST);
        bottomPanel.add(jProgressBar1, java.awt.BorderLayout.CENTER);

        bottomButtonsPanel.setLayout(new javax.swing.BoxLayout(bottomButtonsPanel, javax.swing.BoxLayout.LINE_AXIS));
        bottomButtonsPanel.add(filler4);
        bottomButtonsPanel.add(currentConflict);
        bottomButtonsPanel.add(filler3);

        backButton.setText(LocString.get("STM_BACK_BUTTON")); // NOI18N
        backButton.setEnabled(false);
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        bottomButtonsPanel.add(backButton);

        nextButton.setText(LocString.get("STM_NEXT_BUTTON")); // NOI18N
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        bottomButtonsPanel.add(nextButton);

        doneButton.setText(LocString.get("STM_DONE_BUTTON")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, controller, org.jdesktop.beansbinding.ELProperty.create("${conflictsAreResolved}"), doneButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "conflictsResolved");
        bindingGroup.addBinding(binding);

        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });
        bottomButtonsPanel.add(doneButton);

        bottomPanel.add(bottomButtonsPanel, java.awt.BorderLayout.EAST);

        add(bottomPanel, java.awt.BorderLayout.SOUTH);

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

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        int before = detailModeIndex;
        detailModeIndex = Math.min(detailModeCells.size() - 1, detailModeIndex + 1);
        swapCells(before, detailModeIndex);
        updateDetailModeState();
    }//GEN-LAST:event_nextButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        int before = detailModeIndex;
        detailModeIndex = Math.max(0, detailModeIndex - 1);
        swapCells(before, detailModeIndex);
        updateDetailModeState();
    }//GEN-LAST:event_backButtonActionPerformed

    private void modeSwtichButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeSwtichButtonActionPerformed
        if (isDetailMode) {
            swapCells(detailModeIndex, -1);
        }
        isDetailMode = !isDetailMode;
        initContent();
        validate();
        repaint();
    }//GEN-LAST:event_modeSwtichButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allBaseButton;
    private javax.swing.JButton allLeftButton;
    private javax.swing.JButton allRightButton;
    private javax.swing.JButton backButton;
    private javax.swing.JPanel bottomButtonsPanel;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JLabel centerFilename;
    private javax.swing.JLabel centerTextUnits;
    private org.madlonkay.supertmxmerge.gui.LocStringConverter conflictCountConverter;
    private javax.swing.JLabel conflictCountLabel;
    private org.madlonkay.supertmxmerge.gui.ReasonablySizedPanel conflictInfoPanel;
    private org.madlonkay.supertmxmerge.MergeController controller;
    private javax.swing.JLabel currentConflict;
    private javax.swing.JButton doneButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JLabel instructionsLabel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel leftFilename;
    private javax.swing.JLabel leftTextUnits;
    private org.madlonkay.supertmxmerge.gui.MapToTextConverter mapToTextConverter;
    private javax.swing.JButton modeSwtichButton;
    private javax.swing.JButton nextButton;
    private javax.swing.JLabel rightFilename;
    private javax.swing.JLabel rightTextUnits;
    private org.madlonkay.supertmxmerge.gui.SaveButtonConverter saveButtonConverter;
    private javax.swing.JPanel topPanel;
    private org.madlonkay.supertmxmerge.gui.LocStringConverter unitCountConverter;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
