/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.liguorien.jseditor;

import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.Set;
import org.netbeans.editor.ext.ExtSettingsNames;
import org.netbeans.modules.editor.options.BaseOptions;
import org.openide.text.IndentEngine;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Nicolas Désy
 */
public class JSOptions extends BaseOptions {
    
    public static final String COMPLETION_AUTO_POPUP_DELAY_PROP = "completionAutoPopupDelay"; // NOI18N
    
    public static String JAVASCRIPT = "Javascript"; // NOI18N
    
    /** Name of property. */
    private static final String HELP_ID = "editing.editor.js"; // NOI18N
    
    //no javascript specific options at this time
    static final String[] JS_PROP_NAMES = new String[] {};
    
    public JSOptions() {
        super(JSEditorKit.class, JAVASCRIPT);       
    }
    
    /*
    protected Class getDefaultIndentEngineClass () {
        return JSIndentEngine.class;
    }
     */
    
    
    
    public int getCompletionAutoPopupDelay() {
        return getSettingInteger(ExtSettingsNames.COMPLETION_AUTO_POPUP_DELAY);
    }
    public void setCompletionAutoPopupDelay(int delay) {
        setSettingInteger(ExtSettingsNames.COMPLETION_AUTO_POPUP_DELAY, delay,
            COMPLETION_AUTO_POPUP_DELAY_PROP);
    }
    
    protected Class getDefaultIndentEngineClass() {
	Class engineClass = null;	
	Lookup.Template tmp = new Lookup.Template(IndentEngine.class);
        Lookup.Result res = Lookup.getDefault().lookup(tmp);
        Set allClasses = res.allClasses();
        for (Iterator it = allClasses.iterator(); it.hasNext();) {
            Class cls = (Class)it.next();
            if (cls.getName().equals("com.liguorien.jseditor.indent.JSIndentEngine")) { //NOI18N
                engineClass = cls;
		break;
            }
        }
        
        return (engineClass != null) ? engineClass : super.getDefaultIndentEngineClass();
    }
    
    /**
     * Gets the help ID
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(HELP_ID);
    }
    
    /**
     * Look up a resource bundle message, if it is not found locally defer to
     * the super implementation
     */
    protected String getString(String key) {
        try {
            return NbBundle.getMessage(JSOptions.class, key);
        } catch (MissingResourceException e) {
            return super.getString(key);
        }
    }    

    public IndentEngine getIndentEngine() {        
        return IndentEngine.find(JSEditorKit.MIME_TYPE);
    }
}
