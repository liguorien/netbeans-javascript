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

import com.liguorien.jseditor.completion.BracketCompletion;
import com.liguorien.jseditor.indent.JSFormatter;
import javax.swing.Action;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.TextAction;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Formatter;
import org.netbeans.editor.Syntax;
import org.netbeans.modules.editor.NbEditorKit;

/**
 *
 * @author Nicolas Désy
 */
public class JSEditorKit extends NbEditorKit {
    
   
    public static final String MIME_TYPE = "text/x-javascript"; // NOI18N
    
    /**
     * Creates a new instance of ManifestEditorKit
     */
    public JSEditorKit() {
        
    }
    
    /**
     * Create a syntax object suitable for highlighting javascript file syntax
     */
    public Syntax createSyntax(Document doc) {
        return new JSSyntax();
    }
    
    
    
    /**
     * Retrieves the content type for this editor kit
     */
    public String getContentType() {
        return MIME_TYPE;
    }
    
    /**
     *
     */
    public Formatter createFormatter() {
        return new JSFormatter(JSEditorKit.class);
    }
    
    public Document createDefaultDocument() {
        Document retValue;
        
        retValue = super.createDefaultDocument();
        
        return retValue;
    }
    
    
    protected Action[] createActions() {
        return TextAction.augmentList(super.createActions(), new Action[]{
            new JSKeyTypedAction()
        });
    }
    
    
    public static class JSKeyTypedAction extends ExtDefaultKeyTypedAction {
       
        protected void insertString(BaseDocument doc, int dotPos, Caret caret, String str, boolean overwrite)
        throws BadLocationException {
            super.insertString(doc, dotPos, caret, str, overwrite);
            BracketCompletion.charInserted(doc, dotPos, caret, str.charAt(0));           
            if(str.charAt(0) == '.'){
             //   Completion.get().showCompletion();
            }          
        }        
    }
}
