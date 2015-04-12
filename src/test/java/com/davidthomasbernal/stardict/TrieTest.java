package com.davidthomasbernal.stardict;

import com.davidthomasbernal.stardict.util.datastructures.Trie;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class TrieTest extends TestCase {

    public void testAddWord() throws Exception {
        Trie<Object> trie = new Trie<>();
        String [] words = new String[] {
                "cat",
                "car",
                "act",
                "a",
                "at",
                "am",
        };

        for(String word: words) {
            trie.addWord(word, null);
        }
        // I guess we just care that no exception is throw :/
    }

    public void testContainsWord() throws Exception {
        Trie<Object> trie = new Trie<>();
        trie.addWord("cat", null);

        assertTrue(trie.containsWord("cat"));
        assertFalse(trie.containsWord("car"));
        assertFalse(trie.containsWord("c"));

        trie.addWord("a", null);
        trie.addWord("am", null);
        assertTrue(trie.containsWord("a"));
        assertTrue(trie.containsWord("am"));
        assertFalse(trie.containsWord("at"));
    }

    public void testPrefixSearch() throws Exception {
        Trie<Object> trie = new Trie<>();
        trie.addWord("cat", null);
        trie.addWord("car", null);
        trie.addWord("can", null);
        trie.addWord("clear", null);
        trie.addWord("house", null);

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
        Trie<Object> trie = new Trie<>();
        assertTrue(trie.prefixSearch("ca").size() == 0);

        trie.addWord("house", null);
        assertTrue(trie.prefixSearch("ca").size() == 0);
    }


    public void testGet() {
        Trie<String> trie = new Trie<>();

        assertNull(trie.get("Cow"));

        trie.addWord("Cow", "cow data");
        trie.addWord("Moose", "moose data");
        assertEquals(trie.get("Cow"), "cow data");
        assertEquals(trie.get("Moose"), "moose data");
        assertEquals(trie.get("Bird"), null);
    }
}