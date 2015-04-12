package com.davidthomasbernal.stardict.util.datastructures;

import java.util.*;

/**
 * Created by david on 4/5/15.
 */
public class Trie<T> {
    protected static class TreeNode<T> {
        HashMap<Character, TreeNode<T>> nodes = new HashMap<>();
        T data;
    }
    TreeNode<T> root = new TreeNode<>();

    public final char EOW = Character.MAX_VALUE;

    protected char[] getWordChars(String word) {
        char[] chars  = Arrays.copyOf(word.toCharArray(), word.length() + 1);
        chars[word.length()] = EOW;
        return chars;
    }

    public void addWord(String word, T data) {
        TreeNode<T> node = root;
        char[] chars = getWordChars(word);
        for (Character charr : chars) {
            if (!node.nodes.containsKey(charr)) {
                node.nodes.put(charr, new TreeNode<T>());
            }
            node = node.nodes.get(charr);
        }
        node.data = data;
    }

    public boolean containsWord(String word) {
        TreeNode<T> node = root;
        char[] chars = getWordChars(word);
        for (Character charr : chars) {
            if (!node.nodes.containsKey(charr)) {
                return false;
            }
            node = node.nodes.get(charr);
        }
        return true;
    }

    protected TreeNode<T> findNode(char [] chars) {
        TreeNode<T> node = root;

        for (char charr : chars) {
            if (!node.nodes.containsKey(charr)) {
                return null;
            }
            node = node.nodes.get(charr);
        }

        return node;
    }

    public T get(String word) {
        char[] chars = getWordChars(word);
        TreeNode<T> node = findNode(chars);

        if (node == null) {
            return null;
        }

        return node.data;
    }

    public List<String> prefixSearch(String prefix) {
        char [] chars = prefix.toCharArray();
        TreeNode<T> node = findNode(chars);
        if (node == null) {
            return Collections.emptyList();
        }

        return searchFrom(node, new StringBuilder(prefix));
    }

    protected List<String> searchFrom(TreeNode<T> node, StringBuilder prefix) {
        List<String> words = new ArrayList<>();
        for (Map.Entry<Character, TreeNode<T>> entry : node.nodes.entrySet()) {
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
