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
import org.netbeans.editor.ext.ExtFormatter;
import org.netbeans.modules.editor.FormatterIndentEngine;
import org.openide.util.HelpCtx;

/**
 * @author Nicolas Désy
 */
public class JSIndentEngine extends FormatterIndentEngine {
    

    protected ExtFormatter createFormatter() {
        return new JSFormatter(JSEditorKit.class);
    }
    
    /**
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(JSIndentEngine.class);
    }
    
    /**
     */
    protected boolean acceptMimeType(String mimeType) {
        return "text/x-javascript".equals(mimeType);
    }
}