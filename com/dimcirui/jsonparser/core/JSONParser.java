package com.dimcirui.jsonparser.core;

import com.dimcirui.jsonparser.parser.Parser;
import com.dimcirui.jsonparser.lexer.CharReader;
import com.dimcirui.jsonparser.lexer.TokenList;
import com.dimcirui.jsonparser.lexer.Lexer;

import java.io.IOException;
import java.io.StringReader;


public class JSONParser {
    private Lexer tokenizer = new Lexer();
    private Parser parser = new Parser();

    public Object fromJSON(String json) throws IOException {
        CharReader charReader = new CharReader(new StringReader(json));
        TokenList tokenList = tokenizer.tokenize(charReader);
        return parser.parse(tokenList);
    }
}