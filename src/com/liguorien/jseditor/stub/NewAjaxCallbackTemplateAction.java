/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the JavaScript editor module. 
 * The Initial Developer of the Original Code is Nicolas D�sy. 
 * Portions created by Nicolas D�sy are Copyright (C) 2006.
 * All Rights Reserved.
 */
package com.liguorien.jseditor.stub;

import com.liguorien.jseditor.ui.NewAjaxCallbackTemplateView;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JEditorPane;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 * @author Nicolas D�sy ;)
 */ 
public class NewAjaxCallbackTemplateAction extends CookieAction {
    
    protected void performAction(final Node[] activatedNodes) {
        
        final NewAjaxCallbackTemplateModel model = new NewAjaxCallbackTemplateModel();
        final NewAjaxCallbackTemplateView view = new NewAjaxCallbackTemplateView(model);
        final DialogDescriptor descriptor = new DialogDescriptor(view, "New Ajax Callback Template...");
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        
        descriptor.setButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if("OK".equals(e.getActionCommand()) && activatedNodes.length == 1){
                    final EditorCookie ec = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
                    if (ec != null) {
                        JEditorPane[] panes = ec.getOpenedPanes();
                        if (panes.length > 0) {
                            try {
                                panes[0].replaceSelection(
                                        AjaxTemplateParser.parse(
                                        model.getCallbackName(), 
                                        model.getTargetId(), 
                                        model.getTemplate(), 
                                        model.isNeedToInclude())
                                        );
                            } catch (AjaxTemplateParser.TemplateParsingException ex) {
                                ErrorManager.getDefault().notify(ex);
                            }
                        }
                    }
                }
            }
        });
        
        dialog.setVisible(true);
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(NewAjaxCallbackTemplateAction.class, "CTL_NewAjaxCallbackTemplateAction");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            EditorCookie.class
        };
    }
    
    protected String iconResource() {
        return "com/liguorien/jseditor/jsicon.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}