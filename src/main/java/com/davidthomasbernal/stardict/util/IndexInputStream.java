package com.davidthomasbernal.stardict.util;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by david on 2/5/15.
 */
public class IndexInputStream {
    private final int STARDICT_MAX_WORD_LENGTH = 256;

    private final InputStream in;
    private final DataInputStream dataIn;

    /**
     * Creates a IndexDataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    public IndexInputStream(InputStream in) {
        this.in = in;
        this.dataIn = new DataInputStream(in);
    }

    public int readInt() throws IOException {
        return dataIn.readInt();
    }

    public long readLong() throws IOException {
        return dataIn.readLong();
    }

    private byte[] wordBuffer = new byte[STARDICT_MAX_WORD_LENGTH];

    public String readWordString() throws IOException {
        int wordByte;
        int wordLength = 0;
        while ((wordByte = in.read()) != 0) {
            if (wordByte < 0) {
                // if we're partway throw a word, then uh-oh!
                if (wordLength > 0) {
                    throw new WordStringFormatException("Encountered EOF while trying to read a word!");
                } else {
                    throw new EOFException();
                }
            }

            wordBuffer[wordLength++] = (byte) wordByte;

            if (wordLength >= wordBuffer.length) {
                throw new WordStringFormatException("Word is longer than " + STARDICT_MAX_WORD_LENGTH + " bytes, which is no good");
            }
        }

        return new String(wordBuffer, 0, wordLength, StandardCharsets.UTF_8);
    }
}
