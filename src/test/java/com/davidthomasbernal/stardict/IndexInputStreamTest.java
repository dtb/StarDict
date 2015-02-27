package com.davidthomasbernal.stardict;

import com.davidthomasbernal.stardict.IndexInputStream;
import com.davidthomasbernal.stardict.WordStringFormatException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.*;

public class IndexInputStreamTest {

    @Test
    public void testReadValidStream() throws IOException {
        String testString = "hello world";
        byte[] bytes = Arrays.copyOf(testString.getBytes(StandardCharsets.UTF_8), testString.length() + 1);

        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        IndexInputStream iStream = new IndexInputStream(stream);

        assertEquals(testString, iStream.readWordString());
    }

    @Test(expected = WordStringFormatException.class)
    public void testReadMissingNullTerminator() throws IOException {
        String testString = "hello world";
        byte[] bytes = Arrays.copyOf(testString.getBytes(StandardCharsets.UTF_8), testString.length());

        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        IndexInputStream iStream = new IndexInputStream(stream);

        iStream.readWordString();
    }

    @Test(expected = WordStringFormatException.class)
    public void testReadTooLong() throws IOException {
        String testString = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

        byte[] bytes = Arrays.copyOf(testString.getBytes(StandardCharsets.UTF_8), testString.length());

        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        IndexInputStream iStream = new IndexInputStream(stream);

        iStream.readWordString();
    }

    @Test
    public void testZeroLengthString() throws IOException {
        byte[] bytes = new byte[1];

        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        IndexInputStream iStream = new IndexInputStream(stream);

        assertEquals("", iStream.readWordString());
    }

    @Test(expected = EOFException.class)
    public void testEofAtEndOfStream() throws IOException {
        byte [] bytes = new byte[0];

        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        IndexInputStream iStream = new IndexInputStream(stream);

        iStream.readWordString();
    }

    @Test
    public void testTwoWords() throws IOException {
        String [] words =  new String[] {
            "Hello",
            "Hihi"
        };

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        for (String word : words) {
            byteStream.write(word.getBytes(StandardCharsets.UTF_8));
            byteStream.write(0);
        }

        byte[] bytes = byteStream.toByteArray();

        IndexInputStream stream = new IndexInputStream(new ByteArrayInputStream(bytes));

        assertEquals(words[0], stream.readWordString());
        assertEquals(words[1], stream.readWordString());
    }
}