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

package com.liguorien.jseditor.stub;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;
import org.netbeans.editor.BaseDocument;

/**
 * @author Nicolas Désy
 */
public class NewJSClassModel {
    
    /** Creates a new instance of NewJSClassModel */
    public NewJSClassModel() {
        setName("");
        setSuperclass("");        
        setFields(new ArrayList());
        setMethods(new ArrayList());
    }
    
    
    /**
     *generate the class in a String format
     */
    public String generate(){
        
        final StringBuffer buffer = new StringBuffer(200);
        
        /** CONSTRUCTOR SIGNATURE **/
        buffer.append(getName()).append(" = function(");
        
        int nbParamsAppended = 0;
        
        Iterator fields = getFields().iterator();
        while(fields.hasNext()){
            final JSField field = (JSField)fields.next();
            if(field.isConstructor()){
                if(nbParamsAppended++ > 0){
                    buffer.append(", ");
                }
                buffer.append(field.getName());
            }
        }
        buffer.append(") {\n");
        /** CONSTRUCTOR SIGNATURE **/
        
        
        /** CONSTRUCTOR BODY **/
        fields = getFields().iterator();
        while(fields.hasNext()){
            final JSField field = (JSField)fields.next();
            
            if(!"".equals(field.getDefaultValue())){
                if(field.isConstructor()){
                    buffer
                            .append('\t')
                            .append(field.isStaticField() ? getName() : "this")
                            .append('.')
                            .append(field.getName())
                            .append(" = (")
                            .append(field.getName())
                            .append(" != undefined) ? ")
                            .append(field.getName())
                            .append(" : ")
                            .append(field.getDefaultValue())
                            .append(";\n");
                }else if (!field.isStaticField()){
                    buffer
                            .append("\tthis.")
                            .append(field.getName())
                            .append(" = ")
                            .append(field.getDefaultValue())
                            .append(";\n");
                }
            }else{
                if(field.isConstructor()){
                    buffer
                            .append('\t')
                            .append(field.isStaticField() ? getName() : "this")
                            .append('.')
                            .append(field.getName())
                            .append(" = ")
                            .append(field.getName())
                            .append(";\n");
                }else if (!field.isStaticField()){
                    buffer
                            .append("\tthis.")
                            .append(field.getName())
                            .append(" = null;\n");
                }
            }
        }
        buffer.append("}\n");
        /** CONSTRUCTOR BODY **/
        
        
        /** NON CONSTRUCTOR STATIC FIELDS **/
        fields = getFields().iterator();
        while(fields.hasNext()){
            final JSField field = (JSField)fields.next();
            if(field.isStaticField() && !field.isConstructor()){
                buffer
                        .append(getName())
                        .append('.')
                        .append(field.getName())
                        .append(" = ")
                        .append((!"".equals(field.getDefaultValue())) ? field.getDefaultValue() : "null")
                        .append(";\n");
            }
        }
        /** NON CONSTRUCTOR STATIC FIELDS **/
        
        
        /** SUPERCLASS INHERITANCE **/
        if(!"".equals(getSuperclass())){
            buffer
                    .append('\n')
                    .append(getName())
                    .append(".prototype = new ")
                    .append(getSuperclass())
                    .append("();\n");
        }
        /** SUPERCLASS INHERITANCE **/
        
        
        
        /** METHODS DECLARATION **/
        Iterator methods = getMethods().iterator();
        while(methods.hasNext()){
            final JSMethod method = (JSMethod)methods.next();
            buffer
                    .append("\n")
                    .append(getName())
                    .append((method.isStaticMethod()) ? "" : ".prototype")
                    .append('.')
                    .append(method.getName())
                    .append(" = function(")
                    .append(method.getParameters())
                    .append(") {\n");
            
            if(method.isOverride()){
                buffer
                        .append("\tvar returnValue = ")
                        .append(getSuperclass())
                        .append(".prototype.")
                        .append(method.getName())
                        .append(".apply(this, arguments);\n\t\n\treturn returnValue;");
            }
            
            buffer.append("\n}\n");
        }
        /** METHODS DECLARATION **/
        
        return buffer.toString();
    }
    
    
    private JSListModel _fieldModel = null;
    public JSListModel getFieldModel(){
        if(_fieldModel == null){
            _fieldModel = new JSListModel(getFields());
        }
        return _fieldModel;
    }
    
    private JSListModel _methodModel = null;
    public JSListModel getMethodModel(){
        if(_methodModel == null){
            _methodModel = new JSListModel(getMethods());
        }
        return _methodModel;
    }
    
    /**
     * Holds value of property _name.
     */
    private String _name;
    
    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return _name;
    }
    
    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        _name = name;
    }
    
    /**
     * Holds value of property _superclass.
     */
    private String _superclass;
    
    /**
     * Getter for property superclass.
     * @return Value of property superclass.
     */
    public String getSuperclass() {
        return _superclass;
    }
    
    /**
     * Setter for property superclass.
     * @param superclass New value of property superclass.
     */
    public void setSuperclass(String superclass) {
        _superclass = superclass;
    }
    
    /**
     * Holds value of property _fields.
     */
    private List _fields;
    
    /**
     * Getter for property fields.
     * @return Value of property fields.
     */
    public List getFields() {
        return _fields;
    }
    
    /**
     * Setter for property fields.
     * @param fields New value of property fields.
     */
    public void setFields(List fields) {
        _fields = fields;
    }
    
    /**
     * Holds value of property _methods.
     */
    private List _methods;
    
    /**
     * Getter for property methods.
     * @return Value of property methods.
     */
    public List getMethods() {
        return _methods;
    }
    
    /**
     * Setter for property methods.
     * @param methods New value of property methods.
     */
    public void setMethods(List methods) {
        _methods = methods;
    }
    
    
    
    public static class JSMethod {
        
        public String toString(){
            return _name + '(' + _parameters + ')';
        }
        
        /**
         * Holds value of property _name.
         */
        private String _name;
        
        /**
         * Getter for property name.
         * @return Value of property name.
         */
        public String getName() {
            return _name;
        }
        
        /**
         * Setter for property name.
         * @param name New value of property name.
         */
        public void setName(String name) {
            _name = name;
        }
        
        /**
         * Holds value of property _parameters.
         */
        private String _parameters;
        
        /**
         * Getter for property parameters.
         * @return Value of property parameters.
         */
        public String getParameters() {
            return _parameters;
        }
        
        /**
         * Setter for property parameters.
         * @param parameters New value of property parameters.
         */
        public void setParameters(String parameters) {
            _parameters = parameters;
        }
        
        /**
         * Holds value of property _staticMethod.
         */
        private boolean _staticMethod;
        
        /**
         * Getter for property staticMethod.
         * @return Value of property staticMethod.
         */
        public boolean isStaticMethod() {
            return _staticMethod;
        }
        
        /**
         * Setter for property staticMethod.
         * @param staticMethod New value of property staticMethod.
         */
        public void setStaticMethod(boolean staticMethod) {
            _staticMethod = staticMethod;
        }
        
        /**
         * Holds value of property _override.
         */
        private boolean _override;
        
        /**
         * Getter for property override.
         * @return Value of property override.
         */
        public boolean isOverride() {
            return _override;
        }
        
        /**
         * Setter for property override.
         * @param override New value of property override.
         */
        public void setOverride(boolean override) {
            _override = override;
        }
        
    }
    
    
    public static class JSField {
        
        public String toString(){
            StringBuffer buffer = new StringBuffer(_name);
            if(!"".equals(_defaultValue)){
                buffer.append(" = ").append(_defaultValue);
            }
            return buffer.toString();
        }
        
        
        /**
         * Holds value of property _name.
         */
        private String _name;
        
        /**
         * Getter for property name.
         * @return Value of property name.
         */
        public String getName() {
            return _name;
        }
        
        /**
         * Setter for property name.
         * @param name New value of property name.
         */
        public void setName(String name) {
            _name = name;
        }
        
        /**
         * Holds value of property _defaultValue.
         */
        private String _defaultValue;
        
        /**
         * Getter for property defaultValue.
         * @return Value of property defaultValue.
         */
        public String getDefaultValue() {
            return _defaultValue;
        }
        
        /**
         * Setter for property defaultValue.
         * @param defaultValue New value of property defaultValue.
         */
        public void setDefaultValue(String defaultValue) {
            _defaultValue = defaultValue;
        }
        
        /**
         * Holds value of property _staticField.
         */
        private boolean _staticField;
        
        /**
         * Getter for property staticField.
         * @return Value of property staticField.
         */
        public boolean isStaticField() {
            return _staticField;
        }
        
        /**
         * Setter for property staticField.
         * @param staticField New value of property staticField.
         */
        public void setStaticField(boolean staticField) {
            _staticField = staticField;
        }
        
        /**
         * Holds value of property _constructor.
         */
        private boolean _constructor;
        
        /**
         * Getter for property constructor.
         * @return Value of property constructor.
         */
        public boolean isConstructor() {
            return _constructor;
        }
        
        /**
         * Setter for property constructor.
         * @param constructor New value of property constructor.
         */
        public void setConstructor(boolean constructor) {
            _constructor = constructor;
        }
        
    }
    
    
    
    
    
    public static class JSListModel extends AbstractListModel {
        
        private List _values;
        
        public JSListModel(List values){
            super();
            _values = values;
        }
        
        public int getSize() {
            return _values.size();
        }
        
        public Object getElementAt(int i) {
            return _values.get(i);
        }
        
        public void add(Object value){
            if(value == null) return;
            _values.add(value);
            final int index = _values.size()-1;
            fireIntervalAdded(this, index, index);
        }
        
        public void remove(Object value){
            if(value == null) return;
            int index = _values.indexOf(value);
            if(index == -1) return;
            _values.remove(index);
            fireIntervalRemoved(this, index, index);
        }
    }
}
