/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the JavaScript editor module. 
 * The Initial Developer of the Original Code is Nicolas Désy. 
 * Portions created by Nicolas Désy are Copyright (C) 2006.
 * All Rights Reserved.
 */

package com.liguorien.jseditor.ui;

import com.liguorien.jseditor.stub.NewJSClassModel;
import com.liguorien.jseditor.stub.NewJSClassModel.JSField;
import com.liguorien.jseditor.stub.NewJSClassModel.JSMethod;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author  Nicolas Désy and Matisse ;)
 */
public class NewJSClassView extends javax.swing.JPanel {
    
    private NewJSClassModel _model = null;
    
    public NewJSClassView(NewJSClassModel model){
        _model = model;
        initComponents();
        resetUIFromModel();
        
        fieldNameTxt.getDocument().addDocumentListener(new AddBtnActivator(addFieldBtn));
        methodNameTxt.getDocument().addDocumentListener(new AddBtnActivator(addMethodBtn));
        
        classNameTxt.getDocument().addDocumentListener(new TextFieldUpdater(){
            public void onTextChanged(String text){
                _model.setName(text);
            }
        });
        
         superClassTxt.getDocument().addDocumentListener(new TextFieldUpdater(){
            public void onTextChanged(String text){
                _model.setSuperclass(text);
                overrideChx.setEnabled(text.length() > 0);
            }
        });
        
        fieldList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) { removeFieldBtn.setEnabled(true); }
        });
        
        methodList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) { removeMethodBtn.setEnabled(true); }
        });
        
        addFieldBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFieldBtn.setEnabled(false);
                removeFieldBtn.setEnabled(false);
                
                final JSField field = new JSField();
                field.setName(fieldNameTxt.getText());
                field.setDefaultValue(fieldValueTxt.getText());
                field.setConstructor(constructorChx.isSelected());
                field.setStaticField(staticFieldChx.isSelected());
                                
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        _model.getFieldModel().add(field);                        
                        resetFieldSection();
                        fieldNameTxt.requestFocus();
                    }
                });
            }
        });
        
        
        removeFieldBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _model.getFieldModel().remove(fieldList.getSelectedValue());
                removeFieldBtn.setEnabled(false);
                fieldNameTxt.requestFocus();
            }
        });
        
        
        removeMethodBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _model.getMethodModel().remove(methodList.getSelectedValue());
                removeMethodBtn.setEnabled(false);
                methodNameTxt.requestFocus();
            }
        });
        
        
        addMethodBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addMethodBtn.setEnabled(false);
                removeMethodBtn.setEnabled(false);
                
                final JSMethod method = new JSMethod();
                method.setName(methodNameTxt.getText());
                method.setParameters(methodParamsTxt.getText());
                method.setOverride(overrideChx.isSelected());
                method.setStaticMethod(staticMethodChx.isSelected());
                                
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        _model.getMethodModel().add(method);                        
                        resetMethodSection();
                        methodNameTxt.requestFocus();
                    }
                });
            }
        });
    }
    
    
    private class TextFieldUpdater implements DocumentListener {        
        public void onTextChanged(String text){}
        private void handleEvent(DocumentEvent e) {
            try {
                final Document doc = e.getDocument();
                onTextChanged(doc.getText(0, doc.getLength()));
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
        public void changedUpdate(DocumentEvent e) {handleEvent(e);}
        public void removeUpdate(DocumentEvent e) {handleEvent(e);}
        public void insertUpdate(DocumentEvent e) {handleEvent(e);}
    }
    
    private class AddBtnActivator implements DocumentListener {
        private JButton _btn;
        public AddBtnActivator(JButton btn){
            _btn = btn;
        }
        private void handleEvent(DocumentEvent e) {
            _btn.setEnabled(e.getDocument().getLength() > 0);
        }
        public void changedUpdate(DocumentEvent e) {handleEvent(e);}
        public void removeUpdate(DocumentEvent e) {handleEvent(e);}
        public void insertUpdate(DocumentEvent e) {handleEvent(e);}
    }
    
    
    private void resetFieldSection(){
        fieldNameTxt.setText("");
        fieldValueTxt.setText("");
        staticFieldChx.setSelected(false);
        constructorChx.setSelected(false);
    }
    
    private void resetMethodSection(){
        methodNameTxt.setText("");
        methodParamsTxt.setText("");
        staticMethodChx.setSelected(false);
        overrideChx.setSelected(false);
    }
    
    private void resetUIFromModel(){
        
        // Class infos section
        classNameTxt.setText(_model.getName());
        superClassTxt.setText(_model.getSuperclass());      
        
        // Fields section
        resetFieldSection();
        
        fieldList.setModel(_model.getFieldModel());
        methodList.setModel(_model.getMethodModel());
        
        // Methods section
        resetMethodSection();
        
        addFieldBtn.setEnabled(false);
        removeFieldBtn.setEnabled(false);
        addMethodBtn.setEnabled(false);
        removeMethodBtn.setEnabled(false);
        overrideChx.setEnabled(superClassTxt.getText().length() > 0);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        classNameTxt = new javax.swing.JTextField();
        superClassTxt = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fieldNameTxt = new javax.swing.JTextField();
        fieldValueTxt = new javax.swing.JTextField();
        staticFieldChx = new javax.swing.JCheckBox();
        constructorChx = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        fieldList = new javax.swing.JList();
        addFieldBtn = new javax.swing.JButton();
        removeFieldBtn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        methodNameTxt = new javax.swing.JTextField();
        methodParamsTxt = new javax.swing.JTextField();
        staticMethodChx = new javax.swing.JCheckBox();
        overrideChx = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        methodList = new javax.swing.JList();
        addMethodBtn = new javax.swing.JButton();
        removeMethodBtn = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Class Infos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 0)));
        jLabel1.setText("Name");

        jLabel2.setText("Superclass");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel2))
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(superClassTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(classNameTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(classNameTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(superClassTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fields", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 0)));
        jLabel3.setText("Name");

        jLabel4.setText("Default value");

        staticFieldChx.setText("Static");
        staticFieldChx.setToolTipText("Declare the field as static");
        staticFieldChx.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        staticFieldChx.setMargin(new java.awt.Insets(0, 0, 0, 0));

        constructorChx.setText("Constructor ");
        constructorChx.setToolTipText("The field will initialized by a constructor parameter.  If the parameter is undefined, the default value will be used.");
        constructorChx.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        constructorChx.setMargin(new java.awt.Insets(0, 0, 0, 0));

        fieldList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "_id = -1", "_name", "_items = []", "_listeners = []", "_currentItem = null" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(fieldList);

        addFieldBtn.setText(" Add");
        addFieldBtn.setToolTipText("Add a new field with the current infos.");

        removeFieldBtn.setText("Remove");
        removeFieldBtn.setToolTipText("Remove selected field");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel3)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fieldNameTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                            .add(fieldValueTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, addFieldBtn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, removeFieldBtn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .add(staticFieldChx, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(constructorChx, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(fieldNameTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(constructorChx))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(fieldValueTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(staticFieldChx))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(addFieldBtn)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeFieldBtn)
                        .addContainerGap())
                    .add(jScrollPane1, 0, 0, Short.MAX_VALUE)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Methods", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 0)));
        jLabel5.setText("Name");

        jLabel6.setText("Parameters");

        staticMethodChx.setText("Static");
        staticMethodChx.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        staticMethodChx.setMargin(new java.awt.Insets(0, 0, 0, 0));

        overrideChx.setText("Override");
        overrideChx.setToolTipText("Override and invoke superclass's method implementation in the new implementation.");
        overrideChx.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        overrideChx.setMargin(new java.awt.Insets(0, 0, 0, 0));

        methodList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(methodList);

        addMethodBtn.setText("Add ");

        removeMethodBtn.setText("Remove ");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel5)
                            .add(jLabel6))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(methodParamsTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                            .add(methodNameTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(removeMethodBtn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                    .add(addMethodBtn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, staticMethodChx, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(overrideChx, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(methodNameTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(overrideChx))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(methodParamsTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(staticMethodChx))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(addMethodBtn)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeMethodBtn))
                    .add(jScrollPane2, 0, 0, Short.MAX_VALUE)))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFieldBtn;
    private javax.swing.JButton addMethodBtn;
    private javax.swing.JTextField classNameTxt;
    private javax.swing.JCheckBox constructorChx;
    private javax.swing.JList fieldList;
    private javax.swing.JTextField fieldNameTxt;
    private javax.swing.JTextField fieldValueTxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList methodList;
    private javax.swing.JTextField methodNameTxt;
    private javax.swing.JTextField methodParamsTxt;
    private javax.swing.JCheckBox overrideChx;
    private javax.swing.JButton removeFieldBtn;
    private javax.swing.JButton removeMethodBtn;
    private javax.swing.JCheckBox staticFieldChx;
    private javax.swing.JCheckBox staticMethodChx;
    private javax.swing.JTextField superClassTxt;
    // End of variables declaration//GEN-END:variables
    
}
