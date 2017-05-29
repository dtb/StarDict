package com.davidthomasbernal.stardict.dictionary;

import java.util.Set;

public class DictEntry {
    public Set<String> words;
    public String definition;

    public DictEntry(Set<String> words, String definition) {
        this.words = words;
        this.definition = definition;
    }
}
