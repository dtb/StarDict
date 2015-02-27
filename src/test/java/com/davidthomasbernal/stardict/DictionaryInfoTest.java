package com.davidthomasbernal.stardict;

import com.davidthomasbernal.stardict.DictionaryInfo;
import org.junit.Test;

import static org.junit.Assert.*;

public class DictionaryInfoTest {

    @Test
    public void testAssertValidWithValid() {
        DictionaryInfo info = new DictionaryInfo();
        info.setVersion("2.4.2");
        info.setName("Some dictionary");
        info.setWordCount(12359231);
        info.setIdxFileSize(15291);

        info.assertValid(false);

        assertTrue(true);
    }

    @Test(expected = Exception.class)
    public void testAssertValidWithMissingWordCount() {
        DictionaryInfo info = new DictionaryInfo();
        info.setVersion("2.4.2");
        info.setName("Some dictionary");
        // wordcount
        info.setIdxFileSize(15291);

        info.assertValid(false);

        assertTrue(true);
    }

    @Test(expected = Exception.class)
    public void testAssertValidWithMissingIdxFileSize() {
        DictionaryInfo info = new DictionaryInfo();
        info.setVersion("2.4.2");
        info.setName("Some dictionary");
        info.setWordCount(12359231);
        //idxfilesize

        info.assertValid(false);

        assertTrue(true);
    }

    @Test(expected = Exception.class)
    public void testAssertValidWithMissingName() {
        DictionaryInfo info = new DictionaryInfo();
        info.setVersion("2.4.2");
        // name
        info.setIdxFileSize(15291);
        info.setWordCount(123919);

        info.assertValid(false);

        assertTrue(true);
    }

    @Test(expected = Exception.class)
    public void testAssertValidWithMissingVersion() {
        DictionaryInfo info = new DictionaryInfo();
        // version
        info.setName("Some dictionary");
        info.setWordCount(12359231);
        info.setIdxFileSize(15291);

        info.assertValid(false);

        assertTrue(true);
    }

    @Test
    public void testAssertValidWithValidWithSyn() {
        DictionaryInfo info = new DictionaryInfo();
        info.setVersion("2.4.2");
        info.setName("Some dictionary");
        info.setWordCount(191291);
        info.setIdxFileSize(15291);

        info.setSynWordCount(112381);

        info.assertValid(true);

        assertTrue(true);
    }

    @Test(expected = Exception.class)
    public void testAssertValidWithMissingSynWordCount() {
        DictionaryInfo info = new DictionaryInfo();
        info.setVersion("2.4.2");
        info.setName("Some dictionary");
        info.setWordCount(191291);
        info.setIdxFileSize(15291);

        // synwordcount

        info.assertValid(true);

        assertTrue(true);
    }
}