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

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.madlonkay.supertmxmerge.DiffController;
import org.madlonkay.supertmxmerge.IController;
import org.madlonkay.supertmxmerge.MergeController;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class FileSelectWindow extends javax.swing.JFrame {
    
    private final static Logger LOGGER = Logger.getLogger(FileSelectWindow.class.getName());
    
    /**
     * Creates new form FileSelectWindow
     */
    public FileSelectWindow() {
        initComponents();
        TransferHandler th = new FileDropHandler();
        file1Field.setTransferHandler(th);
        file2Field.setTransferHandler(th);
        leftFileField.setTransferHandler(th);
        rightFileField.setTransferHandler(th);
        baseFileField.setTransferHandler(th);
        mergeController.setOutputFile("");
    }

    private void promptChooseFile(JTextField target) {
        // Inspired by https://netbeans.org/kb/docs/java/gui-filechooser.html#config
        int returnVal = jFileChooser1.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser1.getSelectedFile();
            try {
              target.setText(file.getCanonicalPath());
            } catch (IOException ex) {
              LOGGER.log(Level.SEVERE,
                    LocString.getFormat("error_canonical_path", file.getAbsolutePath()), ex);
            }
        }
    }
    
    public IController getActiveController() {
        Component component = diffMergeTabbedPane.getSelectedComponent();
        if (component == diffPanel) {
            return diffController;
        } else if (component == mergePanel) {
            return mergeController;
        }
        return null;
    }
    
    public DiffController getDiffController() {
        return diffController;
    }
    
    public MergeController getMergeController() {
        return mergeController;
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

        jFileChooser1 = new javax.swing.JFileChooser();
        mergeController = new org.madlonkay.supertmxmerge.MergeController();
        diffController = new org.madlonkay.supertmxmerge.DiffController();
        diffMergeTabbedPane = new javax.swing.JTabbedPane();
        diffPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        file1Button = new javax.swing.JButton();
        file2Button = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        file1Field = new javax.swing.JTextField();
        file2Field = new javax.swing.JTextField();
        diffButtonPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        diffOkButton = new javax.swing.JButton();
        diffCancelButton = new javax.swing.JButton();
        mergePanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        baseFileButton = new javax.swing.JButton();
        leftFileButton = new javax.swing.JButton();
        rightFileButton = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        baseFileField = new javax.swing.JTextField();
        leftFileField = new javax.swing.JTextField();
        rightFileField = new javax.swing.JTextField();
        mergeButtonPanel = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        mergeOkButton = new javax.swing.JButton();
        mergeCancelButton = new javax.swing.JButton();

        jFileChooser1.setFileFilter(new FileNameExtensionFilter(LocString.get("tmx_file_type_label"), "tmx"));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(LocString.get("file_select_window_title")); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        diffPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        diffPanel.setLayout(new javax.swing.BoxLayout(diffPanel, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.GridLayout(0, 1));

        file1Button.setText(LocString.get("select_button_file1")); // NOI18N
        file1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                file1ButtonActionPerformed(evt);
            }
        });
        jPanel3.add(file1Button);

        file2Button.setText(LocString.get("select_button_file2")); // NOI18N
        file2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                file2ButtonActionPerformed(evt);
            }
        });
        jPanel3.add(file2Button);

        jPanel1.add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel4.setLayout(new java.awt.GridLayout(0, 1));

        file1Field.setColumns(45);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, diffController, org.jdesktop.beansbinding.ELProperty.create("${file1}"), file1Field, org.jdesktop.beansbinding.BeanProperty.create("text"), "");
        bindingGroup.addBinding(binding);

        jPanel4.add(file1Field);

        file2Field.setColumns(45);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, diffController, org.jdesktop.beansbinding.ELProperty.create("${file2}"), file2Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel4.add(file2Field);

        jPanel1.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel1);

        diffPanel.add(jPanel2);

        diffButtonPanel.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        diffOkButton.setText(LocString.get("ok_button")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${diffController.inputIsValid}"), diffOkButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "okButton");
        bindingGroup.addBinding(binding);

        diffOkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diffOkButtonActionPerformed(evt);
            }
        });
        jPanel5.add(diffOkButton);

        diffCancelButton.setText(LocString.get("cancel_button")); // NOI18N
        diffCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel5.add(diffCancelButton);

        diffButtonPanel.add(jPanel5, java.awt.BorderLayout.EAST);

        diffPanel.add(diffButtonPanel);

        diffMergeTabbedPane.addTab(LocString.get("file_select_diff_tab"), diffPanel); // NOI18N

        mergePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        mergePanel.setLayout(new javax.swing.BoxLayout(mergePanel, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel7.setLayout(new java.awt.GridLayout(0, 1));

        baseFileButton.setText(LocString.get("select_button_base_file")); // NOI18N
        baseFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baseFileButtonActionPerformed(evt);
            }
        });
        jPanel7.add(baseFileButton);

        leftFileButton.setText(LocString.get("select_button_left_file")); // NOI18N
        leftFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftFileButtonActionPerformed(evt);
            }
        });
        jPanel7.add(leftFileButton);

        rightFileButton.setText(LocString.get("select_button_right_file")); // NOI18N
        rightFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightFileButtonActionPerformed(evt);
            }
        });
        jPanel7.add(rightFileButton);

        jPanel6.add(jPanel7, java.awt.BorderLayout.WEST);

        jPanel8.setLayout(new java.awt.GridLayout(0, 1));

        baseFileField.setColumns(20);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mergeController, org.jdesktop.beansbinding.ELProperty.create("${baseFile}"), baseFileField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel8.add(baseFileField);

        leftFileField.setColumns(20);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mergeController, org.jdesktop.beansbinding.ELProperty.create("${leftFile}"), leftFileField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel8.add(leftFileField);

        rightFileField.setColumns(20);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mergeController, org.jdesktop.beansbinding.ELProperty.create("${rightFile}"), rightFileField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel8.add(rightFileField);

        jPanel6.add(jPanel8, java.awt.BorderLayout.CENTER);

        mergePanel.add(jPanel6);

        mergeButtonPanel.setLayout(new java.awt.BorderLayout());

        jPanel10.setLayout(new javax.swing.BoxLayout(jPanel10, javax.swing.BoxLayout.LINE_AXIS));

        mergeOkButton.setText(LocString.get("ok_button")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${mergeController.inputIsValid}"), mergeOkButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        mergeOkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mergeOkButtonActionPerformed(evt);
            }
        });
        jPanel10.add(mergeOkButton);

        mergeCancelButton.setText(LocString.get("cancel_button")); // NOI18N
        mergeCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel10.add(mergeCancelButton);

        mergeButtonPanel.add(jPanel10, java.awt.BorderLayout.EAST);

        mergePanel.add(mergeButtonPanel);

        diffMergeTabbedPane.addTab(LocString.get("file_select_merge_tab"), mergePanel); // NOI18N

        getContentPane().add(diffMergeTabbedPane);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void file1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_file1ButtonActionPerformed
        promptChooseFile(file1Field);
    }//GEN-LAST:event_file1ButtonActionPerformed

    private void file2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_file2ButtonActionPerformed
        promptChooseFile(file2Field);
    }//GEN-LAST:event_file2ButtonActionPerformed

    private void baseFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baseFileButtonActionPerformed
        promptChooseFile(baseFileField);
    }//GEN-LAST:event_baseFileButtonActionPerformed

    private void leftFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftFileButtonActionPerformed
        promptChooseFile(leftFileField);
    }//GEN-LAST:event_leftFileButtonActionPerformed

    private void rightFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightFileButtonActionPerformed
        promptChooseFile(rightFileField);
    }//GEN-LAST:event_rightFileButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
        System.exit(0);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void diffOkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diffOkButtonActionPerformed
        dispose();
        getDiffController().go();
    }//GEN-LAST:event_diffOkButtonActionPerformed

    private void mergeOkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mergeOkButtonActionPerformed
        dispose();
        getMergeController().go();
    }//GEN-LAST:event_mergeOkButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton baseFileButton;
    private javax.swing.JTextField baseFileField;
    private javax.swing.JPanel diffButtonPanel;
    private javax.swing.JButton diffCancelButton;
    private org.madlonkay.supertmxmerge.DiffController diffController;
    private javax.swing.JTabbedPane diffMergeTabbedPane;
    private javax.swing.JButton diffOkButton;
    private javax.swing.JPanel diffPanel;
    private javax.swing.JButton file1Button;
    private javax.swing.JTextField file1Field;
    private javax.swing.JButton file2Button;
    private javax.swing.JTextField file2Field;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JButton leftFileButton;
    private javax.swing.JTextField leftFileField;
    private javax.swing.JPanel mergeButtonPanel;
    private javax.swing.JButton mergeCancelButton;
    private org.madlonkay.supertmxmerge.MergeController mergeController;
    private javax.swing.JButton mergeOkButton;
    private javax.swing.JPanel mergePanel;
    private javax.swing.JButton rightFileButton;
    private javax.swing.JTextField rightFileField;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
