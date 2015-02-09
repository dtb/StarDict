package com.davidthomasbernal.stardict;

import java.io.*;
import java.util.*;

public class DictionaryIndex {
    protected final DictionaryInfo dictionaryInfo;

    // TODO is this actually needed?
    private final List<IndexEntry> entries;

    private final Map<String, List<IndexEntry>> entryMap;

    public DictionaryIndex(File dictFile, DictionaryInfo dictionaryInfo) throws IOException {
        InputStream stream = new BufferedInputStream(new FileInputStream(dictFile));
        this.dictionaryInfo = dictionaryInfo;
        entries = new ArrayList<IndexEntry>(dictionaryInfo.getWordCount());
        entryMap = new HashMap<String, List<IndexEntry>>(dictionaryInfo.getWordCount());

        try {
            initialize(stream);
        } finally {
            stream.close();
        }
    }

    public DictionaryIndex(InputStream stream, DictionaryInfo info) {
        this.dictionaryInfo = info;
        entries = new ArrayList<IndexEntry>(dictionaryInfo.getWordCount());
        entryMap = new HashMap<String, List<IndexEntry>>(dictionaryInfo.getWordCount());

        initialize(stream);
    }

    protected void initialize(InputStream stream) {
        IndexInputStream indexStream = new IndexInputStream(stream);

        boolean isPartial = false;
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                isPartial = false;

                String word = indexStream.readWordString();

                isPartial = true;

                long dataOffset;
                switch (dictionaryInfo.getIdxOffsetFormat()) {
                    case DictionaryInfo.IDX_OFFSET_FORMAT_LONG:
                        dataOffset = indexStream.readLong();
                        break;
                    case DictionaryInfo.IDX_OFFSET_FORMAT_INT:
                        dataOffset = indexStream.readInt();
                        break;
                    default:
                        throw new IllegalArgumentException("DictionaryInfo contains an unknown offset format");
                }
                long dataSize = indexStream.readInt();

                entries.add(new IndexEntry(word, dataOffset, dataSize));

                if (entries.size() > dictionaryInfo.getWordCount()) {
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

        if (entries.size() != dictionaryInfo.getWordCount()) {
            throw new IndexFormatException("Index and info word counts did not match.");
        }

        buildIndex();
    }

    public Collection<String> getWords() {
        return new IndexWordCollection(entries);
    }

    public List<IndexEntry> getWordEntries() {
        return Collections.unmodifiableList(entries);
    }

    public boolean containsWord(String searchWord) {
        return entryMap.containsKey(searchWord);
    }

    public List<IndexEntry> getWordEntries(String word) {
        return entryMap.get(word);
    }

    private void buildIndex() {
        for (IndexEntry entry : entries) {
            if (!entryMap.containsKey(entry.word)) {
                entryMap.put(entry.word, new LinkedList<IndexEntry>());
            }

            entryMap.get(entry.word).add(entry);
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
