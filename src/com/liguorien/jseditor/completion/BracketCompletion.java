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

import com.liguorien.jseditor.JSTokenContext;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Settings;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.TokenItem;
import org.netbeans.editor.TokenProcessor;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.ext.ExtSyntaxSupport;
import org.netbeans.modules.editor.java.JavaKit;

/**
 * @author Nicolas Désy
 */
public class BracketCompletion {
    
    /**
     * A hook method called after a character was inserted into the
     * document. The function checks for special characters for
     * completion ()[]'"{} and other conditions and optionally performs
     * changes to the doc and or caret (complets braces, moves caret,
     * etc.)
     * @param doc the document where the change occurred
     * @param dotPos position of the character insertion
     * @param caret caret
     * @param ch the character that was inserted
     * @throws BadLocationException if dotPos is not correct
     */
    public static void charInserted(BaseDocument doc,
            int dotPos,
            Caret caret,
            char ch) throws BadLocationException {
        if (doc.getSyntaxSupport() instanceof ExtSyntaxSupport && completionSettingEnabled()) {
            
            if (ch == ')'|| ch == ']'|| ch =='('|| ch =='[') {
                TokenID tokenAtDot = ((ExtSyntaxSupport)doc.getSyntaxSupport()).getTokenID(dotPos);
                
                if (tokenAtDot == JSTokenContext.RBRACKET || tokenAtDot == JSTokenContext.RPAREN) {
                    skipClosingBracket(doc, dotPos, caret, ch);
                } else if (tokenAtDot == JSTokenContext.LBRACKET || tokenAtDot == JSTokenContext.LPAREN) {
                    completeOpeningBracket(doc, dotPos, caret, ch);
                }
            } else if (ch == '\"' || ch == '\'') {
                completeQuote(doc, dotPos, caret, ch);
            } else if (ch == ';') {
                moveSemicolon(doc, dotPos, caret);
            }
        }
    }
    
    private static void moveSemicolon(BaseDocument doc, int dotPos, Caret caret) throws BadLocationException {
        int eolPos = Utilities.getRowEnd(doc, dotPos);
        ExtSyntaxSupport ssup = (ExtSyntaxSupport)doc.getSyntaxSupport();
        int lastParenPos = dotPos;
        TokenItem token = ssup.getTokenChain(dotPos, eolPos);
        for (TokenItem item = token.getNext(); item != null && item.getOffset() <= eolPos; item = item.getNext()) {
            TokenID tokenID = item.getTokenID();
            if (tokenID == JSTokenContext.RPAREN) {
                lastParenPos = item.getOffset();
            } else if (tokenID != JSTokenContext.WHITESPACE) {
                return;
            }
        }
        if (isForLoopSemicolon(token)) {
            return;
        }
        doc.remove(dotPos, 1);
        doc.insertString(lastParenPos, ";", null); // NOI18N
        caret.setDot(lastParenPos + 1);
    }
    
    private static boolean isForLoopSemicolon(TokenItem token) {
        if (token == null || token.getTokenID() != JSTokenContext.SEMICOLON) {
            return false;
        }
        int parDepth = 0; // parenthesis depth
        int braceDepth = 0; // brace depth
        boolean semicolonFound = false; // next semicolon
        token = token.getPrevious(); // ignore this semicolon
        while (token != null) {
            if (token.getTokenID() == JSTokenContext.LPAREN) {
                if (parDepth == 0) { // could be a 'for ('
                    token = token.getPrevious();
                    while(token !=null && (token.getTokenID() == JSTokenContext.WHITESPACE || token.getTokenID() == JSTokenContext.BLOCK_COMMENT || token.getTokenID() == JSTokenContext.LINE_COMMENT)) {
                        token = token.getPrevious();
                    }
                    if (token.getTokenID() == JSTokenContext.FOR) {
                        return true;
                    }
                    return false;
                } else { // non-zero depth
                    parDepth--;
                }
            } else if (token.getTokenID() == JSTokenContext.RPAREN) {
                parDepth++;
            } else if (token.getTokenID() == JSTokenContext.LBRACE) {
                if (braceDepth == 0) { // unclosed left brace
                    return false;
                }
                braceDepth--;
            } else if (token.getTokenID() == JSTokenContext.RBRACE) {
                braceDepth++;
                
            } else if (token.getTokenID() == JSTokenContext.SEMICOLON) {
                if (semicolonFound) { // one semicolon already found
                    return false;
                }
                semicolonFound = true;
            }
            token = token.getPrevious();
        }
        return false;
    }
    
    /**
     * Hook called after a character *ch* was backspace-deleted from
     * *doc*. The function possibly removes bracket or quote pair if
     * appropriate.
     * @param doc the document
     * @param dotPos position of the change
     * @param caret caret
     * @param ch the character that was deleted
     */
    static void charBackspaced(BaseDocument doc,
            int dotPos,
            Caret caret,
            char ch) throws BadLocationException {
        if (completionSettingEnabled()) {
            if (ch == '(' || ch == '[') {
                TokenID tokenAtDot = ((ExtSyntaxSupport)doc.
                        getSyntaxSupport()).getTokenID(dotPos);
                if ((tokenAtDot == JSTokenContext.RBRACKET && tokenBalance(doc,0, doc.getLength(), JSTokenContext.LBRACKET, JSTokenContext.RBRACKET) != 0) ||
                        (tokenAtDot == JSTokenContext.RPAREN && tokenBalance(doc,0, doc.getLength(), JSTokenContext.LPAREN, JSTokenContext.RPAREN) != 0) ) {
                    doc.remove(dotPos, 1);
                }
            } else if (ch == '\"' || ch == '\'') {
                char match [] = doc.getChars(dotPos, 1);
                if (match != null && (match[0] == '\"' || match[0] == '\'')) {
                    doc.remove(dotPos, 1);
                }
            }
        }
    }
    
    
    /**
     * A function to complete opening curly bracket. Various conditions
     * are checked and the pairing curly is inserted if appropriate.
     * @param doc the document
     * @param dotPos position of the opening {
     * @param caret
     */
    static void completeOpeningCurly(BaseDocument doc, int dotPos, Caret caret)
    throws BadLocationException {
        if (completionSettingEnabled()) {
            TokenID tokenAtDot =
                    ((ExtSyntaxSupport)doc.getSyntaxSupport()).getTokenID(dotPos);
            
            if (tokenAtDot == JSTokenContext.LBRACE &&  braceBalance(doc,0, doc.getLength()) > 0) {
                int newPos = dotPos + 1;
                doc.insertString(newPos,"}", null); // NOI18N
                doc.getFormatter().indentNewLine(doc, newPos);
                caret.setDot(dotPos+1);
            }
        }
    }
    
    /**
     * Counts the number of braces starting at dotPos to the end of the
     * document. Every occurence of { increses the count by 1, every
     * occurrence of } decreses the count by 1. The result is returned.
     * @return The number of { - number of } (>0 more { than } ,<0 more } than {)
     */
    private static int braceBalance(BaseDocument doc, int dotPos, int length)
    throws BadLocationException {
        return tokenBalance(doc, dotPos, length, JSTokenContext.LBRACE, JSTokenContext.RBRACE);
    }
    
    /**
     * The same as braceBalance but generalized to any pair of matching
     * tokens.
     * @param open the token that increses the count
     * @param close the token that decreses the count
     */
    private static int tokenBalance(BaseDocument doc, int dotPos, int length,
            TokenID open, TokenID close)
            throws BadLocationException {
        ExtSyntaxSupport ssup = (ExtSyntaxSupport)doc.getSyntaxSupport();
        int counter = 0;
        int endOffset = dotPos + length;
        for (TokenItem it = ssup.getTokenChain(dotPos, endOffset);
        it != null && it.getOffset() < endOffset;
        it = it.getNext()) {
            if (it.getTokenID() == open) counter ++;
            else if (it.getTokenID() == close) counter--;
        }
        
        return counter;
    }
    
    
    /**
     * A hook to be called after closing bracket ) or ] was inserted to
     * the document. The method checks if the bracket should stay there
     * or be removed and some exisitng bracket just skipped.
     *
     * @param doc the document
     * @param dotPos position of the inserted bracket
     * @param caret caret
     * @param theBracket the bracket character ']' or ')'
     */
    private static void skipClosingBracket(BaseDocument doc, int dotPos,
            Caret caret, char theBracket)
            throws BadLocationException {
        // try to match every same bracket from dot on
        
        boolean nomatch = false;
        int dp ;
        for (dp = dotPos; dp <= doc.getLength(); dp++) {
            char [] chars = doc.getChars(dp, 1);
            if (chars == null || chars[0] != theBracket) break;
        }
        
        dp--;
        // dp points to the last position with bracket
        if (dp > dotPos) {
            ExtSyntaxSupport sup = (ExtSyntaxSupport)doc.getSyntaxSupport();
            // test the last bracket for match
            int [] block = sup.findMatchingBlock(dp, false);
            
      /* Fix of #47879
       * If the following case occurs:
       *     SwingUtilities.invokeLater(new Runnable() {
       *         public void run(|)
       *     })
       * after typing right bracket ')' the bracket should be consumed
       * but it would not be because it would match
       * the '(' right before 'new'.
       * Therefore there is a search necessary
       * for unclosed '{' as well in the area of the match block.
       */
            if (block != null) {
                int leftBracketOffset = block[0];
                assert (leftBracketOffset < dp);
                int balance = braceBalance(doc, leftBracketOffset, dp - leftBracketOffset);
                if (balance != 0) {
                    block = null; // make it think that the inserted bracket breakes source consistency
                }
            }
            
            if (block == null) { // doesn't match with the bracket
                // did it match before?
                block = sup.findMatchingBlock(dp-1, false);
                if (block != null) { // it did, the new bracket would violate
                    // source consistency, remove it
                    doc.remove(dotPos, 1);
                    caret.setDot(dotPos+1);
                }
            }
        }
    }
    
    /**
     * Check for various conditions and possibly add a pairing bracket
     * to the already inserted.
     * @param doc the document
     * @param dotPos position of the opening bracket (already in the doc)
     * @param caret caret
     * @param theBracket the bracket that was inserted
     */
    private static void completeOpeningBracket(BaseDocument doc,
            int dotPos,
            Caret caret,
            char theBracket) throws BadLocationException {
        if (isCompletablePosition(doc, dotPos+1)) {
            String matchinBracket = "" + matching(theBracket);
            doc.insertString(dotPos + 1, matchinBracket,null);
            caret.setDot(dotPos+1);
        }
    }
    
    /**
     * Check for conditions and possibly complete an already inserted
     * quote .
     * @param doc the document
     * @param dotPos position of the opening bracket (already in the doc)
     * @param caret caret
     * @param theBracket the character that was inserted
     */
    private static void completeQuote(BaseDocument doc, int dotPos, Caret caret,
            char theBracket)
            throws BadLocationException {
        if (posWithinQuotes(doc,
                dotPos+1,
                theBracket,
                (theBracket =='\"' ? JSTokenContext.STRING_LITERAL : JSTokenContext.CHAR_LITERAL)) &&
                isCompletablePosition(doc, dotPos+1)) {
            doc.insertString(dotPos + 1, "" + theBracket ,null);
            caret.setDot(dotPos+1);
        } else {
            char [] charss = doc.getChars(dotPos+1, 1);
            // System.out.println("NOT Within string, " + new String(charss));
            if (charss != null && charss[0] == theBracket) {
                doc.remove(dotPos+1, 1);
            }
        }
    }
    
    /**
     * Checks whether dotPos is a position at which bracket and quote
     * completion is performed. Brackets and quotes are not completed
     * everywhere but just at suitable places .
     * @param doc the document
     * @param dotPos position to be tested
     */
    private static boolean isCompletablePosition(BaseDocument doc, int dotPos)
    throws BadLocationException {
        if (dotPos == doc.getLength()) // there's no other character to test
            return true;
        else {
            // test that we are in front of ) , " or '
            char chr = doc.getChars(dotPos,1)[0];
            return (chr == ')' ||
                    chr == ',' ||
                    chr == '\"'||
                    chr == '\''||
                    chr == ' ' ||
                    chr == ']' ||
                    chr == '}' ||
                    chr == '\n'||
                    chr == '\t'||
                    chr == ';');
        }
    }
    
    
    /**
     * Returns true if bracket completion is enabled in options.
     */
    private static boolean completionSettingEnabled() {
        return true;
        //return ((Boolean)Settings.getValue(JavaKit.class, JavaSettingsNames.PAIR_CHARACTERS_COMPLETION)).booleanValue();
    }
    
    /**
     * Returns for an opening bracket or quote the appropriate closing
     * character.
     */
    private static char matching(char theBracket) {
        switch (theBracket) {
            case '(' : return ')';
            case '[' : return ']';
            case '\"' : return '\"'; // NOI18N
            case '\'' : return '\'';
            default:  return ' ';
        }
    }
    
    
    
    /**
     * posWithinString(doc, pos) iff position *pos* is within a string
     * literal in document doc.
     * @param doc the document
     * @param dotPos position to be tested
     */
    static  boolean posWithinString(BaseDocument doc, int dotPos) {
        return posWithinQuotes(doc, dotPos, '\"', JSTokenContext.STRING_LITERAL);
    }
    
    /**
     * Generalized posWithingString to any token and delimiting
     * character. It works for tokens are delimited by *quote* and
     * extend up to the other *quote* or whitespace in case of an
     * incomplete token.
     * @param doc the document
     * @param dotPos position to be tested
     */
    static  boolean posWithinQuotes(BaseDocument doc, int dotPos, char quote, TokenID tokenID) {
        try {
            MyTokenProcessor proc = new MyTokenProcessor();
            doc.getSyntaxSupport().tokenizeText( proc,
                    dotPos-1,
                    doc.getLength(), true);
            return proc.tokenID == tokenID &&
                    (dotPos - proc.tokenStart == 1 || doc.getChars(dotPos-1,1)[0]!=quote);
        } catch (BadLocationException ex) {
            return false;
        }
    }
    
    /**
     * A token processor used to find out the length of a token.
     */
    static class MyTokenProcessor implements TokenProcessor {
        public TokenID tokenID = null;
        public int tokenStart = -1;
        
        public boolean token(TokenID tokenID, TokenContextPath tcp,
                int tokBuffOffset, int tokLength) {
            this.tokenStart = tokenBuffer2DocumentOffset(tokBuffOffset);
            this.tokenID = tokenID;
            
            // System.out.println("token " + tokenID.getName() + " at " + tokenStart + " (" +
            //		 tokBuffOffset + ") len:" + tokLength);
            
            
            return false;
        }
        
        public int eot(int offset) { // System.out.println("EOT");
            return 0;}
        
        public void nextBuffer(char [] buffer, int offset, int len, int startPos, int preScan, boolean lastBuffer) {
            // System.out.println("nextBuffer "+ new String(buffer) + "," + offset + "len: " + len + " startPos:"+startPos + " preScan:" + preScan + " lastBuffer:" + lastBuffer);
            
            this.bufferStartPos = startPos - offset;
        }
        
        private int bufferStartPos = 0;
        private int tokenBuffer2DocumentOffset(int offs) { return offs + bufferStartPos;}
    }
    
    
}