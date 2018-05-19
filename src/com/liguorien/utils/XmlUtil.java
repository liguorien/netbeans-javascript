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
 * Portions Copyright 2003-2006 Nicolas Désy.
 * All Rights Reserved.
 */

package com.liguorien.utils;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.xml.sax.*;


/**
 * Classe qui permet de gerer un DOM XML de type org.w3c.dom.Document
 *
 * @author Nicolas Désy - http://www.liguorien.com/blog/
 */
public final class XmlUtil {
    
    
    private final static DocumentBuilderFactory _builderFactory = DocumentBuilderFactory.newInstance();
    private final static TransformerFactory _transformerFactory = TransformerFactory.newInstance();
    private final static Map/*<String, String>*/ _defaultOutputProperties = new HashMap/*<String, String>*/();
    
    static {
        _builderFactory.setIgnoringComments(true);
        _builderFactory.setIgnoringElementContentWhitespace(true);
        _defaultOutputProperties.put(OutputKeys.INDENT, "yes");
        _defaultOutputProperties.put(OutputKeys.ENCODING, "UTF-8");
    }
    
    public final static DocumentBuilder getBuilder() throws ParserConfigurationException{
        DocumentBuilder builder;
        
        synchronized(_builderFactory){
            builder = _builderFactory.newDocumentBuilder();
        }
             
        return builder;
    }
    
    public final static Document parse(InputStream input)
    throws SAXException, ParserConfigurationException, IOException {
        return getBuilder().parse(input);
    }
    
    public final static Document parse(File file)
    throws SAXException, ParserConfigurationException, IOException {
        return getBuilder().parse(file);
    }
    
    
    public final static Document parse(InputSource source)
    throws SAXException, ParserConfigurationException, IOException {
        return getBuilder().parse(source);
    }
    
    public final static Document parse(Reader reader)
    throws SAXException, ParserConfigurationException, IOException {
        return parse(new InputSource(reader));
    }
    
    
    public final static Document parse(String docStr)
    throws SAXException, ParserConfigurationException, IOException {
        return parse(new StringReader(docStr));
    }
    
    
    public final static String toString(Node doc)
    throws TransformerConfigurationException, TransformerException{
        return toString(doc, null);
    }
    
    
    public final static String toString(Node doc, Map/*<String, String>*/ properties)
    throws TransformerConfigurationException, TransformerException {
        
        Transformer transformer;
        
        synchronized(_transformerFactory){
            transformer = _transformerFactory.newTransformer();
        }
        
        if(properties == null){
            properties = _defaultOutputProperties;
        }
        
        Iterator it = properties.keySet().iterator();
        while(it.hasNext()){
            String key = (String)it.next();
            transformer.setOutputProperty(key, (String)properties.get(key));
        }
        
        final StringWriter writer = new StringWriter();
        
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        
        return writer.getBuffer().toString();
    }
    
    
    public final static void save(Document doc, File file) {
        
        try {
            final PrintWriter output = new PrintWriter(new FileWriter(file));
            output.println(toString(doc));
            output.close();            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
    public final static void setAttribute(Node node, String name, String value) {
        
        final NamedNodeMap map = node.getAttributes();
        Node att = map.getNamedItem(name);
        
        if(att == null){
            att = node.getOwnerDocument().createAttribute(name);
            map.setNamedItem(att);
        }
        
        att.setNodeValue(value);
    }
    
    
    public final static void removeAttribute(Node node, String name) {
        node.getAttributes().removeNamedItem(name);
    }
    
    
    public final static String getAttribute(Node node, String name) {
        
        final Node att = node.getAttributes().getNamedItem(name);
        if (att != null) {
            return att.getNodeValue();
        }
        return null;
    }
    
    
    public final static Node addNode(Document doc, String tagName, String content) {
        final Element temp = doc.createElement(tagName);
        if(content != null){           
            temp.appendChild(doc.createTextNode(content));
        } 
        doc.getFirstChild().appendChild(temp);
        return temp;
    }
    
    public final static Node addNode(Document doc, String tagName) {
        return addNode(doc,tagName, null);
    }
    
    
    public final static Node addNode(Node node, String tagName, String content) {
        final Document doc = node.getOwnerDocument();
        final Element temp = doc.createElement(tagName);
        if(content != null){           
            temp.appendChild(doc.createTextNode(content));
        }        
        node.appendChild(temp);
        return temp;
    }
    
    public final static Node addNode(Node node, String tagName) {
        return addNode(node, tagName, null);
    }
    
    
    public final static void removeNode(Node nod) {
        if (nod != null) {
            nod.getParentNode().removeChild(nod);
        }
    }
}