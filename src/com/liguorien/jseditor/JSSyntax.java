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

import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;

/**
 * @author Miloslav Metelka
 * @author Nicolas Désy
 */
public class JSSyntax extends Syntax {
    
    // Internal states
    private static final int ISI_WHITESPACE = 2; // inside white space
    private static final int ISI_LINE_COMMENT = 4; // inside line comment //
    private static final int ISI_BLOCK_COMMENT = 5; // inside block comment /* ... */
    private static final int ISI_STRING = 6; // inside string constant
    private static final int ISI_STRING_A_BSLASH = 7; // inside string constant after backslash
    private static final int ISI_CHAR = 8; // inside string constant
    private static final int ISI_CHAR_A_BSLASH = 9; // inside string constant after backslash
    private static final int ISI_IDENTIFIER = 10; // inside identifier
    private static final int ISA_SLASH = 11; // slash char
    private static final int ISA_EQ = 12; // after '='
    private static final int ISA_GT = 13; // after '>'
    private static final int ISA_GTGT = 14; // after '>>'
    private static final int ISA_GTGTGT = 15; // after '>>>'
    private static final int ISA_LT = 16; // after '<'
    private static final int ISA_LTLT = 17; // after '<<'
    private static final int ISA_PLUS = 18; // after '+'
    private static final int ISA_MINUS = 19; // after '-'
    private static final int ISA_STAR = 20; // after '*'
    private static final int ISA_STAR_I_BLOCK_COMMENT = 21; // after '*'
    private static final int ISA_PIPE = 22; // after '|'
    private static final int ISA_PERCENT = 23; // after '%'
    private static final int ISA_AND = 24; // after '&'
    private static final int ISA_XOR = 25; // after '^'
    private static final int ISA_EXCLAMATION = 26; // after '!'
    private static final int ISA_ZERO = 27; // after '0'
    private static final int ISI_NUMBER = 28; // number
    private static final int ISI_HEX = 32; // hex number
    private static final int ISA_DOT = 33; // after '.'
    private static final int ISA_METHOD = 34; // after method call
    private static final int ISI_REGEXP = 35; // inside RegExp literal
    private static final int ISI_REGEXP_A_BSLASH = 36; // inside RegExp literal after backslash
    private static final int ISI_REGEXP_A_SLASH = 37; // inside RegExp literal after slash
    
    public JSSyntax() {
        tokenContextPath = JSTokenContext.contextPath;
    }
    
    
    protected TokenID parseToken() {
        char actChar;
        
        while(offset < stopOffset) {
            actChar = buffer[offset];
            
            switch (state) {
                case INIT:
                    switch (actChar) {
                        case '"': // NOI18N
                            state = ISI_STRING;
                            break;
                        case '\'':
                            state = ISI_CHAR;
                            break;
                        case '/':
                            state = ISA_SLASH;
                            break;
                        case '=':
                            state = ISA_EQ;
                            break;
                        case '>':
                            state = ISA_GT;
                            break;
                        case '<':
                            state = ISA_LT;
                            break;
                        case '+':
                            state = ISA_PLUS;
                            break;
                        case '-':
                            state = ISA_MINUS;
                            break;
                        case '*':
                            state = ISA_STAR;
                            break;
                        case '|':
                            state = ISA_PIPE;
                            break;
                        case '%':
                            state = ISA_PERCENT;
                            break;
                        case '&':
                            state = ISA_AND;
                            break;
                        case '^':
                            state = ISA_XOR;
                            break;
                        case '~':
                            offset++;
                            return JSTokenContext.NEG;
                        case '!':
                            state = ISA_EXCLAMATION;
                            break;
                        case '0':
                            state = ISA_ZERO;
                            break;
                        case '.':
                            state = ISA_DOT;
                            break;
                        case ',':
                            offset++;
                            return JSTokenContext.COMMA;
                        case ';':
                            offset++;
                            return JSTokenContext.SEMICOLON;
                        case ':':
                            offset++;
                            return JSTokenContext.COLON;
                        case '?':
                            offset++;
                            return JSTokenContext.QUESTION;
                        case '(':
                            offset++;
                            return JSTokenContext.LPAREN;
                        case ')':
                            offset++;
                            return JSTokenContext.RPAREN;
                        case '[':
                            offset++;
                            return JSTokenContext.LBRACKET;
                        case ']':
                            offset++;
                            return JSTokenContext.RBRACKET;
                        case '{':
                            offset++;
                            return JSTokenContext.LBRACE;
                        case '}':
                            offset++;
                            return JSTokenContext.RBRACE;
                            
                        default:
                            // Check for whitespace
                            if (Character.isWhitespace(actChar)) {
                                state = ISI_WHITESPACE;
                                break;
                            }
                            
                            // Check for digit
                            if (Character.isDigit(actChar)) {
                                state = ISI_NUMBER;
                                break;
                            }
                            
                            // Check for identifier
                            if (Character.isJavaIdentifierStart(actChar)) {
                                state = ISI_IDENTIFIER;
                                break;
                            }
                            
                            offset++;
                            return JSTokenContext.INVALID_CHAR;
                    }
                    break;
                    
                case ISI_WHITESPACE: // white space
                    if (!Character.isWhitespace(actChar)) {
                        state = INIT;
                        return JSTokenContext.WHITESPACE;
                    }
                    break;
                    
                case ISI_LINE_COMMENT:
                    switch (actChar) {
                        case '\n':
                            state = INIT;
                            return JSTokenContext.LINE_COMMENT;
                    }
                    break;
                    
                case ISI_BLOCK_COMMENT:
                    switch (actChar) {
                        case '*':
                            state = ISA_STAR_I_BLOCK_COMMENT;
                            break;
                    }
                    break;
                    
                case ISI_STRING:
                    switch (actChar) {
                        case '\\':
                            state = ISI_STRING_A_BSLASH;
                            break;
                        case '\n':
                            state = INIT;
                            supposedTokenID = JSTokenContext.STRING_LITERAL;
                            return supposedTokenID;
                        case '"': // NOI18N
                            offset++;
                            state = INIT;
                            return JSTokenContext.STRING_LITERAL;
                    }
                    break;
                    
                    
                case ISI_CHAR:
                    switch (actChar) {
                        case '\\':
                            state = ISI_CHAR_A_BSLASH;
                            break;
                        case '\n':
                            state = INIT;
                            supposedTokenID = JSTokenContext.CHAR_LITERAL;
                            return supposedTokenID;
                        case '\'': // NOI18N
                            offset++;
                            state = INIT;
                            return JSTokenContext.CHAR_LITERAL;
                    }
                    break;
                    
                case ISI_STRING_A_BSLASH:
                    switch (actChar) {
                        case '"': // NOI18N
                        case '\\':
                            break;
                        default:
                            offset--;
                            break;
                    }
                    state = ISI_STRING;
                    break;
                    
                case ISI_CHAR_A_BSLASH:
                    switch (actChar) {
                        case '\'': // NOI18N
                        case '\\':
                            break;
                        default:
                            offset--;
                            break;
                    }
                    state = ISI_CHAR;
                    break;
                    
                    
                case ISI_IDENTIFIER:
                    if (!(Character.isJavaIdentifierPart(actChar))) {
                        state = INIT;
                        TokenID tid = matchKeyword(buffer, tokenOffset, offset - tokenOffset);
                        if(tid != null) return tid;
                        return (actChar == '(') ? JSTokenContext.METHOD :  JSTokenContext.IDENTIFIER;
                    }
                    break;
                    
                case ISA_SLASH:
                    switch (actChar) {
                        case ' ':
                            state = INIT;
                            return JSTokenContext.DIV;
                        case '=':
                            offset++;
                            state = INIT;
                            return JSTokenContext.DIV_EQ;
                        case '/':
                            state = ISI_LINE_COMMENT;
                            break;
                        case '*':
                            state = ISI_BLOCK_COMMENT;
                            break;
                        default:
                            
                            int i = offset - 1;
                            
                            // rewind to the first none whitespace character before de slash
                            while(--i>=0 && Character.isWhitespace(buffer[i])){}
                            
                            if(i >= 0){
                                char ch = buffer[i];
                                if(ch=='=' || ch=='('){
                                    state = ISI_REGEXP;
                                }else{
                                    state = INIT;
                                    return JSTokenContext.DIV;
                                }
                            }else{
                                state = INIT;
                                return JSTokenContext.DIV;
                            }
                            break;
                    }
                    break;
                    
                case ISA_EQ:
                    switch (actChar) {
                        case '=':
                            offset++;
                            return  JSTokenContext.EQ_EQ;
                        default:
                            state = INIT;
                            return JSTokenContext.EQ;
                    }
                    // break;
                    
                case ISA_GT:
                    switch (actChar) {
                        case '>':
                            state = ISA_GTGT;
                            break;
                        case '=':
                            offset++;
                            return JSTokenContext.GT_EQ;
                        default:
                            state = INIT;
                            return JSTokenContext.GT;
                    }
                    break;
                    
                case ISA_GTGT:
                    switch (actChar) {
                        case '>':
                            state = ISA_GTGTGT;
                            break;
                        case '=':
                            offset++;
                            return JSTokenContext.RSSHIFT_EQ;
                        default:
                            state = INIT;
                            return JSTokenContext.RSSHIFT;
                    }
                    break;
                    
                case ISA_GTGTGT:
                    switch (actChar) {
                        case '=':
                            offset++;
                            return JSTokenContext.RUSHIFT_EQ;
                        default:
                            state = INIT;
                            return JSTokenContext.RUSHIFT;
                    }
                    // break;
                    
                    
                case ISA_LT:
                    switch (actChar) {
                        case '<':
                            state = ISA_LTLT;
                            break;
                        case '=':
                            offset++;
                            return JSTokenContext.LT_EQ;
                        default:
                            state = INIT;
                            return JSTokenContext.LT;
                    }
                    break;
                    
                case ISA_LTLT:
                    switch (actChar) {
                        case '<':
                            state = INIT;
                            offset++;
                            return JSTokenContext.INVALID_OPERATOR;
                        case '=':
                            offset++;
                            return JSTokenContext.LSHIFT_EQ;
                        default:
                            state = INIT;
                            return JSTokenContext.LSHIFT;
                    }
                    
                case ISA_PLUS:
                    switch (actChar) {
                        case '+':
                            offset++;
                            return JSTokenContext.PLUS_PLUS;
                        case '=':
                            offset++;
                            return JSTokenContext.PLUS_EQ;
                        default:
                            state = INIT;
                            return JSTokenContext.PLUS;
                    }
                    
                case ISA_MINUS:
                    switch (actChar) {
                        case '-':
                            offset++;
                            return JSTokenContext.MINUS_MINUS;
                        case '=':
                            offset++;
                            return JSTokenContext.MINUS_EQ;
                        default:
                            state = INIT;
                            return JSTokenContext.MINUS;
                    }
                    
                case ISA_STAR:
                    switch (actChar) {
                        case '=':
                            offset++;
                            return JSTokenContext.MUL_EQ;
                        case '/':
                            offset++;
                            state = INIT;
                            return JSTokenContext.INVALID_COMMENT_END; // '*/' outside comment
                        default:
                            state = INIT;
                            return JSTokenContext.MUL;
                    }
                    
                case ISA_STAR_I_BLOCK_COMMENT:
                    switch (actChar) {
                        case '/':
                            offset++;
                            state = INIT;
                            return JSTokenContext.BLOCK_COMMENT;
                        default:
                            offset--;
                            state = ISI_BLOCK_COMMENT;
                            break;
                    }
                    break;
                    
                case ISA_PIPE:
                    switch (actChar) {
                        case '=':
                            offset++;
                            state = INIT;
                            return JSTokenContext.OR_EQ;
                        case '|':
                            offset++;
                            state = INIT;
                            return JSTokenContext.OR_OR;
                        default:
                            state = INIT;
                            return JSTokenContext.OR;
                    }
                    // break;
                    
                case ISA_PERCENT:
                    switch (actChar) {
                        case '=':
                            offset++;
                            state = INIT;
                            return JSTokenContext.MOD_EQ;
                        default:
                            state = INIT;
                            return JSTokenContext.MOD;
                    }
                    // break;
                    
                case ISA_AND:
                    switch (actChar) {
                        case '=':
                            offset++;
                            state = INIT;
                            return JSTokenContext.AND_EQ;
                        case '&':
                            offset++;
                            state = INIT;
                            return JSTokenContext.AND_AND;
                        default:
                            state = INIT;
                            return JSTokenContext.AND;
                    }
                    // break;
                    
                case ISA_XOR:
                    switch (actChar) {
                        case '=':
                            offset++;
                            state = INIT;
                            return JSTokenContext.XOR_EQ;
                        default:
                            state = INIT;
                            return JSTokenContext.XOR;
                    }
                    // break;
                    
                case ISA_EXCLAMATION:
                    switch (actChar) {
                        case '=':
                            offset++;
                            state = INIT;
                            return JSTokenContext.NOT_EQ;
                        default:
                            state = INIT;
                            return JSTokenContext.NOT;
                    }
                    // break;
                    
                case ISA_ZERO:
                    switch (actChar) {
                        case '.':
                            state = ISI_NUMBER;
                            break;
                        case 'x':
                        case 'X':
                            state = ISI_HEX;
                            break;
                        default:
                            state = INIT;
                            return JSTokenContext.NUMBER_LITERAL;
                    }
                    break;
                    
                case ISI_NUMBER:
                    switch (actChar) {
                        case '.':
                            state = ISI_NUMBER;
                            break;
                        case 'e':
                        default:
                            if (!(actChar >= '0' && actChar <= '9')) {
                                state = INIT;
                                return JSTokenContext.NUMBER_LITERAL;
                            }
                    }
                    break;
                    
                case ISI_HEX:
                    if (!((actChar >= 'a' && actChar <= 'f')
                    || (actChar >= 'A' && actChar <= 'F')
                    || Character.isDigit(actChar))
                    ) {
                        state = INIT;
                        return JSTokenContext.HEX_LITERAL;
                    }
                    break;
                    
                case ISA_DOT:
                    if (Character.isDigit(actChar)) {
                        state = ISI_NUMBER;
                    } else { // only single dot
                        state = INIT;
                        return JSTokenContext.DOT;
                    }
                    break;
                    
                case ISI_REGEXP:
                    
                    switch(actChar){
                        case '\\':
                            state = ISI_REGEXP_A_BSLASH;
                            break;
                        case '/':
                            char ch = buffer[offset+1];
                            if(ch=='g' || ch=='i'){
                                ch = buffer[offset+2];
                                if(ch=='g' || ch=='i'){
                                    offset+=3;
                                }else{
                                    offset+=2;
                                }
                            }else{
                                offset++;
                            }
                            state = INIT;
                            return JSTokenContext.REGEXP;
                    }
                    
                    break;
                    
                case ISI_REGEXP_A_BSLASH:
                    
                    switch(actChar){
                        case '/':
                            break;
                        default:
                            offset--;
                    }
                    state = ISI_REGEXP;
                    break;
                    
            } // end of switch(state)
            
            offset++;
        } // end of while(offset...)
        
        /** At this stage there's no more text in the scanned buffer.
         * Scanner first checks whether this is completely the last
         * available buffer.
         */
        
        if (lastBuffer) {
            switch(state) {
                case ISI_WHITESPACE:
                    state = INIT;
                    return JSTokenContext.WHITESPACE;
                case ISI_IDENTIFIER:
                    state = INIT;
                    TokenID kwd = matchKeyword(buffer, tokenOffset, offset - tokenOffset);
                    return (kwd != null) ? kwd : JSTokenContext.IDENTIFIER;
                case ISI_LINE_COMMENT:
                    return JSTokenContext.LINE_COMMENT; // stay in line-comment state
                case ISI_BLOCK_COMMENT:
                case ISA_STAR_I_BLOCK_COMMENT:
                    return JSTokenContext.BLOCK_COMMENT; // stay in block-comment state
                case ISI_CHAR:
                case ISI_CHAR_A_BSLASH:
                    return JSTokenContext.CHAR_LITERAL; // hold the state
                case ISI_STRING:
                case ISI_STRING_A_BSLASH:
                    return JSTokenContext.STRING_LITERAL; // hold the state
                case ISA_ZERO:
                case ISI_NUMBER:
                    state = INIT;
                    return JSTokenContext.NUMBER_LITERAL;
                case ISI_HEX:
                    state = INIT;
                    return JSTokenContext.HEX_LITERAL;
                case ISA_DOT:
                    state = INIT;
                    return JSTokenContext.DOT;
                case ISA_SLASH:
                    state = INIT;
                    return JSTokenContext.DIV;
                case ISA_EQ:
                    state = INIT;
                    return JSTokenContext.EQ;
                case ISA_GT:
                    state = INIT;
                    return JSTokenContext.GT;
                case ISA_GTGT:
                    state = INIT;
                    return JSTokenContext.RSSHIFT;
                case ISA_GTGTGT:
                    state = INIT;
                    return JSTokenContext.RUSHIFT;
                case ISA_LT:
                    state = INIT;
                    return JSTokenContext.LT;
                case ISA_LTLT:
                    state = INIT;
                    return JSTokenContext.LSHIFT;
                case ISA_PLUS:
                    state = INIT;
                    return JSTokenContext.PLUS;
                case ISA_MINUS:
                    state = INIT;
                    return JSTokenContext.MINUS;
                case ISA_STAR:
                    state = INIT;
                    return JSTokenContext.MUL;
                case ISA_PIPE:
                    state = INIT;
                    return JSTokenContext.OR;
                case ISA_PERCENT:
                    state = INIT;
                    return JSTokenContext.MOD;
                case ISA_AND:
                    state = INIT;
                    return JSTokenContext.AND;
                case ISA_XOR:
                    state = INIT;
                    return JSTokenContext.XOR;
                case ISA_EXCLAMATION:
                    state = INIT;
                    return JSTokenContext.NOT;
                case ISI_REGEXP:
                    state = INIT;
                    return JSTokenContext.REGEXP;
            }
        }
        
        /* At this stage there's no more text in the scanned buffer, but
         * this buffer is not the last so the scan will continue on another buffer.
         * The scanner tries to minimize the amount of characters
         * that will be prescanned in the next buffer by returning the token
         * where possible.
         */
        
        switch (state) {
            case ISI_WHITESPACE:
                return JSTokenContext.WHITESPACE;
        }
        
        return null; // nothing found
    }
    
    
    
    
    private TokenID matchKeyword(char[] buffer, int offset, int len) {
        
        if (len > 10 || len <= 1) return null;
        
        switch (buffer[offset++]) {
            
            // break
            case 'b':
                
                return (len == 5
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'k')
                        ? JSTokenContext.BREAK : null;
                
                
                // case catch continue
            case 'c':
                
                if (len <= 3) return null;
                
                switch (buffer[offset++]) {
                    case 'a':
                        switch (buffer[offset++]) {
                            
                            // case
                            case 's':
                                return (len == 4
                                        && buffer[offset++] == 'e')
                                        ? JSTokenContext.CASE : null;
                                
                                // catch
                            case 't':
                                return (len == 5
                                        && buffer[offset++] == 'c'
                                        && buffer[offset++] == 'h')
                                        ? JSTokenContext.CATCH : null;
                                
                            default: return null;
                        }
                        
                        //continue
                    case 'o':
                        
                        return (len == 8
                                && buffer[offset++] == 'n'
                                && buffer[offset++] == 't'
                                && buffer[offset++] == 'i'
                                && buffer[offset++] == 'n'
                                && buffer[offset++] == 'u'
                                && buffer[offset++] == 'e')
                                ? JSTokenContext.CONTINUE : null;
                        
                    default: return null;
                    
                }
                
                
                // default delete do
            case 'd':
                switch (buffer[offset++]) {
                    case 'e':
                        
                        switch (buffer[offset++]) {
                            
                            // default
                            case 'f' :
                                return (len == 7
                                        && buffer[offset++] == 'a'
                                        && buffer[offset++] == 'u'
                                        && buffer[offset++] == 'l'
                                        && buffer[offset++] == 't')
                                        ? JSTokenContext.DEFAULT : null;
                                
                                // delete
                            case 'l' :
                                return (len == 6
                                        && buffer[offset++] == 'e'
                                        && buffer[offset++] == 't'
                                        && buffer[offset++] == 'e')
                                        ? JSTokenContext.DELETE : null;
                                
                            default: return null;
                        }
                        
                        // do
                    case 'o':
                        return (len == 2) ? JSTokenContext.DO : null;
                    default:
                        return null;
                }
                
                // else
            case 'e':
                
                return (len == 4
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 's'
                        && buffer[offset++] == 'e')
                        ? JSTokenContext.ELSE : null;
                
                
                // false finally for function
            case 'f':
                if (len <= 2) return null;
                
                switch (buffer[offset++]) {
                    
                    // false
                    case 'a':
                        return (len == 5
                                && buffer[offset++] == 'l'
                                && buffer[offset++] == 's'
                                && buffer[offset++] == 'e')
                                ? JSTokenContext.FALSE : null;
                        
                        // finally
                    case 'i':
                        return (len == 7
                                && buffer[offset++] == 'n'
                                && buffer[offset++] == 'a'
                                && buffer[offset++] == 'l'
                                && buffer[offset++] == 'l'
                                && buffer[offset++] == 'y')
                                ? JSTokenContext.FINALLY : null;
                        
                        // for
                    case 'o':
                        return (len == 3  && buffer[offset++] == 'r') ? JSTokenContext.FOR : null;
                        
                        // function
                    case 'u':
                        return (len == 8
                                && buffer[offset++] == 'n'
                                && buffer[offset++] == 'c'
                                && buffer[offset++] == 't'
                                && buffer[offset++] == 'i'
                                && buffer[offset++] == 'o'
                                && buffer[offset++] == 'n')
                                ? JSTokenContext.FUNCTION : null;
                        
                    default: return null;
                }
                
                // if  in instanceof
            case 'i':
                
                switch (buffer[offset++]) {
                    
                    //if
                    case 'f':
                        return (len == 2) ? JSTokenContext.IF : null;
                    case 'n':
                        
                        // in
                        if(len == 2) return JSTokenContext.IN;
                        
                        // instanceof
                        return (len == 10
                                && buffer[offset++] == 's'
                                && buffer[offset++] == 't'
                                && buffer[offset++] == 'a'
                                && buffer[offset++] == 'n'
                                && buffer[offset++] == 'c'
                                && buffer[offset++] == 'e'
                                && buffer[offset++] == 'o'
                                && buffer[offset++] == 'f')
                                ? JSTokenContext.INSTANCEOF : null;
                        
                    default: return null;
                }
                
                // new null
            case 'n':
                
                if (len <= 2) return null;
                
                switch (buffer[offset++]) {
                    
                    // new
                    case 'e':
                        return (len == 3
                                && buffer[offset++] == 'w')
                                ? JSTokenContext.NEW : null;
                        
                        // null
                    case 'u':
                        return (len == 4
                                && buffer[offset++] == 'l'
                                && buffer[offset++] == 'l')
                                ? JSTokenContext.NULL : null;
                        
                    default: return null;
                }
                
                // return
            case 'r':
                
                // return
                return (len == 6
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 'n')
                        ? JSTokenContext.RETURN : null;
                
                
            case 's':
                
                return (len == 6
                        && buffer[offset++] == 'w'
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 'h')
                        ? JSTokenContext.SWITCH : null;
                
                // try this true typeof
            case 't':
                
                if (len <= 2) return null;
                
                switch (buffer[offset++]) {
                    case 'h':
                        if (len <= 3) return null;
                        
                        switch (buffer[offset++]) {
                            
                            // this
                            case 'i':
                                return (len == 4
                                        && buffer[offset++] == 's')
                                        ? JSTokenContext.THIS : null;
                                
                                // throw
                            case 'r':
                                return (len == 5
                                        && buffer[offset++] == 'o'
                                        && buffer[offset++] == 'w')
                                        ? JSTokenContext.THROW : null;
                                
                            default: return null;
                        }
                        
                    case 'r':
                        switch (buffer[offset++]) {
                            
                            // true
                            case 'u':
                                return (len == 4
                                        && buffer[offset++] == 'e')
                                        ? JSTokenContext.TRUE : null;
                                
                                // try
                            case 'y':
                                return (len == 3)
                                ? JSTokenContext.TRY : null;
                                
                            default: return null;
                        }
                    default:
                        return null;
                }
            case 'v':
                
                // var
                return (len == 3
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'r')
                        ? JSTokenContext.VAR : null;
                
            case 'w':
                
                // while
                return (len == 5
                        && buffer[offset++] == 'h'
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 'e')
                        ? JSTokenContext.WHILE : null;
                
            default: return null;
        }
    }
}

