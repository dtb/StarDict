package com.davidthomasbernal.stardict.parsers;

import com.davidthomasbernal.stardict.dictionary.*;
import com.davidthomasbernal.stardict.util.IndexInputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class SynParser {
    private final DictionaryInfo dictionaryInfo;
    private final DictionaryIndex dictionaryIndex;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public SynParser(DictionaryInfo info, DictionaryIndex index) {
        this.dictionaryInfo = info;
        this.dictionaryIndex = index;
    }

    public void parse(InputStream stream) {
        IndexInputStream indexStream = new IndexInputStream(stream);

        Set<IndexEntry> entries = readEntries(indexStream);
        dictionaryIndex.addToIndex(entries);
    }

    protected Set<IndexEntry> readEntries(IndexInputStream indexStream) {
        Set<IndexEntry> entries = new HashSet<>(dictionaryInfo.getWordCount());

        boolean isPartial = false;
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                isPartial = false;

                String word = indexStream.readWordString();

                isPartial = true;

                int originalWordIndex = indexStream.readInt();

                IndexEntry index = dictionaryIndex.getIndexFileEntry(originalWordIndex);
                index.words.add(word);
                entries.add(index);
//                logger.log(Level.FINE, word + " " + originalWordIndex + " " + index.words.toString());

                if (entries.size() > dictionaryInfo.getSynWordCount()) {
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
        if (entries.size() != dictionaryInfo.getSynWordCount()) {
            throw new IndexFormatException("Info and syn word counts did not match: " + entries.size() + " " + dictionaryInfo.getSynWordCount());
        }

        return entries;
    }
}
