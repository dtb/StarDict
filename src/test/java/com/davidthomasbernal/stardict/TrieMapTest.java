package com.davidthomasbernal.stardict;

import com.davidthomasbernal.stardict.util.datastructures.TrieMap;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TrieMapTest {
    @Test
    public void testAddWord() throws Exception {
        TrieMap<Object> trie = new TrieMap<>();
        String [] words = new String[] {
                "cat",
                "car",
                "act",
                "a",
                "at",
                "am",
        };

        for(String word: words) {
            trie.put(word, null);
        }
        // I guess we just care that no exception is thrown :/
    }

    @Test
    public void testContainsWord() throws Exception {
        TrieMap<Object> trie = new TrieMap<>();
        trie.put("cat", null);

        assertTrue(trie.containsKey("cat"));
        assertFalse(trie.containsKey("car"));
        assertFalse(trie.containsKey("c"));
        assertFalse(trie.containsKey("dog"));

        trie.put("a", null);
        trie.put("am", null);
        assertTrue(trie.containsKey("a"));
        assertTrue(trie.containsKey("am"));
        assertFalse(trie.containsKey("at"));
    }

    @Test
    public void testPrefixSearch() throws Exception {
        TrieMap<Object> trie = new TrieMap<>();
        trie.put("cat", null);
        trie.put("car", null);
        trie.put("can", null);
        trie.put("clear", null);
        trie.put("house", null);

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

    @Test
    public void testPrefixSearchNoMatches() throws Exception {
        TrieMap<Object> trie = new TrieMap<>();
        assertTrue(trie.prefixSearch("ca").size() == 0);

        trie.put("house", null);
        assertTrue(trie.prefixSearch("ca").size() == 0);
    }

    @Test
    public void testGet() {
        TrieMap<String> trie = new TrieMap<>();

        assertNull(trie.get("Cow"));

        trie.put("Cow", "cow data");
        trie.put("Moose", "moose data");
        assertEquals(trie.get("Cow"), "cow data");
        assertEquals(trie.get("Moose"), "moose data");
        assertEquals(trie.get("Bird"), null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        TrieMap<String> trie = new TrieMap<>();
        trie.remove("Cow");
    }

    @Test
    public void testMapMethods() {
        TrieMap<String> trie = new TrieMap<>();

        assertEquals(0, trie.size());

        trie.put("cow", "cow data");
        assertEquals(1, trie.size());

        trie.put("cowl", "cow data");
        trie.put("cower", "cow data");
        trie.put("crow", "cow data");

        assertEquals(4, trie.size());

        trie.put("horse", "horse data");

        assertEquals(5, trie.size());

        String[] expected = new String[] { "cow", "cowl", "cower", "crow", "horse" };
        String[] actual = trie.keySet().toArray(new String[trie.keySet().size()]);
        Arrays.sort(actual);
        Arrays.sort(expected);
        assertArrayEquals(expected, actual);
    }
}