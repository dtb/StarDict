package com.davidthomasbernal.stardict;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by david on 2/5/15.
 */
public class IndexInputStream extends DataInputStream {
    private final int STARDICT_MAX_WORD_LENGTH = 256;

    /**
     * Creates a IndexDataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    public IndexInputStream(InputStream in) {
        super(in);
    }

    private byte[] wordBuffer = new byte[STARDICT_MAX_WORD_LENGTH];

    public String readWordString() throws IOException {
        int wordByte;
        int wordLength = 0;
        while ((wordByte = in.read()) != 0) {
            if (wordByte < 0) {
                throw new EOFException();
            }

            wordBuffer[wordLength++] = (byte) wordByte;

            if (wordLength >= wordBuffer.length) {
                throw new IndexOutOfBoundsException("Word is longer than " + STARDICT_MAX_WORD_LENGTH + "bytes, which is no good");
            }
        }

        return new String(wordBuffer, 0, wordLength, StandardCharsets.UTF_8);
    }
}
