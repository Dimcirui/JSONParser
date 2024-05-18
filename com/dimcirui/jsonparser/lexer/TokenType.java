package com.dimcirui.jsonparser.lexer;

/**
 * Use eleven binary numbers (code) to represent 11 different TokenTypes.
 * The purpose is to use bit operations to simplify the logic of the parser.
 */
public enum TokenType {
    BEGIN_OBJECT(1),    // '{'
    END_OBJECT(2),      // '}'
    BEGIN_ARRAY(4),     // '['
    END_ARRAY(8),       // ']'
    NULL(16),           // 'null', starting with 'n'
    NUMBER(32),         // '-1.4e+10', Long or Double, starting with '0'~'9' or '-'
    STRING(64),         // '"c"', char also counts, starting with '"'
    BOOLEAN(128),       // 'true' or 'false', starting with 't' or 'f'
    SEP_COLON(256),     // ':'
    SEP_COMMA(512),     // ','
    END_DOCUMENT(1024); // end of a document

    private int code;
    
    TokenType(int code) {
        this.code = code;
    }

    public int getTokenCode() {
        return code;
    }
}

