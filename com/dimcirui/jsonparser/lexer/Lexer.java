package com.dimcirui.jsonparser.lexer;

import java.io.IOException;
import com.dimcirui.jsonparser.exception.*;

/**
 * Parse characters and generate a TokenList.
 */
public class Lexer {
    private CharReader charReader;
    private TokenList tokenList;

    public TokenList tokenize(CharReader charReader) throws IOException {
        this.charReader = charReader;
        tokenList = new TokenList();
        generateTokens();
        return tokenList;
    }

    private void generateTokens() throws IOException {
        Token token;
        do {
            token = nextToken();
            tokenList.add(token);
        } while (token.getTokenType() != TokenType.END_DOCUMENT);
    }

    /**
     * Read the next token from the input.
     * @return The next token.
     * @throws IOException
     * @throws JsonParseException If an invalid character is encountered.
     */
    private Token nextToken() throws IOException {
        char ch;
        while (true) {
            if (!charReader.hasMore()) {
                return new Token(TokenType.END_DOCUMENT, null);
            }

            ch = charReader.next();
            if (!isWhiteSpace(ch)) break;
        }

        switch (ch) {
            case '{': return new Token(TokenType.BEGIN_OBJECT, String.valueOf(ch));
            case '}': return new Token(TokenType.END_OBJECT, String.valueOf(ch));
            case '[': return new Token(TokenType.BEGIN_ARRAY, String.valueOf(ch));
            case ']': return new Token(TokenType.END_ARRAY, String.valueOf(ch));
            case 'n': return new Token(TokenType.NULL, readNull());
            case '"': return new Token(TokenType.STRING, readString());
            case 't':
            case 'f': return new Token(TokenType.BOOLEAN, readBoolean());
            case ':': return new Token(TokenType.SEP_COLON, String.valueOf(ch));
            case ',': return new Token(TokenType.SEP_COMMA, String.valueOf(ch));
        }

        if (ch == '-' || isDigit(ch)) {
            return new Token(TokenType.NUMBER, readNumber());
        }

        throw new JsonParseException("Illegal character: " + ch);
    }

    // A whitespace can be added between any symbols.
    private boolean isWhiteSpace(char ch) {
        return (ch == ' ' || ch == '\t' ||
                ch == '\n' || ch == '\r');
    }

    private boolean isDigit(char ch) {
        return ('0' <= ch && ch <= '9');
    }

    private boolean isExp(char ch) {
        return ch == 'e' || ch == 'E';
    }

    private boolean isEscape(char ch) {
        return (ch == '"' || ch == '\\' ||
                ch == 'u' || ch == 'b' ||
                ch == 't' || ch == 'n' ||
                ch == 'f' || ch == 'r');
    }

    private boolean isHex(char ch) {
        return ((isDigit(ch)) || ('a' <= ch && ch <= 'f') || ('A' <= ch && ch <= 'F'));
    }

    private boolean isReadEnd() throws IOException{
        return charReader.peek() == (char) -1;
    }

    private String readNull() throws IOException {
        if (!matchNextChars("ull")) {
            throw new JsonParseException("Invalid JSON string for null value");
        }
        return "null";
    }

    private String readNumber() throws IOException {
        char ch = charReader.peek();
        StringBuilder sb = new StringBuilder();

        // Negative number, '-xx'
        if (ch == '-') {
            sb.append(ch);

            ch = charReader.next();
            // Negative fraction, '-0.xx'
            if (ch == '0') {
                sb.append(ch);
                sb.append(readFracAndExp());
            }
            // Negative Integer, '-xx'
            else if (isDigit(ch) && ch != '0') {
                sb.append(readDigits());

                if (!isReadEnd()) {
                    charReader.back();
                    sb.append(readFracAndExp());
                }
            }
            else throw new JsonParseException("Invalid negative number");
        }
        // Fraction
        else if (ch == '0') {
            sb.append(ch);
            sb.append(readFracAndExp());
        }
        // Integer
        else {
            sb.append(readDigits());

            if (!isReadEnd()) {
                charReader.back();
                sb.append(readFracAndExp());
            }
        }

        return sb.toString();
    }

    /**
     * read all 0~9 digits, end at the character after the last digit
     * @return String of digits
     */
    private String readDigits() throws IOException {
        char ch = charReader.peek();
        StringBuilder sb = new StringBuilder();

        do {
            sb.append(ch);
            ch = charReader.next();
        } while (isDigit(ch));

        return sb.toString();
    }

    private String readFracAndExp() throws IOException {
        StringBuilder sb = new StringBuilder();
        char ch = charReader.next();
        // Integer -> Fraction
        if (ch == '.') {
            sb.append(ch);

            ch = charReader.next();
            if (!isDigit(ch)) {
                throw new JsonParseException("Invalid number format: Not a number.");
            }
            sb.append(readDigits());

            ch = charReader.peek();
            // Integer -> Fraction -> Exp
            if (isExp(ch)) {
                sb.append(readExp());
            }
            else if (!isReadEnd()) {
                charReader.back();
            }
        } 
        // Integer -> Exp
        else if (isExp(ch)) {
            sb.append(readExp());
        }
        else charReader.back();

        return sb.toString();
    }

    // Fraction -> Exp
    // Integer -> Exp
    private String readExp() throws IOException {
        StringBuilder sb = new StringBuilder();
        char ch = charReader.peek();
        if (ch == 'e' || ch == 'E') {
            sb.append(ch);

            ch = charReader.next();
            if (ch == '+' || ch == '-') {
                sb.append(ch);
                ch = charReader.next();
            }
    
            if (!isDigit(ch)) {
                throw new JsonParseException("Invalid number format: Missing number after e/E.");
            }
            sb.append(ch);
            
            if (ch == 0 && isDigit(charReader.next())) {
                throw new JsonParseException("Invalid number format: Leading zero after e/E.");
            }
            sb.append(readDigits());
    
            if (!isReadEnd()) {
                charReader.back();
            }
        }

        return sb.toString();
    }

    private String readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char ch = charReader.next();
            if (ch == '"') return sb.toString();
            if (ch == '\r' || ch == '\n') throw new NumberFormatException("Invalid character");

            if (ch == '\\') {
                ch = charReader.next();
                if (!isEscape(ch)) throw new NumberFormatException("Invalid character");
                sb.append('\\');
                sb.append(ch);
                if (ch == 'u') {
                    for (int i = 0; i < 4; i++) {
                        ch = charReader.next();
                        if (!isHex(ch)) throw new NumberFormatException("Invalid character");
                        sb.append(ch);
                    }
                }
            }
            else sb.append(ch);
        }
    }

    private String readBoolean() throws IOException {
        if (charReader.peek() == 't') {
            if (!matchNextChars("rue")) {
                throw new JsonParseException("Invalid JSON string for true value");
            }
            return "true";
        }
        else {
            if (!matchNextChars("alse")) {
                throw new JsonParseException("Invalid JSON string for false value");
            }
            return "false";
        }
    }

    private boolean matchNextChars(String expected) throws IOException {
        for (char exp: expected.toCharArray()) {
            if (charReader.next() != exp) {
                return false;
            }
        }
        return true;
    }
}