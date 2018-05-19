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
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenItem;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.ext.AbstractFormatLayer;
import org.netbeans.editor.ext.ExtFormatter;
import org.netbeans.editor.ext.FormatSupport;
import org.netbeans.editor.ext.FormatTokenPosition;
import org.netbeans.editor.ext.FormatWriter;

/**
 * @author Miloslav Metelka
 * @author Nicolas D�sy
 */
public class JSFormatter extends ExtFormatter {

    public JSFormatter(Class kitClass) {
        super(kitClass);
    }

    protected boolean acceptSyntax(Syntax syntax) {
        return (syntax instanceof JSSyntax);
    }

    public int[] getReformatBlock(JTextComponent target, String typedText) {
        int[] ret = null;
        BaseDocument doc = Utilities.getDocument(target);
        int dotPos = target.getCaret().getDot();
        if (doc != null) {
            /* Check whether the user has written the ending 'e'
             * of the first 'else' on the line.
             */
            if ("e".equals(typedText)) { // NOI18N
                try {
                    int fnw = Utilities.getRowFirstNonWhite(doc, dotPos);
                    if (fnw >= 0 && fnw + 4 == dotPos
                        && "else".equals(doc.getText(fnw, 4)) // NOI18N
                    ) {
                        ret = new int[] { fnw, fnw + 4 };
                    }
                } catch (BadLocationException e) {
                }

            } else if (":".equals(typedText)) { // NOI18N
                try {
                    int fnw = Utilities.getRowFirstNonWhite(doc, dotPos);
                    if (fnw >= 0 && fnw + 4 <= doc.getLength()
                        && "case".equals(doc.getText(fnw, 4)) // NOI18N
                    ) {
                        ret = new int[] { fnw, fnw + 4 };
                    } else {
                        if (fnw >= 0 & fnw + 7 <= doc.getLength()
                            && "default".equals(doc.getText(fnw, 7)) // NOI18N
                        ) {
                            ret = new int[] {fnw, fnw + 7 };
                        }
                    }
                } catch (BadLocationException e) {
                }
            
            } else {
                ret = super.getReformatBlock(target, typedText);
            }
        }
        
        return ret;
    }

    protected void initFormatLayers() {
        addFormatLayer(new StripEndWhitespaceLayer());
        addFormatLayer(new JSLayer());
    }

 
    public FormatSupport createFormatSupport(FormatWriter fw) {
        return new JSFormatSupport(fw);
    }

    public class StripEndWhitespaceLayer extends AbstractFormatLayer {

        public StripEndWhitespaceLayer() {
            super("javascript-strip-whitespace-at-line-end"); // NOI18N
        }

        /*
        protected FormatSupport createFormatSupport(FormatWriter fw) {
            return new JavaFormatSupport(fw);
        }
        */
        public void format(FormatWriter fw) {
            JSFormatSupport jfs = (JSFormatSupport)createFormatSupport(fw);            
            if(jfs==null) return;
            FormatTokenPosition pos = jfs.getFormatStartPosition();
            if (jfs.isIndentOnly()) { // don't do anything

            } else { // remove end-line whitespace
                while (pos.getToken() != null) {
                    FormatTokenPosition startPos = pos;
                    pos = jfs.removeLineEndWhitespace(pos);
                    if (pos.getToken() != null) {
                        pos = jfs.getNextPosition(pos);
                    }
                    // fix for issue 14725
                    // this is more hack than correct fix. It happens that 
                    // jfs.removeLineEndWhitespace() does not move to next
                    // position. The reason is that token from which the 
                    // endline whitespaces must be removed is not 'modifiable' - 
                    // FormatWritter.canModifyToken() returns false in
                    // FormatWritter.remove. I don't dare to fix this problem 
                    // in ExtFormatSupport and so I'm patching this
                    // loop to check whether we are still on the same position
                    // and if we are, let's do break. If similar problem reappear
                    // we will have to find better fix. Hopefully, with the planned
                    // conversion of indentation engines to new lexel module
                    // all this code will be replaced in next verison.
                    if (startPos.equals(pos)) {
                        break;
                    }
                }
            }
        }

    }

    public class JSLayer extends AbstractFormatLayer {

        public JSLayer() {
            super("javascript-layer"); // NOI18N
        }

        protected FormatSupport createFormatSupport(FormatWriter fw) {
            return new JSFormatSupport(fw);
        }

        public void format(FormatWriter fw) {
            try {
                JSFormatSupport jfs = (JSFormatSupport)createFormatSupport(fw);

                FormatTokenPosition pos = jfs.getFormatStartPosition();

                if (jfs.isIndentOnly()) {  // create indentation only
                    jfs.indentLine(pos);

                } else { // regular formatting

                    while (pos != null) {

                        // Indent the current line
                        jfs.indentLine(pos);

                        // Format the line by additional rules
                        formatLine(jfs, pos);

                        // Goto next line
                        FormatTokenPosition pos2 = jfs.findLineEnd(pos);
                        if (pos2 == null || pos2.getToken() == null)
                            break; // the last line was processed
                        
                        pos = jfs.getNextPosition(pos2, javax.swing.text.Position.Bias.Forward);
                        if (pos == pos2)
                            break; // in case there is no next position
                        if (pos == null || pos.getToken() == null)
                            break; // there is nothing after the end of line
                        
                        FormatTokenPosition fnw = jfs.findLineFirstNonWhitespace(pos);
                        if (fnw != null) {
                          pos = fnw;
                        } else { // no non-whitespace char on the line
                          pos = jfs.findLineStart(pos);
                        }
                    }
                }
            } catch (IllegalStateException e) {
            }
        }


        protected void formatLine(JSFormatSupport jfs, FormatTokenPosition pos) {
            TokenItem token = jfs.findLineStart(pos).getToken();
            while (token != null) {
/*                if (jfs.findLineEnd(jfs.getPosition(token, 0)).getToken() == token) {
                    break; // at line end
                }
 */

                if (token.getTokenContextPath() == jfs.getTokenContextPath()) {
                    switch (token.getTokenID().getNumericID()) {
                        case JSTokenContext.LBRACE_ID: // '{'
                            if (!jfs.isIndentOnly()) {
                            if (jfs.getFormatNewlineBeforeBrace()) {
                                FormatTokenPosition lbracePos = jfs.getPosition(token, 0);
                                // Look for first important token in backward direction
                                FormatTokenPosition imp = jfs.findImportant(lbracePos,
                                        null, true, true); // stop on line start
                                if (imp != null && imp.getToken().getTokenContextPath()
                                                        == jfs.getTokenContextPath()
                                ) {
                                    switch (imp.getToken().getTokenID().getNumericID()) {
                                        case JSTokenContext.BLOCK_COMMENT_ID:
                                        case JSTokenContext.LINE_COMMENT_ID:
                                            break; // comments are ignored

                                        case JSTokenContext.RBRACKET_ID:
                                            break; // array initializtion "ttt [] {...}"

                                        default:
                                            // Check whether it isn't a "{ }" case
                                            FormatTokenPosition next = jfs.findImportant(
                                                    lbracePos, null, true, false);
                                            if (next == null || next.getToken() == null
                                                || next.getToken().getTokenID() != JSTokenContext.RBRACE
                                            ) {
                                                // Insert new-line
                                                if (jfs.canInsertToken(token)) {
                                                    jfs.insertToken(token, jfs.getValidWhitespaceTokenID(),
                                                        jfs.getValidWhitespaceTokenContextPath(), "\n"); // NOI18N
                                                    jfs.removeLineEndWhitespace(imp);
                                                    // bug fix: 10225 - reindent newly created line
                                                    jfs.indentLine(lbracePos);
                                                }

                                                //token = imp.getToken();
                                            }
                                            break;
                                    }
                                }

                            } else {
                                FormatTokenPosition lbracePos = jfs.getPosition(token, 0);
                                
                                // Check that nothing exists before "{"
                                if (jfs.findNonWhitespace(lbracePos, null, true, true) != null)
                                    break;
                                // Check that nothing exists after "{", but ignore comments
                                if (jfs.getNextPosition(lbracePos) != null)
                                    if (jfs.findImportant(jfs.getNextPosition(lbracePos), null, true, false) != null)
                                        break;
                                
                                // check that on previous line is some stmt
                                FormatTokenPosition ftp = jfs.findLineStart(lbracePos); // find start of current line
                                FormatTokenPosition endOfPreviousLine = jfs.getPreviousPosition(ftp); // go one position back - means previous line
                                if (endOfPreviousLine == null || endOfPreviousLine.getToken().getTokenID() != JSTokenContext.WHITESPACE)
                                    break;
                                ftp = jfs.findLineStart(endOfPreviousLine); // find start of the previous line - now we have limit position
                                ftp = jfs.findImportant(lbracePos, ftp, false, true); // find something important till the limit
                                if (ftp == null)
                                    break;
                                
                                // check that previous line does not end with "{" or line comment
                                ftp = jfs.findNonWhitespace(endOfPreviousLine, null, true, true);
                                if (ftp.getToken().getTokenID() == JSTokenContext.LINE_COMMENT ||
                                    ftp.getToken().getTokenID() == JSTokenContext.LBRACE)
                                    break;

                                // now move the "{" to the end of previous line
                                boolean remove = true;
                                while (remove)
                                {
                                    if (token.getPrevious() == endOfPreviousLine.getToken())
                                        remove = false;
                                    if (jfs.canRemoveToken(token.getPrevious()))
                                        jfs.removeToken(token.getPrevious());
                                    else
                                        break;  // should never get here!
                                }
                                // insert one space before "{"
                                if (jfs.canInsertToken(token))
                                    jfs.insertSpaces(token, 1);
                            }
                            } // !jfs.isIndentOnly()
                            break;

                        case JSTokenContext.LPAREN_ID:
                            if (jfs.getFormatSpaceBeforeParenthesis()) {
                                TokenItem prevToken = token.getPrevious();
                                if (prevToken != null && 
                                    (prevToken.getTokenID() == JSTokenContext.IDENTIFIER ||
                                    prevToken.getTokenID() == JSTokenContext.THIS) ) {
                                    if (jfs.canInsertToken(token)) {
                                        jfs.insertToken(token, jfs.getWhitespaceTokenID(),
                                            jfs.getWhitespaceTokenContextPath(), " "); // NOI18N
                                    }
                                }
                            } else {
                                // bugfix 9813: remove space before left parenthesis
                                TokenItem prevToken = token.getPrevious();
                                if (prevToken != null && prevToken.getTokenID() == JSTokenContext.WHITESPACE &&
                                        prevToken.getImage().length() == 1) {
                                    TokenItem prevprevToken = prevToken.getPrevious();
                                    if (prevprevToken != null && 
                                        (prevprevToken.getTokenID() == JSTokenContext.IDENTIFIER ||
                                        prevprevToken.getTokenID() == JSTokenContext.THIS) )
                                    {
                                        if (jfs.canRemoveToken(prevToken)) {
                                            jfs.removeToken(prevToken);
                                        }
                                    }
                                }
                                
                            }
                            break;
                    }
                }

                token = token.getNext();
            }
        }

    }

}