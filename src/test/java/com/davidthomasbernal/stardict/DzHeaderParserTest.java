package com.davidthomasbernal.stardict;

import com.davidthomasbernal.stardict.DzHeader;
import com.davidthomasbernal.stardict.DzHeaderParser;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

import static org.junit.Assert.*;

public class DzHeaderParserTest {

    @Test
    public void testParse() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1F, // gzip id
                0x08, // compression method 8 = DEFLATE
                12, // extra + fname
                84, (byte) 226, 90, 81, // mtime
                0, // XFL
                5, // os
                0x00, 0x0C, // bytes of extra data
                0x41, 0x52, // subfield ID for dz data
                0x00, 0x08, // size of subfield data
                0x00, 0x01, // dz version
                0x00, 0x1F, // chunk length
                0x00, 0x01, // chunk count
                0x00, 0x0F, // chunk size
                0x36, 0x00, // fname
        };

        ByteBuffer buffer = ByteBuffer.wrap(data);
        DzHeaderParser parser = new DzHeaderParser();
        DzHeader header = parser.parse(buffer);

        assertEquals(0x08, header.getCompressionMethod());
        assertEquals(12, header.getFlags());
        assertEquals(1424120401, header.getmTime());
        assertTrue(header.hasExtra());
        assertTrue(header.hasFileName());
        assertFalse(header.hasComment());
        assertFalse(header.hasCrc());
        assertEquals(0, header.getXfl());
        assertEquals(5, header.getOs());
        assertEquals(1, header.getRaChunks().length);
        assertEquals(0x0F, header.getRaChunks()[0]);
        assertEquals(0x1F, header.getChlen());
        assertEquals("6", header.getFilename());
    }

    @Test(expected = RuntimeException.class)
    public void testInitNotGzip() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1E, // not a gzip id
                0x08, // compression method 8 = DEFLATE
                12, // extra + fname
                84, (byte) 226, 90, 81, // mtime
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

        ByteBuffer buffer = ByteBuffer.wrap(data);
        DzHeaderParser parser = new DzHeaderParser();
        parser.parse(buffer);
    }

    @Test(expected = RuntimeException.class)
    public void testInitNoDzField() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1F, // not a gzip id
                0x08, // compression method 8 = DEFLATE
                12, // extra + fname
                84, (byte) 226, 90, 81, // mtime
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

        ByteBuffer buffer = ByteBuffer.wrap(data);
        DzHeaderParser header = new DzHeaderParser();
        header.parse(buffer);
    }

    @Test(expected = RuntimeException.class)
    public void testInitNoExtraData() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1F, // not a gzip id
                0x08, // compression method 8 = DEFLATE
                0x08, // missing extra!, has fname
                84, (byte) 226, 90, 81, // mtime
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

        ByteBuffer buffer = ByteBuffer.wrap(data);
        DzHeaderParser header = new DzHeaderParser();
        header.parse(buffer);
    }

    @Test(expected = RuntimeException.class)
    public void testInitWrongDzVersion() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1F, // not a gzip id
                0x08, // compression method 8 = DEFLATE
                0x08, // missing extra!, has fname
                84, (byte) 226, 90, 81, // mtime
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

        ByteBuffer buffer = ByteBuffer.wrap(data);
        DzHeaderParser parser = new DzHeaderParser();

        parser.parse(buffer);
    }

    @Test
    public void testInitWithExtraBeforeDz() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1F, // gzip id
                0x08, // compression method 8 = DEFLATE
                12, // extra + fname
                84, (byte) 226, 90, 81, // mtime
                0, // XFL
                0, // os
                0x00, 0x0C, // bytes of extra data
                0x41, (byte) 0xAA, // some other subfield
                0x00, 0x01, //   with one byte
                0x00,       //   other subfield data
                0x41, 0x52, // subfield ID for dz data
                0x00, 0x08, // size of subfield data
                0x00, 0x01, // dz version
                0x00, 0x1F, // chunk length
                0x00, 0x01, // chunk count
                0x00, 0x0F, // chunk size
                0x36, 0x37, 0x00, // fname
        };

        ByteBuffer buffer = ByteBuffer.wrap(data);

        DzHeaderParser parser = new DzHeaderParser();
        DzHeader header = parser.parse(buffer);

        assertEquals(0x08, header.getCompressionMethod());
        assertTrue(header.hasExtra());
        assertTrue(header.hasFileName());
        assertFalse(header.hasComment());
        assertFalse(header.hasCrc());
        assertEquals(1424120401, header.getmTime());
        assertEquals(1, header.getRaChunks().length);
        assertEquals(0x0F, header.getRaChunks()[0]);
        assertEquals(0x1F, header.getChlen());
        assertEquals("67", header.getFilename());
    }

    @Test
    public void testInitWithExtraAfterDz() throws DataFormatException {
        byte [] data = new byte[] {
                (byte) 0x8B, 0x1F, // gzip id
                0x08, // compression method 8 = DEFLATE
                12, // extra + fname
                84, (byte) 226, 90, 81, // mtime
                0, // XFL
                0, // os
                0x00, 0x11, // bytes of extra data
                0x41, 0x52, // subfield ID for dz data
                0x00, 0x08, // size of subfield data
                0x00, 0x01, // dz version
                0x00, 0x1F, // chunk length
                0x00, 0x01, // chunk count
                0x00, 0x0F, // chunk size
                0x41, (byte) 0xAA, // some other subfield
                0x00, 0x01, //   with one byte
                0x00,       //   other subfield data
                0x36, 0x37, 0x00, // fname
        };

        ByteBuffer buffer = ByteBuffer.wrap(data);

        DzHeaderParser parser = new DzHeaderParser();
        DzHeader header = parser.parse(buffer);

        assertEquals(0x08, header.getCompressionMethod());
        assertEquals(1424120401, header.getmTime());
        assertEquals(1, header.getRaChunks().length);
        assertTrue(header.hasExtra());
        assertTrue(header.hasFileName());
        assertFalse(header.hasComment());
        assertFalse(header.hasCrc());
        assertEquals(0x1F, header.getChlen());
        assertEquals(0x0F, header.getRaChunks()[0]);
        assertEquals("67", header.getFilename());
    }
}