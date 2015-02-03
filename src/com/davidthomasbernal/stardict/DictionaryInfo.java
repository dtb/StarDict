package com.davidthomasbernal.stardict;

import java.io.*;
import java.util.HashMap;

public class DictionaryInfo {

    protected String name;

    protected long wordCount = -1;
    protected long synWordCount = -1;

    protected long idxFileSize = -1;
    protected long idxOffsetBits = -1;

    protected String author;

    protected String version;

    protected String sameTypeSequence;

    protected HashMap<String, String> properties = new HashMap<String, String>();

    public void assertValid(boolean supportSynFile) {

    }

    public long getIdxOffsetBits() {
        return idxOffsetBits;
    }

    public long getIdxFileSize() {
        return idxFileSize;
    }

    public long getSynWordCount() {
        return synWordCount;
    }

    public long getWordCount() {
        return wordCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWordCount(long wordCount) {
        this.wordCount = wordCount;
    }

    public void setSynWordCount(long synWordCount) {
        this.synWordCount = synWordCount;
    }

    public void setIdxFileSize(long idxFileSize) {
        this.idxFileSize = idxFileSize;
    }

    public void setIdxOffsetBits(long idxOffsetBits) {
        this.idxOffsetBits = idxOffsetBits;
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
}
