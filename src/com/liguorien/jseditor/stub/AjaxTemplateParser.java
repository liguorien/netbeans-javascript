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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nicolas Désy
 */
public class AjaxTemplateParser {
    
    private static Pattern quotePattern = Pattern.compile("\"");
    private static Pattern forEach = Pattern.compile("<forEach\\svar=\"(.+)\"\\sitems=\"(.+)\"");
    
    private final static String DOC = "doc";
    private int length;
    private String _template;
    private StringBuffer _buffer = new StringBuffer(1000);
    private int _indendationLevel = 1;
    private boolean _needToInclude = false;
    
    public static String parse(String callbackName, String targetId, String template, boolean wantToInclude)
    throws TemplateParsingException {
        return new AjaxTemplateParser(callbackName, targetId, template, wantToInclude).toString();
    }
    
    /** Creates a new instance of AjaxTemplateParser */
    private AjaxTemplateParser(String callbackName, String targetId, String template, boolean wantToInclude)
    throws TemplateParsingException {
        
        _template = template.replaceAll("\\n"," ");
        _parse(0, _template.length());
        
        final StringBuffer oldBuffer = _buffer;
        _buffer = new StringBuffer(oldBuffer.length()+300);
        
        _buffer
                .append("function ")
                .append(callbackName)
                .append("(response) {\n\tvar doc = response.responseXML.documentElement;\n\tvar buffer = \"\";\n\t");
        
        if(_needToInclude && wantToInclude){
            _buffer
                    .append("var __LIST=function(els,root){this.els=els;};var getItems=function(el,name,recurse,ref){" +
                    "\n\tvar rs=(ref)?ref:[];var ns=(recurse)?el.getElementsByTagName(name):el.childNodes;var i=-1;var l=ns.length;" +
                    "\n\twhile(++i<l){var n=ns[i];if(n.nodeName==name)rs.push(n);}return new __LIST(rs);" +
                    "\n\t};var LO=__LIST.prototype;LO.items=function(){return this.els;};" +
                    "\n\tLO.getItems=function(name,recurse){var rs=[];var els=this.els;var i=-1;var l=els.length;while(++i<l)" +
                    "\n\t{getItems(els[i],name,recurse,rs);}return new __LIST(rs, this);};\n\t");
        }
        
        _buffer
                .append(oldBuffer)
                .append("\n\tdocument.getElementById(\"")
                .append(targetId)
                .append("\").innerHTML = buffer;\n}\n");
    }
    
    
    private void _parse(int offset, int endOffset) throws TemplateParsingException {
        while(offset < endOffset){
            
            final int forEachIndex = _template.indexOf("<forEach", offset);
            final int expIndex = _template.indexOf("${", offset);
            
            if(forEachIndex > -1 && forEachIndex < expIndex && forEachIndex < endOffset){
                
                addStaticOutput(_template.substring(offset, forEachIndex));
                addLineAndIndendation();
                
                final int forEachEndIndex = _template.indexOf(">", forEachIndex);
                
                if(forEachEndIndex == -1){
                    throw new TemplateParsingException("Missing '>' on '<forEach' element");
                }
                
                final Matcher m = forEach.matcher(_template.substring(forEachIndex, forEachEndIndex));
                if(m.find()){
                    offset = handleLoop(m.group(1), m.group(2), forEachEndIndex+1, endOffset);
                }else{
                    throw new TemplateParsingException("Invalid forEach definition");
                }
            }else if(expIndex > -1 && expIndex < endOffset){
                
                
                final int expEndIndex = _template.indexOf("}", expIndex);
                if(expEndIndex == -1){
                    throw new TemplateParsingException("Missing expression closure : '}'");
                }
                addStaticOutput(_template.substring(offset, expIndex));
                addLineAndIndendation();
                addVariableOutput(_template.substring(expIndex+2, expEndIndex));
                addLineAndIndendation();
                offset = expEndIndex + 1;
            }else{
                final int forEachEndIndex = _template.indexOf("</forEach", offset);
                if(forEachEndIndex > -1 && forEachEndIndex > offset && (forEachIndex==-1 || forEachEndIndex < forEachIndex)){
                    addStaticOutput(_template.substring(offset, forEachEndIndex));
                }else{
                    addStaticOutput(_template.substring(offset, endOffset));
                }
                return;
            }
        }
    }
    
    public String toString(){
        return _buffer.toString();
    }
    
    private void addLineAndIndendation(){
        _buffer.append('\n');
        for(int i=0; i<_indendationLevel; i++){
            _buffer.append('\t');
        }
    }
    
    
    private int getLoopEndOffset(int startOffset, int endOffset){
        int nbLoop = 0;
        
        int loopStart = _template.indexOf("<forEach", startOffset);
        int loopEnd = _template.indexOf("</forEach", startOffset);
        
        if(loopStart < loopEnd && loopEnd > -1 && loopStart > -1){
            return getLoopEndOffset(loopEnd+1, endOffset);
        }else{
            return loopEnd;
        }
    }
    
    private String resolveLoopItems(String items)
    throws TemplateParsingException {
        StringBuffer buffer = new StringBuffer(50);
        int offset;
        final int length = items.length();
        int dotIndex = offset = items.indexOf(".");
        int nextDotIndex = -1;
        if(dotIndex == -1){
            nextDotIndex = length;
        }else{
            dotIndex = offset = nextDotIndex = items.indexOf(".", dotIndex);
        }
        
        if(nextDotIndex == -1){
            nextDotIndex = length;
        }
        
        if(items.charAt(0) == '/'){
            if(items.charAt(1) == '/'){
                buffer
                        .append("getItems(")
                        .append(DOC)
                        .append(",\"")
                        .append(items.substring(2, nextDotIndex))
                        .append("\",true)");
                
                
            }else{
                buffer
                        .append("getItems(")
                        .append(DOC)
                        .append(", \"")
                        .append(items.substring(1, nextDotIndex))
                        .append("\")");
            }
            
            if(nextDotIndex == length){
                return buffer.toString();
            }else{
                offset = nextDotIndex+1;
            }
        }else{
            
            if(dotIndex == -1){
                throw new TemplateParsingException("Invalid 'items' in forEach loop");
            }
            
            boolean recurse = (offset < (length-1) && items.charAt(dotIndex+1) == '.');
            
            
            return buffer
                    .append("getItems(")
                    .append(items.substring(0, dotIndex))
                    .append(",\"")
                    .append(items.substring(dotIndex+((recurse?2:1))))
                    .append("\",")
                    .append(recurse)
                    .append(")")
                    .toString();
        }
        
        if(nextDotIndex == -1){
            return buffer.toString();
        }
        
        offset--;
        
        while(offset < length){
            
            switch(items.charAt(offset)){
                case '@' :
                    return buffer
                            .append(".getAttribute(\"")
                            .append(items.substring(offset+1))
                            .append("\");")
                            .toString();
                default:
                    dotIndex = items.indexOf(".", offset);
                    if(dotIndex == -1){
                        return buffer
                                .append(".getItems(\"")
                                .append(items.substring(offset))
                                .append("\")")
                                .toString();
                        
                    }else{
                        
                        final boolean recurse = items.charAt(dotIndex+1) == '.';
                        int endIndex = items.indexOf(".", dotIndex + (recurse ? 2 : 1));
                        if(endIndex==-1){
                            endIndex = items.length();
                        }
                        
                        buffer
                                .append(".getItems(\"")
                                .append(items.substring(dotIndex+ (recurse ? 2 : 1), endIndex))
                                .append("\",")
                                .append(recurse)
                                .append(')');
                        
                        offset = endIndex+1;
                        if(endIndex == items.length()-1){
                            return buffer.toString();
                        }
                    }
            }
        }
        
        return buffer.toString();
    }
    
    private int handleLoop(String var, String items, int startOffset, int endOffset)
    throws TemplateParsingException {
        
        _needToInclude = true;
        _buffer
                .append("var ")
                .append(var)
                .append("s = ")
                .append(resolveLoopItems(items))
                .append(".items();");
        
        addLineAndIndendation();
        
        _buffer
                .append("var ")
                .append(var)
                .append("sL = ")
                .append(var)
                .append("s.length;");
        
        addLineAndIndendation();
        
        _buffer
                .append("for(var i")
                .append(_indendationLevel)
                .append("=0; i")
                .append(_indendationLevel)
                .append('<')
                .append(var)
                .append("sL; i")
                .append(_indendationLevel)
                .append("++){");
        
        _indendationLevel++;
        addLineAndIndendation();
        
        _buffer
                .append("var ")
                .append(var)
                .append(" = ")
                .append(var)
                .append("s[i")
                .append(_indendationLevel-1)
                .append("];");
        
        addLineAndIndendation();
        
        final int forEachEndIndex = getLoopEndOffset(startOffset, endOffset);//_template.lastIndexOf("</forEach", endOffset);
        
        if(forEachEndIndex == -1){
            throw new TemplateParsingException("<forEach> element closure is missing");
        }
        
        _parse(startOffset, forEachEndIndex);
        
        _indendationLevel--;
        addLineAndIndendation();
        _buffer.append('}');
        addLineAndIndendation();
        
        return forEachEndIndex + "</forEach>".length();
    }
    
    
    private void addStaticOutput(String str){        
        _buffer
                .append("buffer += \"")
                .append(quotePattern.matcher(str).replaceAll("\\\\\""))
                .append("\";");
    }
    
    private void addVariableOutput(String expression){
        
        _buffer.append("buffer += ");
        
        int offset = 0;
        int length = expression.length();
        
        while(offset < length){
            switch(expression.charAt(offset)){
                
                case '/' :
                    _buffer.append(DOC);
                    offset++;
                    break;
                    
                case '@' :
                    _buffer
                            .append(".getAttribute(\"")
                            .append(expression.substring(offset+1))
                            .append("\");");
                    return;
                    
                default:
                    int dotIndex = expression.indexOf(".", offset);
                    if(dotIndex == -1){
                        if(offset==0){
                            _buffer
                                    .append(expression.substring(offset))
                                    .append(".firstChild.nodeValue;");
                        }else{
                            _buffer
                                    .append(".getElementsByTagName(\"")
                                    .append(expression.substring(offset))
                                    .append("\")[0].firstChild.nodeValue;");
                            
                        }
                        return;
                    }else{
                        if(offset == 0){
                            _buffer.append(expression.substring(offset, dotIndex));
                        }else{
                            _buffer
                                    .append(".getElementsByTagName(\"")
                                    .append(expression.substring(offset, dotIndex))
                                    .append("\")[0]");
                        }
                        offset = dotIndex+1;
                    }
            }
        }
    }
    
    public static class TemplateParsingException extends Exception {
        public TemplateParsingException(String msg){
            super(msg);
        }
    }
}
