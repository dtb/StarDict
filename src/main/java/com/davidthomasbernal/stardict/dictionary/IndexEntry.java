package com.davidthomasbernal.stardict.dictionary;

import java.util.LinkedHashSet;
import java.util.Set;

/**
* Created by david on 2/8/15.
*/
public class IndexEntry {
    public IndexEntry(String word, long dataOffset, long dataSize) {
        this.words = new LinkedHashSet<>();
        this.words.add(word);
        this.dataOffset = dataOffset;
        this.dataSize = dataSize;
    }

    public final Set<String> words;
    public final long dataOffset;
    public final long dataSize;
}
