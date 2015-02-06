package com.davidthomasbernal.stardict;

import java.io.*;
import java.util.*;

public class DictionaryIndex {
    protected InputStream stream;
    protected final DictionaryInfo dictionaryInfo;

    private List<IndexEntry> entries;

    public DictionaryIndex(File dictFile, DictionaryInfo dictionaryInfo) throws IOException {
        this.stream = new BufferedInputStream(new FileInputStream(dictFile));
        this.dictionaryInfo = dictionaryInfo;

        initialize();
    }

    protected void initialize() {
        ArrayList<IndexEntry> tempEntries = new ArrayList<IndexEntry>(dictionaryInfo.getWordCount());

        IndexInputStream indexStream = new IndexInputStream(stream);

        boolean isPartial = false;
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                isPartial = false;

                IndexEntry entry = new IndexEntry();
                entry.word = indexStream.readWordString();

                isPartial = true;

                switch (dictionaryInfo.getIdxOffsetBits()) {
                    case 64:
                        entry.dataOffset = indexStream.readLong();
                        break;
                    case 32:
                        entry.dataOffset = indexStream.readInt();
                        break;
                }
                entry.dataSize = indexStream.readInt();

                tempEntries.add(entry);

                if (tempEntries.size() > dictionaryInfo.getWordCount()) {
                    throw new IndexFormatException("Found more words than specified in info.");
                }
            }
        } catch (EOFException exception) {
            // this is thrown when we reach the end of the file. If we're partway through initializing the entry,
            // that's bad otherwise it's fine
            if (isPartial) {
                throw new IndexFormatException("Reached the end of the index before we finished parsing a word entry. The index is probably invalid.");
            }
        } catch (IOException exception) {
            throw new IndexFormatException("IOException reading index", exception);
        }

        if (tempEntries.size() != dictionaryInfo.getWordCount()) {
            throw new IndexFormatException("Index and info word counts did not match.");
        }

        entries = tempEntries;
    }

    public Collection<String> getWords() {
        return new IndexWordCollection(entries);
    }

    public boolean containsWord(String searchWord) {
        // TODO LOL
        return getWords().contains(searchWord);
    }

    private class IndexEntry {
        String word;
        long dataOffset;
        long dataSize;
    }

    public static class IndexWordCollection extends AbstractList<String> {
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
