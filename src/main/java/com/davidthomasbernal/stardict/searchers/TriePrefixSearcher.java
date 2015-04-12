package com.davidthomasbernal.stardict.searchers;

import com.davidthomasbernal.stardict.Dictionary;
import com.davidthomasbernal.stardict.util.datastructures.Trie;

import java.util.List;

/**
 * Created by david on 4/5/15.
 */
public class TriePrefixSearcher {
    protected final Dictionary dictionary;
    private final Trie<Object> trie = new Trie<>();
    private boolean trieInitialized = false;

    public TriePrefixSearcher(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void ensureTrie() {
        if (!trieInitialized) {
            List<String> words = this.dictionary.getWords();
            for (String word : words) {
                trie.addWord(word, null);
            }
            trieInitialized = true;
        }
    }

    public List<String> search(String prefix) {
        ensureTrie();
        return trie.prefixSearch(prefix);
    }
}
