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
package com.liguorien.jseditor.dataloader;

import com.sun.org.apache.bcel.internal.util.ClassPath;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.List;
import org.netbeans.modules.java.JavaNode;
import org.netbeans.modules.java.RenameHandler;
import org.openide.ErrorManager;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
/**
 *
 *@author Nicolas Désy
 */
public class JSDataNode extends DataNode {
    
    private static final String SHEETNAME_TEXT_PROPERTIES = "textProperties"; // NOI18N
    private static final String PROP_ENCODING = "encoding"; // NOI18N
    private static final String IMAGE_ICON_BASE = "com/liguorien/jseditor/jsicon.gif";
    static final String ATTR_FILE_ENCODING = "Content-Encoding"; // NOI18N
    
    public JSDataNode(JSDataObject obj) {
        super(obj, Children.LEAF);
        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }
    
    private static synchronized RenameHandler getRenameHandler() {
        Lookup.Result renameImplementations = Lookup.getDefault().lookup(new Lookup.Template(RenameHandler.class));
        List handlers = (List) renameImplementations.allInstances();
        if (handlers.size()==0)
            return null;
        if (handlers.size()>1)
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Multiple instances of RenameHandler found in Lookup; only using first one: " + handlers); //NOI18N
        return (RenameHandler) handlers.get(0);
    }
    
    
    private Node.Property createNameProperty() {
        Node.Property p = new PropertySupport.ReadWrite(
                DataObject.PROP_NAME,
                String.class,
                NbBundle.getMessage(DataObject.class, "PROP_name"),
                NbBundle.getMessage(DataObject.class, "HINT_name")
                ) {
            public Object getValue() {
                return JSDataNode.this.getName();
            }
            
            public Object getValue(String key) {
                if ("suppressCustomEditor".equals(key)) { //NOI18N
                    return Boolean.TRUE;
                } else {
                    return super.getValue(key);
                }
            }
            public void setValue(Object val) throws IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
                if (!canWrite())
                    throw new IllegalAccessException();
                if (!(val instanceof String))
                    throw new IllegalArgumentException();
                
                JSDataNode.this.setName((String)val);
            }
            
            public boolean canWrite() {
                return JSDataNode.this.canRename();
            }
            
        };
        
        return p;
    }
    
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        
        //if there is any rename handler installed
        //push under our own property
        if (getRenameHandler() != null)
            sheet.get(Sheet.PROPERTIES).put(createNameProperty());
        
        Sheet.Set ps = new Sheet.Set();
        ps.setName(SHEETNAME_TEXT_PROPERTIES);
        ps.setDisplayName(NbBundle.getMessage(JSDataNode.class, "PROP_textfileSetName")); // NOI18N
        ps.setShortDescription(NbBundle.getMessage(JSDataNode.class, "HINT_textfileSetName")); // NOI18N
        ps.put(new PropertySupport.ReadWrite(PROP_ENCODING,
                String.class, NbBundle.getMessage(JSDataNode.class, "PROP_fileEncoding"),
                NbBundle.getMessage(JSDataNode.class, "HINT_fileEncoding")) { // NOI18N
            public Object getValue() {
                String enc = (String)getDataObject().getPrimaryFile().getAttribute(ATTR_FILE_ENCODING);
                if (enc == null)
                    return "";
                else
                    return enc;
            }
            
            public void setValue(Object enc) throws InvocationTargetException {
                String encoding = (String)enc;
                if (encoding != null) {
                    if (!"".equals(encoding)) {
                        try {
                            Charset.forName(encoding);
                        } catch (IllegalArgumentException ex) {
                            // IllegalCharsetNameException or UnsupportedCharsetException
                            InvocationTargetException t =  new InvocationTargetException(ex);
                            ErrorManager.getDefault().notify(t);
                            throw t;
                        }
                    } else
                        encoding = null;
                }
                try {
                    getDataObject().getPrimaryFile().setAttribute(ATTR_FILE_ENCODING, encoding);
                    ((JSDataObject)getDataObject()).firePropertyChange0(PROP_ENCODING, null, null);
                } catch (IOException ex) {
                    throw new InvocationTargetException(ex);
                }
            }
        });
        sheet.put(ps);
        
        return sheet;
    }
    
//    /** Creates a property sheet. */
//    protected Sheet createSheet() {
//        Sheet s = super.createSheet();
//        Sheet.Set ss = s.get(Sheet.PROPERTIES);
//        if (ss == null) {
//            ss = Sheet.createPropertiesSet();
//            s.put(ss);
//        }
//        // TODO add some relevant properties: ss.put(...)
//        return s;
//    }
    
}
