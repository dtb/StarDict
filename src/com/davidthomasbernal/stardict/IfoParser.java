package com.davidthomasbernal.stardict;

import java.io.*;

class IfoParser {
    public static final String BOOK_NAME = "bookname";
    public static final String WORD_COUNT = "wordcount";
    public static final String SYN_WORD_COUNT = "synwordcount";
    public static final String IDX_FILE_SIZE = "idxfilesize";
    public static final String IDX_OFFSET_BITS = "idxoffsetbits";
    public static final String AUTHOR = "author";
    public static final String SAME_TYPE_SEQUENCE = "sametypesequence";
    public static final String VERSION = "version";

    protected final BufferedReader reader;

    public IfoParser(File ifo) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(ifo));
    }

    public IfoParser(String ifo) {
        this.reader = new BufferedReader(new StringReader(ifo));
    }

    public DictionaryInfo parse() throws IOException {
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
        if (line.trim().equals("StarDict's dict ifo file")) {
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
            long wordCount = Long.parseLong(value);
            result.setWordCount(wordCount);
        } else if (key.equals(SYN_WORD_COUNT)) {
            long synWordCount = Long.parseLong(value);
            result.setSynWordCount(synWordCount);
        } else if (key.equals(IDX_FILE_SIZE)) {
            long idxFileSize = Long.parseLong(value);
            result.setIdxFileSize(idxFileSize);
        } else if (key.equals(IDX_OFFSET_BITS)) {
            long idxOffsetBits = Long.parseLong(value);
            result.setIdxOffsetBits(idxOffsetBits);
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
}
