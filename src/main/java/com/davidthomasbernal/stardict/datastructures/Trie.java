package com.davidthomasbernal.stardict.datastructures;

import java.util.*;

/**
 * Created by david on 4/5/15.
 */
public class Trie {
    protected static class TreeNode {
        HashMap<Character, TreeNode> nodes = new HashMap<>();
    }
    TreeNode root = new TreeNode();

    public final char EOW = Character.MAX_VALUE;

    protected char[] getWordChars(String word) {
        char[] chars  = Arrays.copyOf(word.toCharArray(), word.length() + 1);
        chars[word.length()] = EOW;
        return chars;
    }

    public void addWord(String word) {
        TreeNode node = root;
        char[] chars = getWordChars(word);
        for (Character charr : chars) {
            if (!node.nodes.containsKey(charr)) {
                node.nodes.put(charr, new TreeNode());
            }
            node = node.nodes.get(charr);
        }
    }

    public boolean containsWord(String word) {
        TreeNode node = root;
        char[] chars = getWordChars(word);
        for (Character charr : chars) {
            if (!node.nodes.containsKey(charr)) {
                return false;
            }
            node = node.nodes.get(charr);
        }
        return true;
    }

    public List<String> prefixSearch(String prefix) {
        // find the prefix node
        // then do DFS?
        char [] chars = prefix.toCharArray();
        TreeNode node = root;

        for (char charr : chars) {
            if (!node.nodes.containsKey(charr)) {
                return Collections.emptyList();
            }
            node = node.nodes.get(charr);
        }

        // "cat…"
        return searchFrom(node, new StringBuilder(prefix));
    }

    protected List<String> searchFrom(TreeNode node, StringBuilder prefix) {
        List<String> words = new ArrayList<>();
        for (Map.Entry<Character, TreeNode> entry : node.nodes.entrySet()) {
            if (entry.getKey() == EOW) {
                words.add(prefix.toString());
                return words;
            } else {
                StringBuilder newPrefix = new StringBuilder(prefix);
                newPrefix.append(entry.getKey());
                words.addAll(searchFrom(entry.getValue(), newPrefix));
            }
        }
        return words;
    }
}
