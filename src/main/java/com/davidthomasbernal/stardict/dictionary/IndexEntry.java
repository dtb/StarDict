package com.davidthomasbernal.stardict.dictionary;

/**
* Created by david on 2/8/15.
*/
public class IndexEntry {
    public IndexEntry(String word, long dataOffset, long dataSize) {
        this.word = word;
        this.dataOffset = dataOffset;
        this.dataSize = dataSize;
    }

    public final String word;
    public final long dataOffset;
    public final long dataSize;
}
