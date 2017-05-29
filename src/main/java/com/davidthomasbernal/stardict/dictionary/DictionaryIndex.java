package com.davidthomasbernal.stardict.dictionary;

import java.util.*;
import java.util.logging.Logger;

public class DictionaryIndex {
    private final List<IndexEntry> entries;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    // Updated also with entries frm the synonyms file, if present.
    private final Map<String, Set<IndexEntry>> entryMap;

    public DictionaryIndex(List<IndexEntry> entries) {
        this.entries = new ArrayList<IndexEntry>(entries);
        entryMap = new HashMap<String, Set<IndexEntry>>(this.entries.size());

        addToIndex(entries);
    }

    public Set<String> getWords() {
        return entryMap.keySet();
    }

    public List<IndexEntry> getWordEntries() {
        return Collections.unmodifiableList(entries);
    }

    public IndexEntry get(int index) {
        return entries.get(index);
    }

    public boolean containsWord(String searchWord) {
        return entryMap.containsKey(searchWord);
    }

    public Set<IndexEntry> getWordEntries(String word) {
        if (entryMap.containsKey(word)) {
            return entryMap.get(word);
        } else {
            return Collections.emptySet();
        }
    }

    public void addToIndex(Collection<IndexEntry> entries) {
        for (IndexEntry entry : entries) {
            for (String word: entry.words) {
                String indexWord = word.toLowerCase();
                entryMap.putIfAbsent(indexWord, new LinkedHashSet<>());
                entryMap.get(indexWord).add(entry);
            }
        }
    }
}
