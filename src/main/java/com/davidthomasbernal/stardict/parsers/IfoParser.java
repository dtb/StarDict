package com.davidthomasbernal.stardict.parsers;

import com.davidthomasbernal.stardict.dictionary.DictionaryInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class IfoParser {
    public static final String BOOK_NAME = "bookname";
    public static final String WORD_COUNT = "wordcount";
    public static final String SYN_WORD_COUNT = "synwordcount";
    public static final String IDX_FILE_SIZE = "idxfilesize";
    public static final String IDX_OFFSET_BITS = "idxoffsetbits";
    public static final String AUTHOR = "author";
    public static final String SAME_TYPE_SEQUENCE = "sametypesequence";
    public static final String VERSION = "version";

    public static final int IDX_OFFSET_BITS_INT = 32;
    public static final int IDX_OFFSET_BITS_LONG = 64;

    public DictionaryInfo parse(Reader srcReader) throws IOException {
        BufferedReader reader = new BufferedReader(srcReader);
        DictionaryInfo result = new DictionaryInfo();

        try {
            String line = reader.readLine();
            while (line != null) {
                processLine(result, line);

                line = reader.readLine();
            }
        } finally {
            reader.close();
        }

        return result;
    }

    private void processLine(DictionaryInfo result, String line) {
        if (line.trim().equals("StarDict's dict ifo file") || line.trim().isEmpty()) {
            return;
        }

        String[] kv = line.trim().split("=");
        if (kv.length != 2) {
            throw new RuntimeException("ifo file is malformed");
        }

        String key = kv[0].trim();
        String value = kv[1].trim();

        setField(result, key, value);
    }

    private void setField(DictionaryInfo result, String key, String value) {
        if (key.equals(BOOK_NAME)) {
            result.setName(value);
        } else if (key.equals(WORD_COUNT)) {
            int wordCount = Integer.parseInt(value);
            result.setWordCount(wordCount);
        } else if (key.equals(SYN_WORD_COUNT)) {
            int synWordCount = Integer.parseInt(value);
            result.setSynWordCount(synWordCount);
        } else if (key.equals(IDX_FILE_SIZE)) {
            int idxFileSize = Integer.parseInt(value);
            result.setIdxFileSize(idxFileSize);
        } else if (key.equals(IDX_OFFSET_BITS)) {
            int idxOffsetBits = Integer.parseInt(value);
            result.setIdxOffsetFormat(parseIdxOffsetBits(idxOffsetBits));
        } else if (key.equals(AUTHOR)) {
            result.setAuthor(value);
        } else if (key.equals(SAME_TYPE_SEQUENCE)) {
            result.setSameTypeSequence(value);
        } else if (key.equals(VERSION)) {
            result.setVersion(value);
        } else {
            result.setProperty(key, value);
        }
    }

    private int parseIdxOffsetBits(int idxOffsetBits) {
        switch (idxOffsetBits) {
            case IDX_OFFSET_BITS_INT:
                return DictionaryInfo.IDX_OFFSET_FORMAT_INT;
            case IDX_OFFSET_BITS_LONG:
                return DictionaryInfo.IDX_OFFSET_FORMAT_LONG;
            default:
                throw new RuntimeException("Bad idxOffsetBets");
        }
    }


}
