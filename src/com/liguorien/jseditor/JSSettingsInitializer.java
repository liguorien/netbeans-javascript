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

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import org.netbeans.editor.BaseImageTokenID;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.SettingsNames;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.TokenCategory;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;

/**
 *
 * @author Nicolas Désy
 */
public class JSSettingsInitializer  extends Settings.AbstractInitializer {
    
    public static final String NAME = "javascript-settings-initializer"; // NOI18N
    
    /**
     * Constructor
     */
    public JSSettingsInitializer() {
        super(NAME);
    }
    
    /**
     * Update map filled with the settings.
     * @param kitClass kit class for which the settings are being updated.
     *   It is always non-null value.
     * @param settingsMap map holding [setting-name, setting-value] pairs.
     *   The map can be empty if this is the first initializer
     *   that updates it or if no previous initializers updated it.
     */
    public void updateSettingsMap(Class kitClass, Map settingsMap) {
        if (kitClass == BaseKit.class) {
            new JSTokenColoringInitializer().updateSettingsMap(kitClass, settingsMap);
        }
        
        if (kitClass == JSEditorKit.class) {
            SettingsUtil.updateListSetting(
                    settingsMap,
                    SettingsNames.TOKEN_CONTEXT_LIST,
                    new TokenContext[] { JSTokenContext.context }
            );
        }
    }
    
    /**
     * Class for adding syntax coloring to the editor
     */
    /** Properties token coloring initializer. */
    private static class JSTokenColoringInitializer extends SettingsUtil.TokenColoringInitializer {
        
        /** Bold font. */
        private static final Font boldFont = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
        /** Italic font. */
        private static final Font italicFont = SettingsDefaults.defaultFont.deriveFont(Font.ITALIC);
        
        /** Keyword coloring. */
        private static final Coloring keywordsColoring = new Coloring(null, Color.decode("0x0000BB"), null);
        /** identifier coloring. */
        private static final Coloring identifierColoring = new Coloring(null, Color.black, null);
        /** string coloring. */
        private static final Coloring stringColoring = new Coloring(null, Color.decode("0x99006B"), null);
        /** string coloring. */
        private static final Coloring charColoring = new Coloring(null, Color.decode("0x99006B"), null);
        /** number coloring. */
        private static final Coloring numberColoring = new Coloring(null, Color.decode("0x780000"), null);
        /** Colon coloring. */
        private static final Coloring operatorsColoring = new Coloring(null, Color.red, null);
        
        /** Empty coloring. */
        private static final Coloring emptyColoring = new Coloring(null, null, null);
        /** Method coloring */
        private static final Coloring methodsColoring = new Coloring(null, Color.decode("0x000077"), null);
        
        private static final Coloring commentColoring = new Coloring(null, Color.decode("0x005500"), null);
        
        private static final Coloring regexpColoring = new Coloring(null, Color.decode("0x333333"), null);
                
        private static final Coloring blockCommentColoring = new Coloring(null, Color.decode("0x005500"), null);
        
        /** Constructs PropertiesTokenColoringInitializer. */
        public JSTokenColoringInitializer() {
            super(JSTokenContext.context);
        }
        
        /** Gets token coloring. */
        
        
        public Object getTokenColoring(TokenContextPath tokenContextPath,
                TokenCategory tokenIDOrCategory, boolean printingSet) {
            if(!printingSet) {
                
                if(tokenIDOrCategory instanceof BaseImageTokenID){
                    tokenIDOrCategory = ((BaseImageTokenID) tokenIDOrCategory).getCategory();
                }
                
                switch (tokenIDOrCategory.getNumericID()) {
                    case JSTokenContext.WHITESPACE_ID:
                        return emptyColoring;
                    case JSTokenContext.IDENTIFIER_ID:
                        return identifierColoring;
                    case JSTokenContext.OPERATORS_ID:
                        return operatorsColoring;
                    case JSTokenContext.STRING_LITERAL_ID:
                        return stringColoring;
                    case JSTokenContext.CHAR_LITERAL_ID:
                        return charColoring;
                    case JSTokenContext.KEYWORDS_ID:
                        return keywordsColoring;
                    case JSTokenContext.LINE_COMMENT_ID:
                        return commentColoring;
                    case JSTokenContext.BLOCK_COMMENT_ID:
                        return blockCommentColoring;
                    case JSTokenContext.METHOD_ID:
                        return methodsColoring;
                    case JSTokenContext.REGEXP_ID:
                        return regexpColoring;
                    default:
                        return emptyColoring;
                }                
            } else { // printing set
                return SettingsUtil.defaultPrintColoringEvaluator;
            }
        }
    }
}
