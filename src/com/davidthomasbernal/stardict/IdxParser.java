package com.davidthomasbernal.stardict;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 2/16/15.
 */
class IdxParser {
    private final DictionaryInfo dictionaryInfo;

    public IdxParser(DictionaryInfo info) {
        this.dictionaryInfo = info;
    }

    public DictionaryIndex parse(InputStream stream) {
        IndexInputStream indexStream = new IndexInputStream(stream);

        List<IndexEntry> entries = readEntries(indexStream);
        validateEntries(entries);

        return new DictionaryIndex(entries);
    }

    protected List<IndexEntry> readEntries(IndexInputStream indexStream) {
        List<IndexEntry> entries = new ArrayList<IndexEntry>(dictionaryInfo.getWordCount());

        boolean isPartial = false;
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                isPartial = false;

                String word = indexStream.readWordString();

                isPartial = true;

                long dataOffset = getDataOffset(indexStream);
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

        return entries;
    }

    private long getDataOffset(IndexInputStream indexStream) throws IOException {
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

        return dataOffset;
    }

    protected void validateEntries(List<IndexEntry> entries) {
        if (entries.size() != dictionaryInfo.getWordCount()) {
            throw new IndexFormatException("Index and info word counts did not match.");
        }
    }
}
