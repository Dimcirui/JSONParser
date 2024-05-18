
package com.dimcirui.jsonparser.lexer;

import java.io.IOException;
import java.io.Reader;

/**
 * Read characters from a string stream.
 */
public class CharReader {
    private static final int BUFFER_SIZE = 1024;

    // buffer position
    private int pos = 0;
    // buffer ends
    private int size = 0;

    private char[] buffer;
    private Reader reader;

    public CharReader(Reader reader) {
        this.buffer = new char[BUFFER_SIZE];
        this.reader = reader;
    }

    /**
     * check if there're any unread characters.
     * @return
     */
    public boolean hasMore() throws IOException {
        if (pos < size) {
            return true;
        }
        fillBuffer();
        return pos < size;
    }

    /**
     * read cur character without moving.
     * if no such character, return char '-1'.
     * @return
     */
    public char peek() throws IOException{
        if (!hasMore()) {
            return (char) -1;
        }
        return buffer[Math.max(0, pos - 1)];
    }

    /**
     * read the next character and move to it.
     * if no such character, return char '-1'.
     * @return
     */
    public char next() throws IOException {
        if (!hasMore()) {
            return (char) -1;
        }
        return buffer[pos++];
    }

    /**
     * move to prev one without return.
     * if already the first character, do nothing.
     */
    public void back() {
        if (pos > 0) pos--;
    }

    /**
     * read characters from input source and fill buffer
     */
    void fillBuffer() throws IOException {
        int n = reader.read(buffer);
        if (n == -1) return;

        this.pos = 0;
        this.size = n;
    }
}