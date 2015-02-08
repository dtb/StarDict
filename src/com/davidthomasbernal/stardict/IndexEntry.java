package com.davidthomasbernal.stardict;

/**
* Created by david on 2/8/15.
*/
public class IndexEntry {
    public IndexEntry() { }

    public IndexEntry(String word, long dataOffset, long dataSize) {
        this.word = word;
        this.dataOffset = dataOffset;
        this.dataSize = dataSize;
    }

    String word;
    long dataOffset;
    long dataSize;
}