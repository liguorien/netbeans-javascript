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


import com.liguorien.utils.XmlUtil;
import com.sun.org.apache.xpath.internal.XPathAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Désy
 */
public class JSCompletionProvider implements CompletionProvider {
    
    public CompletionTask createTask(int queryType, final JTextComponent component) {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
                
                JSCompletionProvider.init();
                
                final CompletionContext context = JSCompletionProvider.getCompletionContext((BaseDocument)doc, caretOffset);
                
                if(context != null && context.scope != null){
                    if(context.typingText == null){
                        context.dotOffset = caretOffset - 1;
                    }else{
                        context.dotOffset = caretOffset - context.typingText.length() - 1;
                    }
                    if(context.dotOffset < 0) context.dotOffset=0;
                    
                    // if the user typed text after the dot, we need to filter the result
                    if(context.typingText!=null){
                        final Iterator it = context.scope.items.iterator();
                        while(it.hasNext()){
                            final CompletionEntry entry = (CompletionEntry)it.next();
                            if(entry.identifier.startsWith(context.typingText)){
                                resultSet.addItem(new JSCompletionItem(entry.identifier, entry.label, entry.type, context.dotOffset, caretOffset));
                            }
                        }
                    }else{
                        final Iterator it = context.scope.items.iterator();
                        while(it.hasNext()){
                            final CompletionEntry entry = (CompletionEntry)it.next();
                            resultSet.addItem(new JSCompletionItem(entry.identifier, entry.label, entry.type, context.dotOffset, caretOffset));
                        }
                    }
                }
                
                /**
                 * FIXME: There is a strange issue here,
                 * if there is only 1 item in the ResultSet
                 * and the CompletionPopup what not open previously,
                 * the completionPopup doesn't pop at all
                 */
                resultSet.finish();
            }
        }, component);
    }
    
    
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        
        if(typedText.charAt(typedText.length()-1) == '.') {
            
            JSCompletionProvider.init();
            
            final CompletionContext context = JSCompletionProvider.getCompletionContext(
                    Utilities.getDocument(component), component.getCaret().getDot());
            
            if(context != null && context.scope != null){
                return COMPLETION_QUERY_TYPE;
            }
        }
        return 0;
    }    
    
    private static Map/*<String, CompletionScope>*/ completionScopes = new HashMap();
    private static boolean _inited = false;
    private static byte[] _initLock = new byte[0];
    
    private static int getType(String name){       
        switch(name.charAt(0)){
            case 'f' : return JSCompletionItem.FIELD;
            case 'm' : return JSCompletionItem.METHOD;
            case 'e' : return JSCompletionItem.EVENT;
            case 'a' : return JSCompletionItem.ARRAY;
            case 'c' : return JSCompletionItem.CLASS;
        }
        return -1;
    }
    
    private static void init(){
        synchronized(_initLock){
            if(!_inited){
                try {
                    final FileObject file = Repository.getDefault().getDefaultFileSystem()
                    .getRoot().getFileObject("Editors/text/x-javascript/completion/completion.xml");
                    final org.w3c.dom.Document dom = XmlUtil.parse(file.getInputStream());
                    final NodeList scopes = XPathAPI.selectNodeList(dom,"/completion/scope");
                    final int nbScope = scopes.getLength();
                    for(int i=0; i<nbScope; i++){
                        final Node scopeNode = scopes.item(i);
                        final String scopeName = XmlUtil.getAttribute(scopeNode,"name");
                        final CompletionScope scope = new CompletionScope(scopeName);
                        final NodeList entries = XPathAPI.selectNodeList(scopeNode,"./item");
                        final int nbEntries = entries.getLength();
                        for(int j=0; j<nbEntries; j++){
                            final Node entryNode = entries.item(j);
                            scope.addEntry(
                                    XmlUtil.getAttribute(entryNode, "name"),
                                    JSCompletionProvider.getType(XmlUtil.getAttribute(entryNode,"type"))
                                    );
                        }
                        completionScopes.put(scopeName, scope);
                    }
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                } finally{
                    _inited = true;
                }
            }
        }
    }
    
    
    private static class CompletionEntry {
        String identifier;
        String label;
        int type;
        CompletionEntry(String label, int type){
            this.label = label;
            this.type = type;
            this.identifier = (type == JSCompletionItem.METHOD) ?
                label.substring(0, label.indexOf('(')) : label;
        }
    }
    
    
    private static class CompletionScope {
        String wordMatch;
        List items = new ArrayList();
        CompletionScope(String wordMatch){
            this.wordMatch=wordMatch;
        }
        void addEntry(String name, int type){            
            items.add(new CompletionEntry(name,type));
        }
        public String toString(){
            return "CompletionScope["+wordMatch+']';
        }
    }
    
    
    private static class CompletionContext {
        CompletionScope scope = null;
        String typingText = null;
        int dotOffset = -1;
    }
    
    
    private static CompletionContext searchCompletionContext(char[] line, int caretOffset){
        
        final CompletionContext context = new CompletionContext();
        int dotOffset = -1;
        int i = line.length;
        int cpt = 0;
        char previousChar = '|';
        
        while(--i > -1){
            cpt++;
            switch(line[i]){
                case '(' :
                case '{' :
                case '+' :
                case '-' :
                case '/' :
                case '*' :
                case ',' :
                case ':' :
                case ' ' :
                    
                    if(dotOffset == -1){
                        if(i < (line.length-1)){
                            context.typingText = new String(line, i+1, line.length-i-1);
                        }
                        dotOffset = i;
                        context.dotOffset = caretOffset-cpt;
                        context.scope = (CompletionScope)completionScopes.get("global");
                    }else{                        
                        Object scope = completionScopes.get(new String(line,i+1,dotOffset-i-1));
                        if(scope != null){
                            context.scope = (CompletionScope)scope;
                        }
                    }
                    return context;
                    
                case '.' :
                    // if the dot is the first on left side of the caret
                    if(dotOffset == -1){
                        // if there is text after the dot
                        if(i < (line.length-1)){
                            context.typingText = new String(line, i+1, line.length-i-1);
                        }
                        dotOffset = i;
                        context.dotOffset = caretOffset-cpt;
                    }else{                        
                        Object scope = completionScopes.get(new String(line,i+1,dotOffset-i-1));
                        if(scope != null){
                            context.scope = (CompletionScope)scope;
                        }
                        return context;
                    }
                    break;
                    
                case '"' :
                    if(previousChar == '.'){
                        context.scope = (CompletionScope)completionScopes.get("string");
                        return context;
                    }
                    break;
            }
            previousChar = line[i];
        }
        
        if(dotOffset == -1){
            context.scope = (CompletionScope)completionScopes.get("global");
            context.typingText = new String(line);
        }else{
            Object scope = completionScopes.get(new String(line,0,dotOffset-i-1));
            if(scope != null){
                context.scope = (CompletionScope)scope;
            }
        }
        
        return context;
    }
    
    
    private static CompletionContext getCompletionContext(BaseDocument bDoc, int caretOffset){
        
        CompletionContext context = null;
        bDoc.readLock();
        
        try{
            final int lineStartOffset = Utilities.getRowFirstNonWhite(bDoc, caretOffset);
            if(lineStartOffset == -1 || caretOffset < lineStartOffset){
                context = new CompletionContext();
                context.scope = (CompletionScope)completionScopes.get("global");
            }else{
                context = JSCompletionProvider.searchCompletionContext(
                        bDoc.getChars(lineStartOffset, caretOffset-lineStartOffset), caretOffset);
            }
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ex);
        }finally{
            bDoc.readUnlock();
        }
        return context;
    }
    
    
    
    
    
    
    
}
