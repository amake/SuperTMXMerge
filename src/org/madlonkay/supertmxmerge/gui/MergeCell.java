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

import java.awt.Color;
import java.util.Map;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import org.madlonkay.supertmxmerge.MergeController.ConflictInfo;
import org.madlonkay.supertmxmerge.data.Key;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class MergeCell extends javax.swing.JPanel {

    private final static Border BORDER_DEFAULT;
    private final static Border BORDER_SELECTED;
    
    private final static MapToTextConverter CONVERTER = new MapToTextConverter();
    
    private final JScrollPane scrollTarget;
    private final Key key;
    
    static {
        JScrollPane sp = new JScrollPane();
        BORDER_DEFAULT = sp.getBorder();
        BORDER_SELECTED = new MatteBorder(2, 2, 2, 2, Color.BLUE);
    }
    
    /**
     * Creates new form TUDiffCell
     * @param itemNumber
     * @param info
     * @param scrollTarget
     */
    public MergeCell(int itemNumber, ConflictInfo info, JScrollPane scrollTarget) {
        initComponents();
        
        this.scrollTarget = scrollTarget;
        
        this.key = info.key;
        itemNumberLabel.setText(String.valueOf(itemNumber));
        sourceText.setText(info.key.sourceText);
        if (info.key.props != null) {
            setToolTipText((String) CONVERTER.convertForward(info.key.props));
        }
        setSourceLanguage(info.sourceLanguage);
        setTargetLanguage(info.targetLanguage);
        setBaseText(info.baseTuvText, info.baseTuvProps);
        setLeftText(info.leftTuvText, info.leftTuvProps, info.baseTuvText != null);
        setRightText(info.rightTuvText, info.rightTuvProps, info.baseTuvText != null);
        tuvTextLeft.addMouseListener(new ClickForwarder(leftButton));
        tuvTextRight.addMouseListener(new ClickForwarder(rightButton));
        tuvTextCenter.addMouseListener(new ClickForwarder(centerButton));
        info.leftTuvDiff.applyStyling(tuvTextCenter, tuvTextLeft);
        info.rightTuvDiff.applyStyling(tuvTextCenter, tuvTextRight, true);
        if (info.twoWayDiff != null) {
            info.twoWayDiff.applyStyling(tuvTextLeft, tuvTextRight);
        }
    }
    
    private void setSourceLanguage(String language) {
        TitledBorder sourceBorder = (TitledBorder) jPanel2.getBorder();
        sourceBorder.setTitle(language);
    }
    
    private void setTargetLanguage(String language) {
        TitledBorder targetBorder = (TitledBorder) jPanel1.getBorder();
        targetBorder.setTitle(language);
    }
    
    private void setBaseText(String text, Map<String, String> props) {
        if (text == null) {
            tuvTextCenter.setBackground(getBackground());
            text = LocString.get("STM_TUV_NOT_PRESENT");
        }
        if (props != null) {
            tuvTextCenter.setToolTipText((String) CONVERTER.convertForward(props));
        }
        tuvTextCenter.setText(text);
    }
    
    private void setLeftText(String text, Map<String, String> props, boolean presentInBase) {
        if (text == null) {
            tuvTextLeft.setBackground(getBackground());
            text = presentInBase? LocString.get("STM_TUV_DELETED") : LocString.get("STM_TUV_NOT_PRESENT");
        }
        if (props != null) {
            tuvTextLeft.setToolTipText((String) CONVERTER.convertForward(props));
        }
        tuvTextLeft.setText(text);
    }
    
    private void setRightText(String text, Map<String, String> props, boolean presentInBase) {
        if (text == null) {
            tuvTextRight.setBackground(getBackground());
            text = presentInBase? LocString.get("STM_TUV_DELETED") : LocString.get("STM_TUV_NOT_PRESENT");
        }
        if (props != null) {
            tuvTextRight.setToolTipText((String) CONVERTER.convertForward(props));
        }
        tuvTextRight.setText(text);
    }
    
    public JRadioButton getLeftButton() {
        return leftButton;
    }
    
    public JRadioButton getRightButton() {
        return rightButton;
    }
    
    public JRadioButton getCenterButton() {
        return centerButton;
    }
    
    public Key getKey() {
        return key;
    }
    
    public void setIsTwoWayMerge(boolean isTwoWayMerge) {
        if (isTwoWayMerge) {
            jPanel1.remove(jScrollPane2);
        } else {
            jPanel1.add(jScrollPane2, 1);
        }
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

        buttonGroup = new javax.swing.ButtonGroup();
        borderConverter1 = new BorderConverter(BORDER_SELECTED, BORDER_DEFAULT);
        leftButton = new javax.swing.JRadioButton();
        centerButton = new javax.swing.JRadioButton();
        rightButton = new javax.swing.JRadioButton();
        itemNumberLabel = new javax.swing.JLabel();
        sourceTargetPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        sourceScrollPane = new javax.swing.JScrollPane();
        sourceText = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tuvTextLeft = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        tuvTextCenter = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        tuvTextRight = new javax.swing.JTextPane();

        buttonGroup.add(leftButton);
        leftButton.setContentAreaFilled(false);
        leftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftButtonActionPerformed(evt);
            }
        });

        buttonGroup.add(centerButton);
        centerButton.setContentAreaFilled(false);
        centerButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        buttonGroup.add(rightButton);
        rightButton.setContentAreaFilled(false);

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new java.awt.BorderLayout());

        itemNumberLabel.setText("n");
        itemNumberLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        add(itemNumberLabel, java.awt.BorderLayout.WEST);

        sourceTargetPanel.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Source Language"));
        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        sourceScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sourceScrollPane.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                forwardScrollEvent(evt);
            }
        });

        sourceText.setEditable(false);
        sourceText.setColumns(72);
        sourceText.setFont(UIManager.getDefaults().getFont("Label.font"));
        sourceText.setLineWrap(true);
        sourceText.setText("Source text");
        sourceText.setWrapStyleWord(true);
        sourceScrollPane.setViewportView(sourceText);

        jPanel2.add(sourceScrollPane);

        sourceTargetPanel.add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Target Language"));
        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, leftButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jScrollPane1, org.jdesktop.beansbinding.BeanProperty.create("border"), "tuvTextLeftBorder");
        binding.setConverter(borderConverter1);
        bindingGroup.addBinding(binding);

        jScrollPane1.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                forwardScrollEvent(evt);
            }
        });

        tuvTextLeft.setEditable(false);
        tuvTextLeft.setFont(UIManager.getDefaults().getFont("Label.font"));
        tuvTextLeft.setText("Target text 1");
        jScrollPane1.setViewportView(tuvTextLeft);

        jPanel1.add(jScrollPane1);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, centerButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jScrollPane2, org.jdesktop.beansbinding.BeanProperty.create("border"), "tuvTextCenterBorder");
        binding.setConverter(borderConverter1);
        bindingGroup.addBinding(binding);

        jScrollPane2.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                forwardScrollEvent(evt);
            }
        });

        tuvTextCenter.setEditable(false);
        tuvTextCenter.setFont(UIManager.getDefaults().getFont("Label.font"));
        tuvTextCenter.setText("Target text 2");
        jScrollPane2.setViewportView(tuvTextCenter);

        jPanel1.add(jScrollPane2);

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane5.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, rightButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jScrollPane5, org.jdesktop.beansbinding.BeanProperty.create("border"), "tuvTextRightBorder");
        binding.setConverter(borderConverter1);
        bindingGroup.addBinding(binding);

        jScrollPane5.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                forwardScrollEvent(evt);
            }
        });

        tuvTextRight.setEditable(false);
        tuvTextRight.setFont(UIManager.getDefaults().getFont("Label.font"));
        tuvTextRight.setText("Target text 3");
        jScrollPane5.setViewportView(tuvTextRight);

        jPanel1.add(jScrollPane5);

        sourceTargetPanel.add(jPanel1, java.awt.BorderLayout.CENTER);

        add(sourceTargetPanel, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void leftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_leftButtonActionPerformed

    private void forwardScrollEvent(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_forwardScrollEvent
        GuiUtil.forwardMouseWheelEvent(scrollTarget, evt);
    }//GEN-LAST:event_forwardScrollEvent

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.madlonkay.supertmxmerge.gui.BorderConverter borderConverter1;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JRadioButton centerButton;
    private javax.swing.JLabel itemNumberLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JRadioButton leftButton;
    private javax.swing.JRadioButton rightButton;
    private javax.swing.JScrollPane sourceScrollPane;
    private javax.swing.JPanel sourceTargetPanel;
    private javax.swing.JTextArea sourceText;
    private javax.swing.JTextPane tuvTextCenter;
    private javax.swing.JTextPane tuvTextLeft;
    private javax.swing.JTextPane tuvTextRight;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
