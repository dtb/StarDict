package com.davidthomasbernal.stardict;

import java.io.*;

public class DictionaryInfo {

    protected String name;

    protected long wordCount = -1;
    protected long synWordCount = -1;

    protected long idxFileSize = -1;
    protected long idxOffsetBits = -1;

    protected String sameTypeSequence;

    public static DictionaryInfo fromIfo(File ifo) throws IOException {
        IfoParser parser = new IfoParser(ifo);
        return parser.parse();
    }

    public long getIdxOffsetBits() {
        return idxOffsetBits;
    }

    public long getIdxFileSize() {
        return idxFileSize;
    }

    public long getSynWordCount() {
        return synWordCount;
    }

    public long getWordCount() {
        return wordCount;
    }

    public String getName() {
        return name;
    }
}
