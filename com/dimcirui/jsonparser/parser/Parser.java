package com.dimcirui.jsonparser.parser;

import com.dimcirui.jsonparser.lexer.TokenType;
import com.dimcirui.jsonparser.model.JsonArray;
import com.dimcirui.jsonparser.model.JsonObject;
import com.dimcirui.jsonparser.lexer.Token;
import com.dimcirui.jsonparser.lexer.TokenList;
import com.dimcirui.jsonparser.exception.*;

/**
 * Generate a JsonObject(or JsonArray) from a TokenList.
 */
public class Parser {
    private static final int BEGIN_OBJECT_TOKEN = 1;
    private static final int END_OBJECT_TOKEN = 2;
    private static final int BEGIN_ARRAY_TOKEN = 4;
    private static final int END_ARRAY_TOKEN = 8;
    private static final int NULL_TOKEN = 16;
    private static final int NUMBER_TOKEN = 32;
    private static final int STRING_TOKEN = 64;
    private static final int BOOLEAN_TOKEN = 128;
    private static final int SEP_COLON_TOKEN = 256;
    private static final int SEP_COMMA_TOKEN = 512;

    private TokenList tokenList;
    
    public Object parse(TokenList tokenList) {
        this.tokenList = tokenList;
        return parse();
    }

    private Object parse() {
        Token token = tokenList.next();
        if (token == null) {
            return new JsonObject();
        }
        if (token.getTokenType() == TokenType.BEGIN_OBJECT) {
            return parseJsonObject();
        }
        if (token.getTokenType() == TokenType.BEGIN_ARRAY) {
            return parseJsonArray();
        }
        throw new JsonParseException("Parse error, invalid token");
    }

    private JsonObject parseJsonObject() {
        JsonObject jsonObject = new JsonObject();
        int expectedToken = END_OBJECT_TOKEN | STRING_TOKEN;
        String key = null;
        Object value = null;

        while (tokenList.hasMore()) {
            Token token = tokenList.next();
            TokenType tokenType = token.getTokenType();
            String tokenValue = token.getValue();

            switch (tokenType) {
                case BEGIN_OBJECT:
                    checkExpectToken(tokenType, expectedToken);
                    jsonObject.put(key, parseJsonObject());
                    expectedToken = END_OBJECT_TOKEN | SEP_COMMA_TOKEN;
                    break;
                case END_OBJECT:
                    checkExpectToken(tokenType, expectedToken);
                    return jsonObject;
                case BEGIN_ARRAY:
                    checkExpectToken(tokenType, expectedToken);
                    jsonObject.put(key, parseJsonArray());
                    expectedToken = END_OBJECT_TOKEN | SEP_COMMA_TOKEN;
                    break;
                case NULL:
                    checkExpectToken(tokenType, expectedToken);
                    jsonObject.put(key, null);
                    expectedToken = END_OBJECT_TOKEN | SEP_COMMA_TOKEN;
                    break;
                case NUMBER:
                    checkExpectToken(tokenType, expectedToken);
                    if (tokenValue.contains(".") || tokenValue.contains("e") || tokenValue.contains("E")) {
                        jsonObject.put(key, Double.valueOf(tokenValue));
                    }
                    else {
                        Long num = Long.valueOf(tokenValue);
                        if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
                            jsonObject.put(key, num);
                        }
                        else jsonObject.put(key, num.intValue());
                    }
                    expectedToken = END_OBJECT_TOKEN | SEP_COMMA_TOKEN;
                    break;
                case STRING:
                    checkExpectToken(tokenType, expectedToken);
                    Token prevToken = tokenList.peekPrevious();
                    // as a value
                    if (prevToken.getTokenType() == TokenType.SEP_COLON) {
                        value = tokenValue;
                        jsonObject.put(key, value);
                        expectedToken = END_OBJECT_TOKEN | SEP_COMMA_TOKEN;
                    }
                    // as a key
                    else {
                        key = tokenValue;
                        expectedToken = SEP_COLON_TOKEN;
                    }
                    break;
                case BOOLEAN:
                    checkExpectToken(tokenType, expectedToken);
                    jsonObject.put(key, Boolean.valueOf(tokenValue));
                    expectedToken = END_OBJECT_TOKEN | SEP_COMMA_TOKEN;
                    break;
                case SEP_COLON:
                    checkExpectToken(tokenType, expectedToken);
                    expectedToken = BEGIN_OBJECT_TOKEN | BEGIN_ARRAY_TOKEN |
                                    NULL_TOKEN | NUMBER_TOKEN |
                                    STRING_TOKEN | BOOLEAN_TOKEN;
                    break;
                case SEP_COMMA:
                    checkExpectToken(tokenType, expectedToken);
                    expectedToken = STRING_TOKEN;
                    break;
                case END_DOCUMENT:
                    checkExpectToken(tokenType, expectedToken);
                    return jsonObject;
                default:
                    throw new JsonParseException("Unexpected Token");
                }
            }

        throw new JsonParseException("Parse error, invalid Token.");
    }

    private JsonArray parseJsonArray() {
        JsonArray jsonArray = new JsonArray();
        int expectedToken = BEGIN_ARRAY_TOKEN | END_ARRAY_TOKEN |
                            BEGIN_OBJECT_TOKEN |END_OBJECT_TOKEN |
                            STRING_TOKEN | NULL_TOKEN | NUMBER_TOKEN | BOOLEAN_TOKEN;

        while (tokenList.hasMore()) {
            Token token = tokenList.next();
            TokenType tokenType = token.getTokenType();
            String tokenValue = token.getValue();
    
            switch (tokenType) {
                case BEGIN_OBJECT:
                    checkExpectToken(tokenType, expectedToken);
                    jsonArray.add(parseJsonObject());
                    expectedToken = END_ARRAY_TOKEN | SEP_COMMA_TOKEN;
                    break;
                case BEGIN_ARRAY:
                    checkExpectToken(tokenType, expectedToken);
                    jsonArray.add(parseJsonArray());
                    expectedToken = END_ARRAY_TOKEN | SEP_COMMA_TOKEN;
                    break;
                case END_ARRAY:
                    checkExpectToken(tokenType, expectedToken);
                    return jsonArray;
                case NULL:
                    checkExpectToken(tokenType, expectedToken);
                    jsonArray.add(null);
                    expectedToken = END_ARRAY_TOKEN | SEP_COMMA_TOKEN;
                    break;
                case NUMBER:
                    checkExpectToken(tokenType, expectedToken);
                    if (tokenValue.contains(".") || tokenValue.contains("e") || tokenValue.contains("E")) {
                        jsonArray.add(Double.valueOf(tokenValue));
                    }
                    else {
                        Long num = Long.valueOf(tokenValue);                        
                        if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
                            jsonArray.add(num);
                        }
                        else jsonArray.add(num.intValue());
                    }
                    expectedToken = END_ARRAY_TOKEN | SEP_COMMA_TOKEN;
                    break;
                case STRING:
                    checkExpectToken(tokenType, expectedToken);
                    jsonArray.add(tokenValue);
                    expectedToken = END_ARRAY_TOKEN | SEP_COMMA_TOKEN;
                    break;
                case BOOLEAN:
                    checkExpectToken(tokenType, expectedToken);
                    jsonArray.add(Boolean.valueOf(tokenValue));
                    expectedToken = END_ARRAY_TOKEN | SEP_COMMA_TOKEN;
                    break;
                case SEP_COMMA:
                    checkExpectToken(tokenType, expectedToken);
                    expectedToken = BEGIN_OBJECT_TOKEN | BEGIN_ARRAY_TOKEN |
                                    NULL_TOKEN | NUMBER_TOKEN |
                                    STRING_TOKEN | BOOLEAN_TOKEN;
                    break;
                case END_DOCUMENT:
                    checkExpectToken(tokenType, expectedToken);
                    return jsonArray;
                default:
                    throw new JsonParseException("Unexpected Token");
            }
        }

        throw new JsonParseException("Parse error, invalid Token");
    }

    private void checkExpectToken(TokenType tokenType, int expectedToken) {
        if ((tokenType.getTokenCode() & expectedToken) == 0) {
            throw new JsonParseException("Parse error, invalid Token: " + tokenType);
        }
    }
}