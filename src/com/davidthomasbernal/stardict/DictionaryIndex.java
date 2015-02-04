package com.davidthomasbernal.stardict;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

public class DictionaryIndex {
    protected InputStream stream;
    protected final DictionaryInfo dictionaryInfo;

    private Collection<IndexEntry> entries;

    public DictionaryIndex(File dictFile, DictionaryInfo dictionaryInfo) throws IOException {
        this.stream = new FileInputStream(dictFile);
        this.dictionaryInfo = dictionaryInfo;

        initialize();
    }

    protected void initialize() throws IOException {
        byte[] data = readData();

        entries = new ArrayList<IndexEntry>(dictionaryInfo.getWordCount());

        int offset = 0;
        while (offset < data.length) {
            int wordLength = 0;
            while (offset < data.length && data[offset] != 0) {
                wordLength++;
                offset++;
            }

            String word = new String(data, offset - wordLength, wordLength, StandardCharsets.UTF_8);
            offset++;

            long dataOffset = 0;
            switch (dictionaryInfo.getIdxOffsetBits()) {
                case 32:
                    dataOffset = readInt(data, offset);
                    offset += 4;
                    break;
                case 64:
                    dataOffset = readLong(data, offset);
                    offset += 8;
                    break;
                default:
                    throw new RuntimeException("Bad idxOffsetBits!");
            }

            int dataSize = readInt(data, offset);
            offset += 4;

            IndexEntry entry = new IndexEntry();
            entry.dataOffset = dataOffset;
            entry.dataSize = dataSize;
            entry.word = word;

            entries.add(entry);
        }
    }

    private static long readLong(byte[] data, int offset) {
        return data[offset] << 8 &
               data[offset + 1] << 7 &
               data[offset + 2] << 6 &
               data[offset + 3] << 5 &
               data[offset + 4] << 4 &
               data[offset + 5] << 3 &
               data[offset + 6] << 2 &
               data[offset + 7];
    }

    private static int readInt(byte[] data, int offset) {
        return data[offset] << 4 &
                data[offset + 1] << 3 &
                data[offset + 2] << 2 &
                data[offset + 3];
    }

    protected byte[] readData() throws IOException {
        byte [] data = new byte[dictionaryInfo.getIdxFileSize()];

        try {
            int read = 0;
            int offset = 0;
            do {
                read = stream.read(data, offset, data.length - offset);
                offset += read;
            } while (offset < data.length && read > -1);
        } finally {
            stream.close();
        }

        return data;
    }

    public Collection<String> getWords() {
        return null;
    }

    public boolean containsWord(String word) {
        return false;
    }

    private class IndexEntry {
        String word;
        long dataOffset;
        long dataSize;
    }
}
