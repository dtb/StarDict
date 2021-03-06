package com.davidthomasbernal.stardict.dictionary;

import com.davidthomasbernal.stardict.dictionary.IndexEntry;

import java.util.*;

public class DictionaryIndex {
    // TODO is this actually needed?
    private final List<IndexEntry> entries;

    private final Map<String, List<IndexEntry>> entryMap;

    public DictionaryIndex(List<IndexEntry> entries) {
        this.entries = new ArrayList<IndexEntry>(entries);
        entryMap = new HashMap<String, List<IndexEntry>>(this.entries.size());

        buildIndex();
    }

    public List<String> getWords() {
        return new IndexWordCollection(entries);
    }

    public List<IndexEntry> getWordEntries() {
        return Collections.unmodifiableList(entries);
    }

    public boolean containsWord(String searchWord) {
        return entryMap.containsKey(searchWord);
    }

    public List<IndexEntry> getWordEntries(String word) {
        if (entryMap.containsKey(word)) {
            return entryMap.get(word);
        } else {
            return Collections.emptyList();
        }
    }

    private void buildIndex() {
        for (IndexEntry entry : entries) {
            String indexWord = entry.word.toLowerCase();
            if (!entryMap.containsKey(indexWord)) {
                entryMap.put(indexWord, new LinkedList<IndexEntry>());
            }

            entryMap.get(indexWord).add(entry);
        }
    }

    private static class IndexWordCollection extends AbstractList<String> {
        List<IndexEntry> indexEntries;

        private IndexWordCollection(List<IndexEntry> indexEntries) {
            this.indexEntries = indexEntries;
        }

        @Override
        public String get(int index) {
            return indexEntries.get(index).word;
        }

        @Override
        public int size() {
            return indexEntries.size();
        }
    }
}
