package com.davidthomasbernal.stardict;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

import static org.junit.Assert.*;

public class DictionaryDefinitionsTest {

    @Test
    public void testInit() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1F, // gzip id
                0x08, // compression method 8 = DEFLATE
                12, // extra + fname
                0x00, 0x00, 0x00, 0x00, // mtime
                0, // XFL
                0, // os
                0x00, 0x0C, // bytes of extra data
                0x41, 0x52, // subfield ID for dz data
                0x00, 0x08, // size of subfield data
                0x00, 0x01, // dz version
                0x00, 0x0F, // chunk length
                0x00, 0x01, // chunk count
                0x00, 0x0F, // chunk size
                0x36, 0x00, // fname
        };

        DictionaryInfo info = new DictionaryInfo();

        ByteBuffer buffer = ByteBuffer.wrap(data);
        DictionaryDefinitions defs = new DictionaryDefinitions(buffer, info);

        int[] chunks = defs.getChunks();

        assertEquals(1, chunks.length);
        assertEquals(0x0F, chunks[0]);
        assertEquals("6", defs.getFilename());
    }

    @Test(expected = RuntimeException.class)
    public void testInitNotGzip() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1E, // not a gzip id
                0x08, // compression method 8 = DEFLATE
                12, // extra + fname
                0x00, 0x00, 0x00, 0x00, // mtime
                0, // XFL
                0, // os
                0x00, 0x0C, // bytes of extra data
                0x41, 0x52, // subfield ID for dz data
                0x00, 0x08, // size of subfield data
                0x00, 0x01, // dz version
                0x00, 0x0F, // chunk length
                0x00, 0x01, // chunk count
                0x00, 0x0F, // chunk size
                0x36, 0x00, // fname
        };

        DictionaryInfo info = new DictionaryInfo();

        ByteBuffer buffer = ByteBuffer.wrap(data);
        DictionaryDefinitions defs = new DictionaryDefinitions(buffer, info);
    }

    @Test(expected = RuntimeException.class)
    public void testInitNoDzField() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1F, // not a gzip id
                0x08, // compression method 8 = DEFLATE
                12, // extra + fname
                0x00, 0x00, 0x00, 0x00, // mtime
                0, // XFL
                0, // os
                0x00, 0x0C, // bytes of extra data
                0x41, 0x51, // subfield ID for dz data
                0x00, 0x08, // size of subfield data
                0x00, 0x01, // dz version
                0x00, 0x0F, // chunk length
                0x00, 0x01, // chunk count
                0x00, 0x0F, // chunk size
                0x36, 0x00, // fname
        };

        DictionaryInfo info = new DictionaryInfo();

        ByteBuffer buffer = ByteBuffer.wrap(data);
        DictionaryDefinitions defs = new DictionaryDefinitions(buffer, info);
    }

    @Test(expected = RuntimeException.class)
    public void testInitNoExtraData() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1F, // not a gzip id
                0x08, // compression method 8 = DEFLATE
                0x08, // missing extra!, has fname
                0x00, 0x00, 0x00, 0x00, // mtime
                0, // XFL
                0, // os
                0x00, 0x0C, // bytes of extra data
                0x41, 0x51, // subfield ID for dz data
                0x00, 0x08, // size of subfield data
                0x00, 0x01, // dz version
                0x00, 0x0F, // chunk length
                0x00, 0x01, // chunk count
                0x00, 0x0F, // chunk size
                0x36, 0x00, // fname
        };

        DictionaryInfo info = new DictionaryInfo();

        ByteBuffer buffer = ByteBuffer.wrap(data);
        DictionaryDefinitions defs = new DictionaryDefinitions(buffer, info);
    }

    @Test(expected = RuntimeException.class)
    public void testInitWrongDzVersion() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1F, // not a gzip id
                0x08, // compression method 8 = DEFLATE
                0x08, // missing extra!, has fname
                0x00, 0x00, 0x00, 0x00, // mtime
                0, // XFL
                0, // os
                0x00, 0x0C, // bytes of extra data
                0x41, 0x51, // subfield ID for dz data
                0x00, 0x08, // size of subfield data
                0x00, 0x02, // dz version
                0x00, 0x0F, // chunk length
                0x00, 0x01, // chunk count
                0x00, 0x0F, // chunk size
                0x36, 0x00, // fname
        };

        DictionaryInfo info = new DictionaryInfo();

        ByteBuffer buffer = ByteBuffer.wrap(data);
        DictionaryDefinitions defs = new DictionaryDefinitions(buffer, info);
    }

    @Test
    public void testInitWithExtraBeforeDz() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1F, // gzip id
                0x08, // compression method 8 = DEFLATE
                12, // extra + fname
                0x00, 0x00, 0x00, 0x00, // mtime
                0, // XFL
                0, // os
                0x00, 0x0C, // bytes of extra data
                0x41, (byte) 0xAA, // some other subfield
                0x00, 0x01, //   with one byte
                0x00,       //   other subfield data
                0x41, 0x52, // subfield ID for dz data
                0x00, 0x08, // size of subfield data
                0x00, 0x01, // dz version
                0x00, 0x0F, // chunk length
                0x00, 0x01, // chunk count
                0x00, 0x0F, // chunk size
                0x36, 0x00, // fname
        };

        DictionaryInfo info = new DictionaryInfo();

        ByteBuffer buffer = ByteBuffer.wrap(data);
        DictionaryDefinitions defs = new DictionaryDefinitions(buffer, info);

        int[] chunks = defs.getChunks();

        assertEquals(1, chunks.length);
        assertEquals(0x0F, chunks[0]);
        assertEquals("6", defs.getFilename());
    }

    @Test
    public void testInitWithExtraAfterDz() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1F, // gzip id
                0x08, // compression method 8 = DEFLATE
                12, // extra + fname
                0x00, 0x00, 0x00, 0x00, // mtime
                0, // XFL
                0, // os
                0x00, 0x11, // bytes of extra data
                0x41, 0x52, // subfield ID for dz data
                0x00, 0x08, // size of subfield data
                0x00, 0x01, // dz version
                0x00, 0x0F, // chunk length
                0x00, 0x01, // chunk count
                0x00, 0x0F, // chunk size
                0x41, (byte) 0xAA, // some other subfield
                0x00, 0x01, //   with one byte
                0x00,       //   other subfield data
                0x36, 0x00, // fname
        };

        DictionaryInfo info = new DictionaryInfo();

        ByteBuffer buffer = ByteBuffer.wrap(data);
        DictionaryDefinitions defs = new DictionaryDefinitions(buffer, info);

        int[] chunks = defs.getChunks();

        assertEquals(1, chunks.length);
        assertEquals(0x0F, chunks[0]);
        assertEquals("6", defs.getFilename());
    }
}