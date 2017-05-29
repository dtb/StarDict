package com.davidthomasbernal.stardict.searchers;

import com.davidthomasbernal.stardict.Dictionary;
import com.davidthomasbernal.stardict.util.datastructures.TrieMap;

import java.util.List;
import java.util.Set;

/**
 * Created by david on 4/5/15.
 */
public class TriePrefixSearcher {
    protected final Dictionary dictionary;
    private final TrieMap<Object> trie = new TrieMap<>();
    private boolean trieInitialized = false;

    public TriePrefixSearcher(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void ensureTrie() {
        if (!trieInitialized) {
            Set<String> words = this.dictionary.getWords();
            for (String word : words) {
                trie.put(word, null);
            }
            trieInitialized = true;
        }
    }

    public List<String> search(String prefix) {
        ensureTrie();
        return trie.prefixSearch(prefix);
    }
}
