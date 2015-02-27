package com.davidthomasbernal.stardict;

import com.davidthomasbernal.stardict.DictionaryInfo;
import com.davidthomasbernal.stardict.IfoParser;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.StringReader;

import static org.junit.Assert.*;

public class IfoParserTest {

    @Test
    public void testParseNormal() throws Exception {
        String ifo =
                "StarDict's dict ifo file\n" +
                "version=2.4.2\n" +
                "wordcount=15291\n" +
                "idxfilesize=2301921\n" +
                "bookname=Some dictionary\n" +
                "description=This is a test\n" +
                "idxoffsetbits=32\n" +
                "date=2015-02-02\n" +
                "sametypesequence=m\n";

        StringReader reader = new StringReader(ifo);

        IfoParser parser = new IfoParser();
        DictionaryInfo info = parser.parse(reader);

        assertEquals("2.4.2", info.getVersion());
        assertEquals(15291, info.getWordCount());
        assertEquals(2301921, info.getIdxFileSize());
        assertEquals("Some dictionary", info.getName());
        assertEquals(DictionaryInfo.IDX_OFFSET_FORMAT_INT, info.getIdxOffsetFormat());
        assertEquals("m", info.getSameTypeSequence());
        assertEquals("2015-02-02", info.getProperty("date"));
        assertEquals("This is a test", info.getProperty("description"));
    }
    @Test
    public void testParseSpacey() throws Exception {
        String ifo =
                "StarDict's dict ifo file \n" +
                "version = 2.4.2\n" +
                "wordcount=15291 \n" +
                "idxfilesize= 2301921\n" +
                "idxoffsetbits=64\n" +
                "bookname= Some dictionary\n" +
                "description =This is a test\n" +
                " date=2015-02-02\n" +
                " sametypesequence =m\n";

        StringReader reader = new StringReader(ifo);
        IfoParser parser = new IfoParser();
        DictionaryInfo info = parser.parse(reader);

        assertEquals("2.4.2", info.getVersion());
        assertEquals(15291, info.getWordCount());
        assertEquals(2301921, info.getIdxFileSize());
        assertEquals(DictionaryInfo.IDX_OFFSET_FORMAT_LONG, info.getIdxOffsetFormat());
        assertEquals("Some dictionary", info.getName());
        assertEquals("m", info.getSameTypeSequence());
        assertEquals("2015-02-02", info.getProperty("date"));
        assertEquals("This is a test", info.getProperty("description"));
    }

    @Test(expected=NumberFormatException.class)
    public void testBadFileSize() throws Exception {
        String ifo =
                "idxfilesize=not a number\n";

        StringReader reader = new StringReader(ifo);
        IfoParser parser = new IfoParser();
        DictionaryInfo info = parser.parse(reader);
    }

    @Test(expected=NumberFormatException.class)
    public void testBadWordCount() throws Exception {
        String ifo =
                "wordcount=not a number\n";

        StringReader reader = new StringReader(ifo);
        IfoParser parser = new IfoParser();
        DictionaryInfo info = parser.parse(reader);
    }

    @Test(expected=RuntimeException.class)
    public void testBadlyFormattedIfo() throws Exception {
        String ifo =
                "wordcount: 7212\n";

        StringReader reader = new StringReader(ifo);
        IfoParser parser = new IfoParser();
        DictionaryInfo info = parser.parse(reader);
    }

    @Test(expected=RuntimeException.class)
    public void testBadIdxOffsetBits() throws Exception {
        String ifo =
                "StarDict's dict ifo file\n" +
                "version=2.4.2\n" +
                "wordcount=15291\n" +
                "idxfilesize=2301921\n" +
                "idxoffsetbits=72\n" +
                "bookname=Some dictionary\n" +
                "description=This is a test\n" +
                "date=2015-02-02\n" +
                "sametypesequence=m\n";

        StringReader reader = new StringReader(ifo);
        IfoParser parser = new IfoParser();
        DictionaryInfo info = parser.parse(reader);
    }
}