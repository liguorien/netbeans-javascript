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
package com.liguorien.jseditor.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.editor.BaseDocument;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.ErrorManager;
import org.openide.util.Utilities;

/**
 * @author Nicolas Désy
 */
public class JSCompletionItem implements CompletionItem {
    
    public final static int EVENT = 1;
    public final static int ARRAY = 2;
    public final static int FIELD = 3;
    public final static int METHOD = 6;
    public final static int CLASS = 5;
    
    private static Color classColor = Color.decode("0x7C0000");
    private static ImageIcon classIcon = null;
    private static Color methodColor = Color.decode("0x7C0000");
    private static ImageIcon methodIcon = null;
    private static Color fieldColor = Color.decode("0x0000B2");
    private static ImageIcon fieldIcon = null;
    private static ImageIcon eventIcon = null;
    private ImageIcon  icon;
    private int _type;
    private int _carretOffset;
    private int _dotOffset;
    private String _name;
    private String _displayName;
    
    
    /** Creates a new instance of JSCompletionItem */
    public JSCompletionItem(String name, String label, int type, int dotOffset, int carretOffset) {
        _displayName = label;
        _type = type;
        _dotOffset = dotOffset;
        _carretOffset = carretOffset;
        
        if(methodIcon == null){
            methodIcon = new ImageIcon(Utilities.loadImage("com/liguorien/jseditor/resources/method-icon.png"));
            fieldIcon = new ImageIcon(Utilities.loadImage("com/liguorien/jseditor/resources/field-icon.png"));
            classIcon = new ImageIcon(Utilities.loadImage("com/liguorien/jseditor/resources/class-icon.png"));
            eventIcon = new ImageIcon(Utilities.loadImage("com/liguorien/jseditor/resources/event-icon.png"));
        }
        
        _name = name;
        
        switch(type){
            case CLASS :
                icon = classIcon;
                break;
                
            case METHOD :
                icon = methodIcon;               
                break;
                
            case EVENT :
                icon = eventIcon;
                break;
                
            case FIELD :
            case ARRAY :                
                icon = fieldIcon;
                break;
        }
    }
    
    private void doSubstitute(JTextComponent component, String toAdd, int backOffset) {
        final BaseDocument doc = (BaseDocument) component.getDocument();
        final int caretOffset = component.getCaretPosition();
        String value = getText();
        
        if (toAdd != null) {
            value += toAdd;
        }
        
        // Update the text
        doc.atomicLock();
        
        try {
            //  final String prefix = doc.getText(_carretOffset, caretOffset - _carretOffset);
            doc.remove(_dotOffset+1, _carretOffset-_dotOffset-1);
            switch(_type){
                case METHOD :
                    doc.insertString(_dotOffset+1, value + "()", null);
                    component.setCaretPosition(component.getCaretPosition() - backOffset-1);
                    break;
                case ARRAY :
                    doc.insertString(_dotOffset+1, value + "[]", null);
                    component.setCaretPosition(component.getCaretPosition() - backOffset-1);
                    break;
                default :
                    doc.insertString(_dotOffset+1, value, null);
                    component.setCaretPosition(component.getCaretPosition() - backOffset);
                    break;
            }
            
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        } finally {
            doc.atomicUnlock();
        }
    }
    
    public void defaultAction(JTextComponent component) {
        doSubstitute(component, null, 0);
        Completion.get().hideAll();
    }
    
    public void processKeyEvent(KeyEvent evt) {
        if (evt.getID() == KeyEvent.KEY_TYPED && evt.getKeyCode() == KeyEvent.VK_ENTER) {
            doSubstitute((JTextComponent) evt.getSource(), _name, _name.length() - 1);
            evt.consume();
        }
    }
    
    public int getPreferredWidth(Graphics g, Font defaultFont) {
        //defaultFont = defaultFont.deriveFont(defaultFont.getStyle() ^ Font.BOLD);
        switch(_type){
            case ARRAY : return CompletionUtilities.getPreferredWidth(_name + "[]", null, g, defaultFont);
            case METHOD : return CompletionUtilities.getPreferredWidth(_displayName, null, g, defaultFont);
            default : return CompletionUtilities.getPreferredWidth(_name, null, g, defaultFont);
        }
    }
    
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        //defaultFont = defaultFont.deriveFont(defaultFont.getStyle() ^ Font.BOLD);
        switch(_type){
            case ARRAY :
                CompletionUtilities.renderHtml(icon, _name + "[]", null, g, defaultFont,
                        (selected ? Color.white : fieldColor), width, height, selected);
                break;
            case METHOD :
                CompletionUtilities.renderHtml(icon, _displayName, null, g, defaultFont,
                        (selected ? Color.white : methodColor), width, height, selected);
                break;
            case CLASS :
                CompletionUtilities.renderHtml(icon, _displayName, null, g, defaultFont,
                        (selected ? Color.white : classColor), width, height, selected);
                break;
            default :
                CompletionUtilities.renderHtml(icon, _name, null, g, defaultFont,
                        (selected ? Color.white : fieldColor), width, height, selected);
                break;
        }
        
    }
    
    public CompletionTask createDocumentationTask() {
        return null;
    }
    
    public CompletionTask createToolTipTask() {
        return null;
    }
    
    public boolean instantSubstitution(JTextComponent component) {
        return true; //????
    }
    
    public int getSortPriority() {
        if(_type == CLASS) return CLASS;
        if(_type == METHOD) return METHOD;
        return FIELD;
    }
    
    public CharSequence getSortText() {
        return getText();
    }
    
    public CharSequence getInsertPrefix() {
        return getText();
    }
    
    public String getText() {
        return _name;
    }
    
    public int hashCode() {
        return getText().hashCode();
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof JSCompletionItem))
            return false;
        
        JSCompletionItem remote = (JSCompletionItem) o;
        
        return getText().equals(remote.getText());
    }
}