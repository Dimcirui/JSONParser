
package com.dimcirui.jsonparser.lexer;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of tokens.
 */
public class TokenList {
    private List<Token> tokenList = new ArrayList<>();
    private int pos = 0;

    public void add(Token token) {
        tokenList.add(token);
    }

    public Token peek() {
        if (!hasMore()) return null;
        return tokenList.get(pos);
    }

    public Token peekPrevious() {
        if (pos - 1  < 0) return null;
        return tokenList.get(pos - 2);
    }

    public Token next() {
        return tokenList.get(pos++);
    }

    public boolean hasMore() {
        return pos < tokenList.size();
    }

    @Override
    public String toString() {
        return "TokenList{" +
                "tokenList=" + tokenList +
                '}';
    }
}
