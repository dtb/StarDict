package com.davidthomasbernal.stardict.util.datastructures;

import javax.naming.OperationNotSupportedException;
import java.util.*;

/**
 * Created by david on 4/5/15.
 */
public class TrieMap<T> extends AbstractMap<String, T>{
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

    @Override
    public T get(Object key) {
        return get((String)key);
    }

    public T get(String word) {
        char[] chars = getWordChars(word);
        TreeNode<T> node = findNode(chars);

        if (node == null) {
            return null;
        }

        return node.data;
    }

    @Override
    public boolean containsKey(Object key) {
        return containsKey((String)key);
    }

    public boolean containsKey(String word) {
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

    public List<String> prefixSearch(String prefix) {
        char [] chars = prefix.toCharArray();
        TreeNode<T> node = findNode(chars);
        if (node == null) {
            return Collections.emptyList();
        }

        Set<Entry<String, T>> wordEntries = searchFrom(node, new StringBuilder(prefix));
        List<String> result = new ArrayList<>(wordEntries.size());
        for (Entry<String, T> wordEntry : wordEntries) {
            result.add(wordEntry.getKey());
        }

        return result;
    }

    protected Set<Entry<String, T>> searchFrom(TreeNode<T> node, StringBuilder prefix) {
        Set<Entry<String, T>> words = new HashSet<>();
        for (Map.Entry<Character, TreeNode<T>> entry : node.nodes.entrySet()) {
            if (entry.getKey() == EOW) {
                Entry<String, T> result = new SimpleImmutableEntry<>(prefix.toString(), entry.getValue().data);
                words.add(result);
            } else {
                StringBuilder newPrefix = new StringBuilder(prefix);
                newPrefix.append(entry.getKey());
                words.addAll(searchFrom(entry.getValue(), newPrefix));
            }
        }
        return words;
    }

    @Override
    public Set<Entry<String, T>> entrySet() {
        Set<Entry<String, T>> entries = searchFrom(root, new StringBuilder(""));
        return entries;
    }

    @Override
    public T put(String word, T data) {
        TreeNode<T> node = root;
        char[] chars = getWordChars(word);
        for (Character charr : chars) {
            if (!node.nodes.containsKey(charr)) {
                node.nodes.put(charr, new TreeNode<>());
            }
            node = node.nodes.get(charr);
        }

        T prevData = null;
        if (node.data != null) {
            prevData = node.data;
        }
        node.data = data;

        return prevData;
    }

    @Override
    public T remove(Object key) {
        throw new UnsupportedOperationException();
    }
}
