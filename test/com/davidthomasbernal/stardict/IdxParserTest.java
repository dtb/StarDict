package com.davidthomasbernal.stardict;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class IdxParserTest {

    private DictionaryIndex getDictionaryIndex(List<IndexEntry> entries) throws IOException {
        return getDictionaryIndex(entries, DictionaryInfo.IDX_OFFSET_FORMAT_INT);
    }

    private DictionaryIndex getDictionaryIndex(List<IndexEntry> entries, int idxOffsetFormat) throws IOException {
        DictionaryInfo info = new DictionaryInfo();
        info.setWordCount(entries.size());
        info.setIdxOffsetFormat(idxOffsetFormat);

        return getDictionaryIndex(entries, info);
    }

    private byte[] getDictionaryBytes(List<IndexEntry> entries, DictionaryInfo info) throws IOException {
        ByteArrayOutputStream dictWriter = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(dictWriter);

        for (IndexEntry entry : entries) {
            byte [] wordBytes = Arrays.copyOf(entry.word.getBytes(StandardCharsets.UTF_8), entry.word.length() + 1);

            ds.write(wordBytes, 0, wordBytes.length);
            switch (info.getIdxOffsetFormat()) {
                case DictionaryInfo.IDX_OFFSET_FORMAT_INT:
                    ds.writeInt((int) entry.dataOffset);
                    break;
                case DictionaryInfo.IDX_OFFSET_FORMAT_LONG:
                    ds.writeLong(entry.dataOffset);
                    break;

            }

            ds.writeInt((int) entry.dataSize);
        }

        return dictWriter.toByteArray();
    }

    private DictionaryIndex getDictionaryIndex(List<IndexEntry> entries, DictionaryInfo info) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(getDictionaryBytes(entries, info));

        IdxParser parser = new IdxParser(info);
        return parser.parse(input);
    }

    public void assertSame(List<IndexEntry> expectedEntries, List<IndexEntry> actualEntries) {
        assertEquals(expectedEntries.size(), actualEntries.size());
        for (int i = 0; i < expectedEntries.size(); i++) {
            assertEquals(expectedEntries.get(i).dataOffset, actualEntries.get(i).dataOffset);
            assertEquals(expectedEntries.get(i).dataSize, actualEntries.get(i).dataSize);
        }
    }

    @Test
    public void testTinyDict() throws IOException {
        List<IndexEntry> expectedEntries = new ArrayList<IndexEntry>();
        expectedEntries.add(new IndexEntry("hello", 0, 120));

        DictionaryIndex index = getDictionaryIndex(expectedEntries);

        List<IndexEntry> actualEntries = index.getWordEntries();

        assertSame(expectedEntries, actualEntries);
    }

    @Test
    public void testTwoWordDict() throws IOException {
        List<IndexEntry> expectedEntries = new ArrayList<IndexEntry>();
        expectedEntries.add(new IndexEntry("hello", 0, 120));
        expectedEntries.add(new IndexEntry("hihihihi", 72, 450));

        DictionaryIndex index = getDictionaryIndex(expectedEntries);

        List<IndexEntry> actualEntries = index.getWordEntries();

        assertSame(expectedEntries, actualEntries);
    }

    @Test
    public void testTwoWordDict64BitOffet() throws IOException {
        List<IndexEntry> expectedEntries = new ArrayList<IndexEntry>();
        expectedEntries.add(new IndexEntry("hello", 0, 120));
        expectedEntries.add(new IndexEntry("hihihihi", 72, 450));

        DictionaryIndex index = getDictionaryIndex(expectedEntries, DictionaryInfo.IDX_OFFSET_FORMAT_LONG);

        List<IndexEntry> actualEntries = index.getWordEntries();

        assertSame(expectedEntries, actualEntries);
    }

    @Test(expected = IndexFormatException.class)
    public void testMismatchedWordCount() throws IOException {
        List<IndexEntry> expectedEntries = new ArrayList<IndexEntry>();
        expectedEntries.add(new IndexEntry("hello", 0, 120));
        expectedEntries.add(new IndexEntry("hihihihi", 72, 450));

        DictionaryInfo info = new DictionaryInfo();
        info.setWordCount(50);
        DictionaryIndex index = getDictionaryIndex(expectedEntries, info);

        List<IndexEntry> actualEntries = index.getWordEntries();

        assertSame(expectedEntries, actualEntries);
    }

    @Test(expected = IndexFormatException.class)
    public void testEntryWithMissingInfo() throws IOException {
        List<IndexEntry> expectedEntries = new ArrayList<IndexEntry>();
        expectedEntries.add(new IndexEntry("hello", 0, 120));
        expectedEntries.add(new IndexEntry("hihihihi", 72, 450));

        DictionaryInfo info = new DictionaryInfo();
        info.setWordCount(2);
        // this should stop the byte stream right after the dataOffset of the first word
        byte[] bytes = Arrays.copyOf(getDictionaryBytes(expectedEntries, info), 10);

        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);

        IdxParser parser = new IdxParser(info);

        DictionaryIndex index = parser.parse(byteStream);
    }
}