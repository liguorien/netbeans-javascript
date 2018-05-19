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

import org.netbeans.editor.BaseImageTokenID;
import org.netbeans.editor.BaseTokenCategory;
import org.netbeans.editor.BaseTokenID;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.Utilities;

/**
 * @author Nicolas Désy
 */
public class JSTokenContext extends TokenContext {
    
    
    private static final String operators = "operators"; 
    private static final String keywords = "keywords"; 
    
    // Token category-ids
    public static final int KEYWORDS_ID  = 1;
    public static final int OPERATORS_ID = KEYWORDS_ID + 1;
    public static final int NUMERIC_LITERALS_ID = OPERATORS_ID + 1;
    public static final int ERRORS_ID = NUMERIC_LITERALS_ID + 1;
    
    // Numeric-ids for token-ids
    public static final int WHITESPACE_ID = ERRORS_ID + 1;
    public static final int IDENTIFIER_ID = WHITESPACE_ID + 1;
    public static final int LINE_COMMENT_ID = IDENTIFIER_ID + 1;
    public static final int BLOCK_COMMENT_ID = LINE_COMMENT_ID + 1;
    public static final int STRING_LITERAL_ID = BLOCK_COMMENT_ID + 1;
    public static final int CHAR_LITERAL_ID = STRING_LITERAL_ID + 1;
    public static final int NUMBER_LITERAL_ID = CHAR_LITERAL_ID + 1;
    public static final int HEX_LITERAL_ID = NUMBER_LITERAL_ID + 1;
    public static final int METHOD_ID = HEX_LITERAL_ID + 1;
    public static final int REGEXP_ID = METHOD_ID + 1;
    
    // Operator numeric-ids
    public static final int EQ_ID = REGEXP_ID + 1; // =
    public static final int LT_ID = EQ_ID + 1;            // <
    public static final int GT_ID = LT_ID + 1;            // >
    public static final int LSHIFT_ID = GT_ID + 1;        // <<
    public static final int RSSHIFT_ID = LSHIFT_ID + 1;   // >>
    public static final int RUSHIFT_ID = RSSHIFT_ID + 1;  // >>>
    public static final int PLUS_ID = RUSHIFT_ID + 1;     // +
    public static final int MINUS_ID = PLUS_ID + 1;       // -
    public static final int MUL_ID = MINUS_ID + 1;        // *
    public static final int DIV_ID = MUL_ID + 1;          // /
    public static final int AND_ID = DIV_ID + 1;          // &
    public static final int OR_ID = AND_ID + 1;           // |
    public static final int XOR_ID = OR_ID + 1;           // ^
    public static final int MOD_ID = XOR_ID + 1;          // %
    public static final int NOT_ID = MOD_ID + 1;          // !
    public static final int NEG_ID = NOT_ID + 1;          // ~
    public static final int EQ_EQ_ID = NEG_ID + 1;        // ==
    public static final int LT_EQ_ID = EQ_EQ_ID + 1;      // <=
    public static final int GT_EQ_ID = LT_EQ_ID + 1;         // >=
    public static final int LSHIFT_EQ_ID = GT_EQ_ID + 1;  // <<=
    public static final int RSSHIFT_EQ_ID = LSHIFT_EQ_ID + 1; // >>=
    public static final int RUSHIFT_EQ_ID = RSSHIFT_EQ_ID + 1; // >>>=
    public static final int PLUS_EQ_ID = RUSHIFT_EQ_ID + 1; // +=
    public static final int MINUS_EQ_ID = PLUS_EQ_ID + 1; // -=
    public static final int MUL_EQ_ID = MINUS_EQ_ID + 1;  // *=
    public static final int DIV_EQ_ID = MUL_EQ_ID + 1;    // /=
    public static final int AND_EQ_ID = DIV_EQ_ID + 1;    // &=
    public static final int OR_EQ_ID = AND_EQ_ID + 1;     // |=
    public static final int XOR_EQ_ID = OR_EQ_ID + 1;     // ^=
    public static final int MOD_EQ_ID = XOR_EQ_ID + 1;    // %=
    public static final int NOT_EQ_ID = MOD_EQ_ID + 1;    // !=
    public static final int DOT_ID = NOT_EQ_ID + 1;       // .
    public static final int COMMA_ID = DOT_ID + 1;        // ,
    public static final int COLON_ID = COMMA_ID + 1;      // :
    public static final int SEMICOLON_ID = COLON_ID + 1;  // ;
    public static final int QUESTION_ID = SEMICOLON_ID + 1; // ?
    public static final int LPAREN_ID = QUESTION_ID + 1;  // (
    public static final int RPAREN_ID = LPAREN_ID + 1;    // )
    public static final int LBRACKET_ID = RPAREN_ID + 1;  // [
    public static final int RBRACKET_ID = LBRACKET_ID + 1; // ]
    public static final int LBRACE_ID = RBRACKET_ID + 1;  // {
    public static final int RBRACE_ID = LBRACE_ID + 1;    // }
    public static final int PLUS_PLUS_ID = RBRACE_ID + 1; // ++
    public static final int MINUS_MINUS_ID = PLUS_PLUS_ID + 1; // --
    public static final int AND_AND_ID = MINUS_MINUS_ID + 1; // &&
    public static final int OR_OR_ID = AND_AND_ID + 1;    // ||
    
    // Other keywords numeric-ids
    public static final int BREAK_ID = OR_OR_ID + 1;
    public static final int CASE_ID = BREAK_ID + 1;
    public static final int CATCH_ID = CASE_ID + 1;
    public static final int CONTINUE_ID = CATCH_ID + 1;
    public static final int DEFAULT_ID = CONTINUE_ID + 1;
    public static final int DELETE_ID = DEFAULT_ID + 1;
    public static final int DO_ID = DELETE_ID + 1;
    public static final int ELSE_ID = DO_ID + 1;
    public static final int FALSE_ID = ELSE_ID + 1;
    public static final int FINALLY_ID = FALSE_ID + 1;
    public static final int FOR_ID = FINALLY_ID + 1;
    public static final int FUNCTION_ID = FOR_ID + 1;
    public static final int IF_ID = FUNCTION_ID + 1;
    public static final int IN_ID = IF_ID + 1;
    public static final int INSTANCEOF_ID = IN_ID + 1;
    public static final int NEW_ID = INSTANCEOF_ID + 1;
    public static final int NULL_ID = NEW_ID + 1;
    public static final int RETURN_ID = NULL_ID + 1;
    public static final int SWITCH_ID = RETURN_ID + 1;
    public static final int THIS_ID = SWITCH_ID + 1;
    public static final int THROW_ID = THIS_ID + 1;
    public static final int TRUE_ID = THROW_ID + 1;
    public static final int TRY_ID = TRUE_ID + 1;
    public static final int VAR_ID = TRY_ID + 1;
    public static final int WHILE_ID = VAR_ID + 1;
    
    // Incomplete tokens
    public static final int INCOMPLETE_STRING_LITERAL_ID = WHILE_ID + 1;
    public static final int INCOMPLETE_HEX_LITERAL_ID = INCOMPLETE_STRING_LITERAL_ID + 1;
    public static final int INVALID_OPERATOR_ID = INCOMPLETE_HEX_LITERAL_ID + 1;
    public static final int INVALID_COMMENT_END_ID = INVALID_OPERATOR_ID + 1;
    public static final int INVALID_CHAR_ID = INVALID_COMMENT_END_ID + 1;
    
    // Token-categories
    /** All the keywords belong to this category. */
    public static final BaseTokenCategory KEYWORDS
            = new BaseTokenCategory("keywords", KEYWORDS_ID); // NOI18N
    
    /** All the operators belong to this category. */
    public static final BaseTokenCategory OPERATORS
            = new BaseTokenCategory(operators, OPERATORS_ID); // NOI18N
    
    /** All the numeric literals belong to this category. */
    public static final BaseTokenCategory NUMERIC_LITERALS
            = new BaseTokenCategory("numeric-literals", NUMERIC_LITERALS_ID); // NOI18N
    
    /** All the errorneous constructions and incomplete tokens belong to this category. */
    public static final BaseTokenCategory ERRORS
            = new BaseTokenCategory("errors", ERRORS_ID); // NOI18N
    
    
    // Token-ids
    public static final BaseTokenID WHITESPACE
            = new BaseTokenID("whitespace", WHITESPACE_ID); // NOI18N
    
    public static final BaseTokenID IDENTIFIER
            = new BaseTokenID("identifier", IDENTIFIER_ID); // NOI18N
    
    /** Comment with the '//' prefix */
    public static final BaseTokenID LINE_COMMENT
            = new BaseTokenID("line-comment", LINE_COMMENT_ID); // NOI18N
    
    /** Block comment */
    public static final BaseTokenID BLOCK_COMMENT
            = new BaseTokenID("block-comment", BLOCK_COMMENT_ID); // NOI18N
    
    /** Javascript string literal e.g. "hello" */
    public static final BaseTokenID STRING_LITERAL
            = new BaseTokenID("string-literal", STRING_LITERAL_ID); // NOI18N
    
    /** Javascript char literal e.g. 'hello' */
    public static final BaseTokenID CHAR_LITERAL
            = new BaseTokenID("char-literal", CHAR_LITERAL_ID); // NOI18N
    
    
    /** Javascript hexadecimal literal e.g. 0x5a */
    public static final BaseTokenID HEX_LITERAL
            = new BaseTokenID("hex-literal", HEX_LITERAL_ID, NUMERIC_LITERALS); // NOI18N
    
    
     /** Javascript method e.g. test() */
    public static final BaseTokenID METHOD
            = new BaseTokenID("methods", METHOD_ID); // NOI18N
    
     /** Javascript RegExp literal */
    public static final BaseTokenID REGEXP
            = new BaseTokenID("regexp", REGEXP_ID); // NOI18N
    
    // Operators
    public static final BaseImageTokenID EQ
            = new BaseImageTokenID(operators, EQ_ID, OPERATORS, "="); // NOI18N
    
    public static final BaseImageTokenID LT
            = new BaseImageTokenID(operators, LT_ID, OPERATORS, "<"); // NOI18N
    
    public static final BaseImageTokenID GT
            = new BaseImageTokenID(operators, GT_ID, OPERATORS, ">"); // NOI18N
    
    public static final BaseImageTokenID LSHIFT
            = new BaseImageTokenID(operators, LSHIFT_ID, OPERATORS, "<<"); // NOI18N
    
    public static final BaseImageTokenID RSSHIFT
            = new BaseImageTokenID(operators, RSSHIFT_ID, OPERATORS, ">>"); // NOI18N
    
    public static final BaseImageTokenID RUSHIFT
            = new BaseImageTokenID(operators, RUSHIFT_ID, OPERATORS, ">>>"); // NOI18N
    
    public static final BaseImageTokenID PLUS
            = new BaseImageTokenID(operators, PLUS_ID, OPERATORS, "+"); // NOI18N
    
    public static final BaseImageTokenID MINUS
            = new BaseImageTokenID(operators, MINUS_ID, OPERATORS, "-"); // NOI18N
    
    public static final BaseImageTokenID MUL
            = new BaseImageTokenID(operators, MUL_ID, OPERATORS, "*"); // NOI18N
    
    public static final BaseImageTokenID DIV
            = new BaseImageTokenID(operators, DIV_ID, OPERATORS, "/"); // NOI18N
    
    public static final BaseImageTokenID AND
            = new BaseImageTokenID(operators, AND_ID, OPERATORS, "&"); // NOI18N
    
    public static final BaseImageTokenID OR
            = new BaseImageTokenID(operators, OR_ID, OPERATORS, "|"); // NOI18N
    
    public static final BaseImageTokenID XOR
            = new BaseImageTokenID(operators, XOR_ID, OPERATORS, "^"); // NOI18N
    
    public static final BaseImageTokenID MOD
            = new BaseImageTokenID(operators, MOD_ID, OPERATORS, "%"); // NOI18N
    
    public static final BaseImageTokenID NOT
            = new BaseImageTokenID(operators, NOT_ID, OPERATORS, "!"); // NOI18N
    
    public static final BaseImageTokenID NEG
            = new BaseImageTokenID(operators, NEG_ID, OPERATORS, "~"); // NOI18N
    
    
    public static final BaseImageTokenID EQ_EQ
            = new BaseImageTokenID(operators, EQ_EQ_ID, OPERATORS, "=="); // NOI18N
    
    public static final BaseImageTokenID LT_EQ
            = new BaseImageTokenID(operators, LT_EQ_ID, OPERATORS, "<="); // NOI18N
    
    public static final BaseImageTokenID GT_EQ
            = new BaseImageTokenID(operators, GT_EQ_ID, OPERATORS, ">="); // NOI18N
    
    public static final BaseImageTokenID LSHIFT_EQ
            = new BaseImageTokenID(operators, LSHIFT_EQ_ID, OPERATORS, "<<="); // NOI18N
    
    public static final BaseImageTokenID RSSHIFT_EQ
            = new BaseImageTokenID(operators, RSSHIFT_EQ_ID, OPERATORS, ">>="); // NOI18N
    
    public static final BaseImageTokenID RUSHIFT_EQ
            = new BaseImageTokenID(operators, RUSHIFT_EQ_ID, OPERATORS, ">>>="); // NOI18N
    
    public static final BaseImageTokenID PLUS_EQ
            = new BaseImageTokenID(operators, PLUS_EQ_ID, OPERATORS, "+="); // NOI18N
    
    public static final BaseImageTokenID MINUS_EQ
            = new BaseImageTokenID(operators, MINUS_EQ_ID, OPERATORS, "-="); // NOI18N
    
    public static final BaseImageTokenID MUL_EQ
            = new BaseImageTokenID(operators, MUL_EQ_ID, OPERATORS, "*="); // NOI18N
    
    public static final BaseImageTokenID DIV_EQ
            = new BaseImageTokenID(operators, DIV_EQ_ID, OPERATORS, "/="); // NOI18N
    
    public static final BaseImageTokenID AND_EQ
            = new BaseImageTokenID(operators, AND_EQ_ID, OPERATORS, "&="); // NOI18N
    
    public static final BaseImageTokenID OR_EQ
            = new BaseImageTokenID(operators, OR_EQ_ID, OPERATORS, "|="); // NOI18N
    
    public static final BaseImageTokenID XOR_EQ
            = new BaseImageTokenID(operators, XOR_EQ_ID, OPERATORS, "^="); // NOI18N
    
    public static final BaseImageTokenID MOD_EQ
            = new BaseImageTokenID(operators, MOD_EQ_ID, OPERATORS, "%="); // NOI18N
    
    public static final BaseImageTokenID NOT_EQ
            = new BaseImageTokenID(operators, NOT_EQ_ID, OPERATORS, "!="); // NOI18N
    
    public static final BaseImageTokenID DOT
            = new BaseImageTokenID(operators, DOT_ID, OPERATORS, "."); // NOI18N
    
    public static final BaseImageTokenID COMMA
            = new BaseImageTokenID(operators, COMMA_ID, OPERATORS, ","); // NOI18N
    
    public static final BaseImageTokenID COLON
            = new BaseImageTokenID(operators, COLON_ID, OPERATORS, ":"); // NOI18N
    
    public static final BaseImageTokenID SEMICOLON
            = new BaseImageTokenID(operators, SEMICOLON_ID, OPERATORS, ";"); // NOI18N
    
    public static final BaseImageTokenID QUESTION
            = new BaseImageTokenID(operators, QUESTION_ID, OPERATORS, "?"); // NOI18N
    
    public static final BaseImageTokenID LPAREN
            = new BaseImageTokenID(operators, LPAREN_ID, OPERATORS, "("); // NOI18N
    
    public static final BaseImageTokenID RPAREN
            = new BaseImageTokenID(operators, RPAREN_ID, OPERATORS, ")"); // NOI18N
    
    public static final BaseImageTokenID LBRACKET
            = new BaseImageTokenID(operators, LBRACKET_ID, OPERATORS, "["); // NOI18N
    
    public static final BaseImageTokenID RBRACKET
            = new BaseImageTokenID(operators, RBRACKET_ID, OPERATORS, "]"); // NOI18N
    
    public static final BaseImageTokenID LBRACE
            = new BaseImageTokenID(operators, LBRACE_ID, OPERATORS, "{"); // NOI18N
    
    public static final BaseImageTokenID RBRACE
            = new BaseImageTokenID(operators, RBRACE_ID, OPERATORS, "}"); // NOI18N
    
    public static final BaseImageTokenID PLUS_PLUS
            = new BaseImageTokenID(operators, PLUS_PLUS_ID, OPERATORS, "++"); // NOI18N
    
    public static final BaseImageTokenID MINUS_MINUS
            = new BaseImageTokenID(operators, MINUS_MINUS_ID, OPERATORS, "--"); // NOI18N
    
    public static final BaseImageTokenID AND_AND
            = new BaseImageTokenID(operators, AND_AND_ID, OPERATORS, "&&"); // NOI18N
    
    public static final BaseImageTokenID OR_OR
            = new BaseImageTokenID(operators, OR_OR_ID, OPERATORS, "||"); // NOI18N
    
    
    // Data types
    public static final BaseImageTokenID BREAK
            = new BaseImageTokenID(keywords, BREAK_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID CASE
            = new BaseImageTokenID(keywords, CASE_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID CATCH
            = new BaseImageTokenID(keywords, CATCH_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID CONTINUE
            = new BaseImageTokenID(keywords, CONTINUE_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID DEFAULT
            = new BaseImageTokenID(keywords, DEFAULT_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID DELETE
            = new BaseImageTokenID(keywords, DELETE_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID DO
            = new BaseImageTokenID(keywords, DO_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID ELSE
            = new BaseImageTokenID(keywords, ELSE_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID FALSE
            = new BaseImageTokenID(keywords, FALSE_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID FINALLY
            = new BaseImageTokenID(keywords, FINALLY_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID FOR
            = new BaseImageTokenID(keywords, FOR_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID FUNCTION
            = new BaseImageTokenID(keywords, FUNCTION_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID IF
            = new BaseImageTokenID(keywords, IF_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID IN
            = new BaseImageTokenID(keywords, IN_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID INSTANCEOF
            = new BaseImageTokenID(keywords, INSTANCEOF_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID NEW
            = new BaseImageTokenID(keywords, NEW_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID NULL
            = new BaseImageTokenID(keywords, NULL_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID RETURN
            = new BaseImageTokenID(keywords, RETURN_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID SWITCH
            = new BaseImageTokenID(keywords, SWITCH_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID THIS
            = new BaseImageTokenID(keywords, THIS_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID THROW
            = new BaseImageTokenID(keywords, THROW_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID TRUE
            = new BaseImageTokenID(keywords, TRUE_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID TRY
            = new BaseImageTokenID(keywords, TRY_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID VAR
            = new BaseImageTokenID(keywords, VAR_ID, KEYWORDS); // NOI18N
    
    public static final BaseImageTokenID WHILE
            = new BaseImageTokenID(keywords, WHILE_ID, KEYWORDS); // NOI18N
    
    public static final BaseTokenID NUMBER_LITERAL
            = new BaseTokenID("number-literal", NUMBER_LITERAL_ID, NUMERIC_LITERALS); // NOI18N
    
    // Incomplete and error token-ids
    public static final BaseTokenID INCOMPLETE_STRING_LITERAL
            = new BaseTokenID("incomplete-string-literal", INCOMPLETE_STRING_LITERAL_ID, ERRORS); // NOI18N
    
    public static final BaseTokenID INCOMPLETE_HEX_LITERAL
            = new BaseTokenID("incomplete-hex-literal", INCOMPLETE_HEX_LITERAL_ID, ERRORS); // NOI18N
    
    public static final BaseTokenID INVALID_OPERATOR
            = new BaseTokenID("invalid-operator", INVALID_OPERATOR_ID, ERRORS); // NOI18N
    
    public static final BaseTokenID INVALID_COMMENT_END
            = new BaseTokenID("invalid-comment-end", INVALID_COMMENT_END_ID, ERRORS); // NOI18N
    
    public static final BaseTokenID INVALID_CHAR
            = new BaseTokenID("invalid-char", INVALID_CHAR_ID, ERRORS); // NOI18N
    
    
    public static final JSTokenContext context = new JSTokenContext();
    public static final TokenContextPath contextPath = context.getContextPath();
    
    /**
     *
     */
    public JSTokenContext() {
        super("js-");
        
        try {
            addDeclaredTokenIDs();
        } catch (Exception e) {
            Utilities.annotateLoggable(e);
        }
    }
}
