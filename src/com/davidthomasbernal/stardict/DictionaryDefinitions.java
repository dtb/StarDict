package com.davidthomasbernal.stardict;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;

public class DictionaryDefinitions {

    private static final int COMPRESSION_METHOD_DEFLATE = 0x08;

    private int dataOffset;

    private static final int FORMAT_TEXT = 1;
    private static final int FORMAT_CRC = 2;
    private static final int FORMAT_EXTRA = 4;
    private static final int FORMAT_FNAME = 8;
    private static final int FORMAT_COMMENT = 16;

    /* "RA" for random access, but backwards b/c little endian */
    private static final int RA_ID = 0x4152;

    private static final int GZIP_ID = 0x8B1F;

    private final DictionaryInfo dictionaryInfo;
    private final ByteBuffer buffer;

    private int[] chunks;
    private String filename;

    private static final int BUFFER_SIZE = 1024 * 1024;

    // for testing
    public int[] getChunks() {
        return chunks.clone();
    }

    // for testing
    public String getFilename() {
        return filename;
    }

    public DictionaryDefinitions(ByteBuffer buffer, DictionaryInfo dictionaryInfo) throws DataFormatException {
        this.buffer = buffer;
        this.dictionaryInfo = dictionaryInfo;

        initialize();
    }

    public DictionaryDefinitions(File dict, DictionaryInfo dictionaryInfo) throws IOException, DataFormatException {
        this.dictionaryInfo = dictionaryInfo;

        RandomAccessFile file = new RandomAccessFile(dict, "r");
        FileChannel channel = file.getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, BUFFER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        initialize();
    }

    private void initialize() throws DataFormatException {
        int id = getUnsignedShort();
        if (id != GZIP_ID) {
            throw new RuntimeException("Missing id values in header");
        }

        short compressionMethod = getUnsignedByte();
        if (compressionMethod != COMPRESSION_METHOD_DEFLATE) {
            throw new RuntimeException("Unknown compression method");
        }

        short flags = getUnsignedByte();

        boolean hasFileName = (flags & FORMAT_FNAME) != 0;
        boolean hasComment = (flags & FORMAT_COMMENT) != 0;
        boolean hasExtra = (flags & FORMAT_EXTRA) != 0;
        boolean hasCrc = (flags & FORMAT_CRC) != 0;

        if (!hasExtra) {
            throw new RuntimeException("I need the dz to have an extra!");
        }

        // mtime, xfl, os
        getUnsignedInt();
        getUnsignedByte();
        getUnsignedByte();

        int remainingExtra = skipToRAData();
        if (remainingExtra == -1) {
            throw new RuntimeException("Failed to find RA data!");
        }

        int raRead = 0;

        int raSize = getUnsignedShort();
        raRead += 2;

        int version = getUnsignedShort();
        raRead += 2;

        if (version != 1) {
            throw new RuntimeException("Unknown dict.dz version!");
        }

        int chlen = getUnsignedShort();
        raRead += 2;

        int chcnt = getUnsignedShort();
        raRead += 2;

        if ((raSize - (raRead - 2)) != chcnt * 2) {
            throw new RuntimeException("Subfield size remaining too small for chunk count");
        }

        chunks = new int[chcnt];
        for (int i = 0; i < chcnt; i++) {
            chunks[i] = getUnsignedShort();
            raRead += 2;
        }

        // skip through the rest of extraData
        for(int i = 0; i < (remainingExtra - raRead); i++) {
            getUnsignedByte();
        }

        if (hasFileName) {
            filename = getString();
        }

        String comment;
        if (hasComment) {
            comment = getString();
        }

        int crc;
        if (hasCrc) {
            crc = getUnsignedShort(); // who cares
        }

        // here we are, we're at the first data blog omg this is great.
        // store the position so that we can use it to look up some fucking words later, hell yeah
        dataOffset = buffer.position();

//        byte [] firstChunk = new byte[chunks[0] + 1];
//        buffer.get(firstChunk);
//
//        Inflater inflater = new Inflater(true);
//        inflater.setInput(firstChunk);
//
//        byte[] output = new byte[chlen];
//        int bytes = inflater.inflate(output);
//        System.out.println(bytes);
    }

    protected int getUnsignedShort() {
        return (buffer.getShort() & 0xFFFF);
    }

    protected short getUnsignedByte() {
        return (short) (buffer.get() & 0xFF);
    }

    protected long getUnsignedInt() {
        return (buffer.getInt() & 0xFFFFFFFFL);
    }

    protected String getString() {
        ByteArrayOutputStream stringBytes = new ByteArrayOutputStream();
        byte b;
        while ((b = buffer.get()) != 0) {
            stringBytes.write(b);
        }
        return new String(stringBytes.toByteArray(), StandardCharsets.ISO_8859_1);
    }

    protected int skipToRAData() {
        int xlen = getUnsignedShort();

        boolean foundRaField = false;

        int bytesRead = 0;
        while (bytesRead < xlen) {
            int subFieldId = getUnsignedShort();
            bytesRead += 2;

            if (subFieldId == RA_ID) {
                break;
            } else {
                int subFieldLength = getUnsignedShort();
                bytesRead += 2;

                byte[] subFieldData = new byte[subFieldLength];

                buffer.get(subFieldData);
                bytesRead += subFieldLength;
            }
        }

        int remaining = xlen - bytesRead;

        if (remaining == 0) {
            return -1;
        } else {
            return remaining;
        }
    }
}
