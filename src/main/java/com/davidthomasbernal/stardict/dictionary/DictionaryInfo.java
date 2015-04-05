package com.davidthomasbernal.stardict.dictionary;

import java.util.HashMap;

public class DictionaryInfo {

    protected String name;

    protected int wordCount = -1;
    protected int synWordCount = -1;

    protected int idxFileSize = -1;

    public static final int IDX_OFFSET_FORMAT_INT = 0;
    public static final int IDX_OFFSET_FORMAT_LONG = 1;

    protected int idxOffsetFormat = IDX_OFFSET_FORMAT_INT;

    protected String author;

    protected String version;

    protected String sameTypeSequence;

    protected HashMap<String, String> properties = new HashMap<String, String>();

    public class InvalidInfoException extends RuntimeException {
        public InvalidInfoException(String message) { super(message); }
    }

    public void assertValid(boolean supportSynFile) {
        if (wordCount < 0) {
            throw new InvalidInfoException("Wordcount is missing or negative.");
        }

        if (idxFileSize < 0) {
            throw new InvalidInfoException("idxFileSize is missing or negative.");
        }

        if (name == null) {
            throw new InvalidInfoException("Name is missing");
        }

        if (version == null) {
            throw new InvalidInfoException("Version is missing.");
        }

        if (supportSynFile) {
            if (synWordCount < 0) {
                throw new InvalidInfoException("synWordCount is missing or negative");
            }
        }
    }

    public int getIdxFileSize() {
        return idxFileSize;
    }

    public int getSynWordCount() {
        return synWordCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public void setSynWordCount(int synWordCount) {
        this.synWordCount = synWordCount;
    }

    public void setIdxFileSize(int idxFileSize) {
        this.idxFileSize = idxFileSize;
    }

    public void setSameTypeSequence(String sameTypeSequence) {
        this.sameTypeSequence = sameTypeSequence;
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSameTypeSequence() {
        return sameTypeSequence;
    }

    public int getIdxOffsetFormat() {
        return idxOffsetFormat;
    }

    public void setIdxOffsetFormat(int idxOffsetFormat) {
        this.idxOffsetFormat = idxOffsetFormat;
    }

}
