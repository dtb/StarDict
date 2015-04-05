package com.davidthomasbernal.stardict;

import com.davidthomasbernal.stardict.util.datastructures.Trie;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class TrieTest extends TestCase {

    public void testAddWord() throws Exception {
        Trie trie = new Trie();
        String [] words = new String[] {
                "cat",
                "car",
                "act",
                "a",
                "at",
                "am",
        };

        for(String word: words) {
            trie.addWord(word);
        }
        // I guess we just care that no exception is throw :/
    }

    public void testContainsWord() throws Exception {
        Trie trie = new Trie();
        trie.addWord("cat");

        assertTrue(trie.containsWord("cat"));
        assertFalse(trie.containsWord("car"));
        assertFalse(trie.containsWord("c"));

        trie.addWord("a");
        trie.addWord("am");
        assertTrue(trie.containsWord("a"));
        assertTrue(trie.containsWord("am"));
        assertFalse(trie.containsWord("at"));
    }

    public void testPrefixSearch() throws Exception {
        Trie trie = new Trie();
        trie.addWord("cat");
        trie.addWord("car");
        trie.addWord("can");
        trie.addWord("clear");
        trie.addWord("house");

        List<String> words = trie.prefixSearch("cat");
        assertTrue(words.size() == 1);
        assertTrue(words.contains("cat"));

        List<String> words2 = trie.prefixSearch("ca");
        assertTrue(words2.size() == 3);
        assertTrue(words2.containsAll(Arrays.asList("cat", "car", "can")));

        List<String> words3 = trie.prefixSearch("c");
        assertTrue(words3.size() == 4);
        assertTrue(words3.containsAll(Arrays.asList("clear", "cat", "car", "can")));
    }

    public void testPrefixSearchNoMatches() throws Exception {
        Trie trie = new Trie();
        assertTrue(trie.prefixSearch("ca").size() == 0);

        trie.addWord("house");
        assertTrue(trie.prefixSearch("ca").size() == 0);
    }
}