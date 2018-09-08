package com.davidthomasbernal.stardict;

import com.davidthomasbernal.stardict.dictionary.*;
import com.davidthomasbernal.stardict.parsers.IdxParser;
import com.davidthomasbernal.stardict.parsers.IfoParser;
import com.davidthomasbernal.stardict.parsers.SynParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

public class Dictionary {

    protected final DictionaryDefinitions definitions;
    protected final DictionaryIndex index;
    protected final DictionaryInfo info;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Make a dictionary, using the ifo at path
     *
     * The remaining files (minimally an idx and a dict or dict.dz) should be in the same directory, with the same name,
     * but their respective extensions.
     *
     * @param path
     * @return
     */
    public static Dictionary fromIfo(String path, boolean tolerateInfoMismatch) throws IOException, DataFormatException {
        File ifo = new File(path);
        String abs = ifo.getAbsolutePath();

        if (!ifo.isFile() || !ifo.exists()) {
            throw new IllegalArgumentException("File at path is not a file, or does not exist.");
        }

        String ifoName = ifo.getName();
        int dotIndex = ifoName.lastIndexOf(".");
        if (dotIndex < 0 || !ifoName.substring(dotIndex + 1).equals("ifo")) {
            throw new IllegalArgumentException("File at path must be a .ifo file.");
        }

        String ifoPath = ifo.getParentFile().getAbsolutePath();
        String name = ifoName.substring(0, ifoName.lastIndexOf("."));

        File index = new File(ifoPath, name + ".idx");
        boolean hasIdx = index.exists() && index.isFile();

        if (!hasIdx) {
            throw new IllegalArgumentException("Idx file does not exist");
        }

        File dict = new File(ifoPath, name + ".dict");
        boolean hasDict;
        hasDict = dict.exists() && dict.isFile();

        if (!hasDict) {
            dict = new File(ifoPath, name + ".dict.dz");
            hasDict = dict.exists() && dict.isFile();
        }

        if (!hasDict) {
            throw new IllegalArgumentException("Dict file does not exist");
        }

        File syn = new File(ifoPath, name + ".syn");
        boolean hasSyn = syn.exists() && syn.isFile();

        if (!hasSyn) {
            throw new IllegalArgumentException("Idx file does not exist");
        }

        DictionaryInfo dictionaryInfo;
        try (Reader ifoReader = new InputStreamReader(new FileInputStream(ifo), StandardCharsets.UTF_8)) {

            IfoParser ifoParser = new IfoParser();
            dictionaryInfo = ifoParser.parse(ifoReader);
        }

        BufferedInputStream stream = null;
        DictionaryIndex dictionaryIndex;

        try {
            IdxParser idxParser = new IdxParser(dictionaryInfo, tolerateInfoMismatch);

            stream = new BufferedInputStream(new FileInputStream(index));
            dictionaryIndex = idxParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        if (hasSyn) {
            try {
                SynParser parser = new SynParser(dictionaryInfo, dictionaryIndex, tolerateInfoMismatch);

                stream = new BufferedInputStream(new FileInputStream(syn));
                parser.parse(stream);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }

        return new Dictionary(
                dictionaryInfo,
                dictionaryIndex,
                new DictionaryDefinitions(dict, dictionaryInfo)
        );
    }

    public Dictionary(DictionaryInfo info, DictionaryIndex index, DictionaryDefinitions definitions) {
        this.info = info;
        this.index = index;
        this.definitions = definitions;
    }

    public String getName() {
        return info.getName();
    }

    public long getWordCount() {
        return info.getWordCount();
    }

    public boolean containsWord(String word) {
        return index.containsWord(word);
    }

    public List<String> getDefinitions(String word) throws DataFormatException, IOException {
        Set<IndexEntry> entries = index.getIndexFileEntries(word.toLowerCase());

        if (entries.size() == 0) {
            return Collections.emptyList();
        } else {
            return definitions.getDefinitions(entries);
        }
    }

    public Set<String> getWords() {
        return index.getWords();
    }

    class DictEntryIterator implements Iterator<DictEntry> {
        private Iterator<IndexEntry> entries = index.getIndexFileEntries().iterator();
        @Override
        public DictEntry next() throws NoSuchElementException {
            IndexEntry indexEntry = entries.next();
            try {
                return new DictEntry(indexEntry.words, definitions.getDefinition(indexEntry));
            } catch (DataFormatException e) {
                e.printStackTrace();
                throw new NoSuchElementException();
            } catch (IOException e) {
                e.printStackTrace();
                throw new NoSuchElementException();
            }
        }

        @Override
        public boolean hasNext() {
            return entries.hasNext();
        }
    }

    public Iterator<DictEntry> getIterator() {
        return new DictEntryIterator();
    }

    public Set<String> searchForWord(String search) {
        String searchLower = search.toLowerCase();
        Set<String> words = index.getWords();

        Set<String> results = new LinkedHashSet<>();
        for (String word : words) {
           if (word.toLowerCase().equals(searchLower) || word.toLowerCase().startsWith(searchLower)) {
               results.add(word);
           }
        }

        return results;
    }
}
