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
import javax.swing.AbstractButton;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import org.madlonkay.supertmxmerge.MergeController.ConflictInfo;
import org.madlonkay.supertmxmerge.data.Key;
import org.madlonkay.supertmxmerge.util.CharDiff;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class MergeCell extends javax.swing.JPanel {

    private final static Border BORDER_DEFAULT;
    private final static Border BORDER_SELECTED;
    
    private final static MapToTextConverter CONVERTER = new MapToTextConverter();
    
    private final ConflictInfo info;
    private final boolean isDetailMode;
    
    static {
        JScrollPane sp = new JScrollPane();
        BORDER_DEFAULT = sp.getBorder();
        BORDER_SELECTED = new MatteBorder(2, 2, 2, 2, Color.BLUE);
    }
    
    /**
     * Creates new form TUDiffCell
     * @param itemNumber
     * @param info
     * @param isDetailMode
     */
    public MergeCell(int itemNumber, ConflictInfo info, boolean isDetailMode) {
        initComponents();
        
        this.isDetailMode = isDetailMode;
        this.info = info;
        itemNumberLabel.setText(String.valueOf(itemNumber));
        sourceText.setText(info.key.sourceText);
        if (info.key.props != null) {
            setToolTipText((String) CONVERTER.convertForward(info.key.props));
        }
        setSourceLanguage(info.sourceLanguage);
        setTargetLanguage(info.targetLanguage);
        String baseText = setBaseText(info.baseTuvText, info.baseTuvProps);
        String leftText = setLeftText(info.leftTuvText, info.leftTuvProps, info.baseTuvText != null);
        String rightText = setRightText(info.rightTuvText, info.rightTuvProps, info.baseTuvText != null);
        
        tuvTextLeft.addMouseListener(new ClickForwarder(leftButton));
        tuvTextRight.addMouseListener(new ClickForwarder(rightButton));
        tuvTextCenter.addMouseListener(new ClickForwarder(centerButton));
        if (info.baseTuvText == null) {
            CharDiff.applyStyling(leftText, rightText, tuvTextLeft, tuvTextRight);
        } else {
            CharDiff.applyStyling(baseText, leftText, tuvTextCenter, tuvTextLeft);
            CharDiff.applyStyling(baseText, rightText, tuvTextCenter, tuvTextRight, true);
        }
        
        if (isDetailMode) {
            itemNumberLabel.setVisible(false);
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
    
    private String setBaseText(String text, Map<String, String> props) {
        boolean disableCharDiff = false;
        if (text == null) {
            tuvTextCenter.setBackground(getBackground());
            text = LocString.get("STM_TUV_NOT_PRESENT");
            disableCharDiff = true;
        }
        if (props != null) {
            if (isDetailMode) {
                text += "\n\n" + MapToTextConverter.mapToPlainText(props);
            } else {
                tuvTextCenter.setToolTipText(MapToTextConverter.mapToHtml(props));
            }
        }
        tuvTextCenter.setText(text);
        return disableCharDiff ? null : text;
    }
    
    private String setLeftText(String text, Map<String, String> props, boolean presentInBase) {
        boolean disableCharDiff = false;
        if (text == null) {
            tuvTextLeft.setBackground(getBackground());
            text = presentInBase? LocString.get("STM_TUV_DELETED") :
                    LocString.get("STM_TUV_NOT_PRESENT");
            disableCharDiff = true;
        }
        if (props != null) {
            if (isDetailMode) {
                text += "\n\n" + MapToTextConverter.mapToPlainText(props);
            } else {
                tuvTextLeft.setToolTipText(MapToTextConverter.mapToHtml(props));
            }
        }
        tuvTextLeft.setText(text);
        return disableCharDiff ? null : text;
    }
    
    private String setRightText(String text, Map<String, String> props, boolean presentInBase) {
        boolean disableCharDiff = false;
        if (text == null) {
            tuvTextRight.setBackground(getBackground());
            text = presentInBase? LocString.get("STM_TUV_DELETED") :
                    LocString.get("STM_TUV_NOT_PRESENT");
            disableCharDiff = true;
        }
        if (props != null) {
            if (isDetailMode) {
                text += "\n\n" + MapToTextConverter.mapToPlainText(props);
            } else {
                tuvTextRight.setToolTipText(MapToTextConverter.mapToHtml(props));
            }
        }
        tuvTextRight.setText(text);
        return disableCharDiff ? null : text;
    }
    
    public AbstractButton[] getButtons() {
        return new AbstractButton[] { leftButton, centerButton, rightButton };
    }
    
    public Key getKey() {
        return info.key;
    }
    
    public void setIsTwoWayMerge(boolean isTwoWayMerge) {
        if (isTwoWayMerge) {
            jPanel1.remove(tuvTextCenter);
        } else {
            jPanel1.add(tuvTextCenter, 1);
        }
    }
    
    public boolean isSelectionPerformed() {
        return leftButton.getModel().isSelected() || rightButton.getModel().isSelected()||
                centerButton.getModel().isSelected();
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
        sourceText = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        tuvTextLeft = new javax.swing.JTextPane();
        tuvTextCenter = new javax.swing.JTextPane();
        tuvTextRight = new javax.swing.JTextPane();

        buttonGroup.add(leftButton);
        leftButton.setContentAreaFilled(false);

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

        sourceText.setEditable(false);
        sourceText.setColumns(72);
        sourceText.setFont(UIManager.getDefaults().getFont("Label.font"));
        sourceText.setLineWrap(true);
        sourceText.setText("Source text");
        sourceText.setWrapStyleWord(true);
        sourceText.setBorder(BORDER_DEFAULT);
        jPanel2.add(sourceText);

        sourceTargetPanel.add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Target Language"));
        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        tuvTextLeft.setEditable(false);
        tuvTextLeft.setFont(UIManager.getDefaults().getFont("Label.font"));
        tuvTextLeft.setText("Target text 1");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, leftButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), tuvTextLeft, org.jdesktop.beansbinding.BeanProperty.create("border"), "tuvTextLeftBorder");
        binding.setConverter(borderConverter1);
        bindingGroup.addBinding(binding);

        jPanel1.add(tuvTextLeft);

        tuvTextCenter.setEditable(false);
        tuvTextCenter.setFont(UIManager.getDefaults().getFont("Label.font"));
        tuvTextCenter.setText("Target text 2");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, centerButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), tuvTextCenter, org.jdesktop.beansbinding.BeanProperty.create("border"), "tuvTextCenterBorder");
        binding.setConverter(borderConverter1);
        bindingGroup.addBinding(binding);

        jPanel1.add(tuvTextCenter);

        tuvTextRight.setEditable(false);
        tuvTextRight.setFont(UIManager.getDefaults().getFont("Label.font"));
        tuvTextRight.setText("Target text 3");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, rightButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), tuvTextRight, org.jdesktop.beansbinding.BeanProperty.create("border"), "tuvTextRightBorder");
        binding.setConverter(borderConverter1);
        bindingGroup.addBinding(binding);

        jPanel1.add(tuvTextRight);

        sourceTargetPanel.add(jPanel1, java.awt.BorderLayout.CENTER);

        add(sourceTargetPanel, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.madlonkay.supertmxmerge.gui.BorderConverter borderConverter1;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JRadioButton centerButton;
    private javax.swing.JLabel itemNumberLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton leftButton;
    private javax.swing.JRadioButton rightButton;
    private javax.swing.JPanel sourceTargetPanel;
    private javax.swing.JTextArea sourceText;
    private javax.swing.JTextPane tuvTextCenter;
    private javax.swing.JTextPane tuvTextLeft;
    private javax.swing.JTextPane tuvTextRight;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
