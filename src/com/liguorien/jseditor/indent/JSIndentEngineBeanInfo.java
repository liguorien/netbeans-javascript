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

package com.liguorien.jseditor.indent;

import com.liguorien.jseditor.*;
import java.beans.BeanDescriptor;
import java.util.MissingResourceException;
import org.netbeans.modules.editor.FormatterIndentEngineBeanInfo;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.util.NbBundle;

/**
 * @author Nicolas Désy
 */
public class JSIndentEngineBeanInfo extends FormatterIndentEngineBeanInfo {

    /** */
    private BeanDescriptor beanDescriptor;

    
    static{
        
    }

    //
    // init
    //

    /** */
    public JSIndentEngineBeanInfo () {
    }


    //
    // FormatterIndentEngineBeanInfo
    //

    /**
     */
    public BeanDescriptor getBeanDescriptor () {
        if (beanDescriptor == null) {
            beanDescriptor = new BeanDescriptor (getBeanClass());
            beanDescriptor.setDisplayName (getString ("LAB_JSIndentEngine"));
            beanDescriptor.setShortDescription (getString ("HINT_JSIndentEngine"));         
        }
        return beanDescriptor;
    }

    /**
     */
    protected Class getBeanClass () {
        return JSIndentEngine.class;
    }

    /**
     */
    protected String[] createPropertyNames () {
        return NbEditorUtilities.mergeStringArrays
            (super.createPropertyNames(),
             new String[] {
             }
             );
    }

    /**
     */
    protected String getString (String key) {
        try {
            return NbBundle.getMessage(JSIndentEngineBeanInfo.class, key);
        } catch (MissingResourceException e) {
            return super.getString (key);
        }
    }
}