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

/**
 * @author Nicolas Désy
 */
public class NewAjaxCallbackTemplateModel {
    
    public NewAjaxCallbackTemplateModel(){
        setTemplate("");
        setTargetId("");
        setNeedToInclude(true);
        setCallbackName("");
    }
    
    /**
     * Holds value of property _callbackName.
     */
    private String _callbackName;

    /**
     * Getter for property callbackName.
     * @return Value of property callbackName.
     */
    public String getCallbackName() {
        return _callbackName;
    }

    /**
     * Setter for property callbackName.
     * @param callbackName New value of property callbackName.
     */
    public void setCallbackName(String callbackName) {
        _callbackName = callbackName;
    }

    /**
     * Holds value of property _targetId.
     */
    private String _targetId;

    /**
     * Getter for property targetId.
     * @return Value of property targetId.
     */
    public String getTargetId() {
        return _targetId;
    }

    /**
     * Setter for property targetId.
     * @param targetId New value of property targetId.
     */
    public void setTargetId(String targetId) {
        _targetId = targetId;
    }

    /**
     * Holds value of property _template.
     */
    private String _template;

    /**
     * Getter for property template.
     * @return Value of property template.
     */
    public String getTemplate() {
        return _template;
    }

    /**
     * Setter for property template.
     * @param template New value of property template.
     */
    public void setTemplate(String template) {
        _template = template;
    }

    /**
     * Holds value of property _needToInclude.
     */
    private boolean _needToInclude;

    /**
     * Getter for property needToInclude.
     * @return Value of property needToInclude.
     */
    public boolean isNeedToInclude() {
        return _needToInclude;
    }

    /**
     * Setter for property needToInclude.
     * @param needToInclude New value of property needToInclude.
     */
    public void setNeedToInclude(boolean needToInclude) {
        _needToInclude = needToInclude;
    }
    

    
}
