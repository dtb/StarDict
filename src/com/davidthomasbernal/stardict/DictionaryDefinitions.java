package com.davidthomasbernal.stardict;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class DictionaryDefinitions {

    private static final int COMPRESSION_METHOD_DEFLATE = 0x08;

    private int dataOffset;

    private static final int FORMAT_TEXT = 1;
    private static final int FORMAT_CRC = 2;
    private static final int FORMAT_EXTRA = 4;
    private static final int FORMAT_FNAME = 8;
    private static final int FORMAT_COMMENT = 16;

    private final DictionaryInfo dictionaryInfo;
    private final DictionaryIndex dictionaryIndex;
    private final ByteBuffer buffer;

    private static final int BUFFER_SIZE = 1024 * 1024;

    public DictionaryDefinitions(File dict, DictionaryIndex dictionaryIndex, DictionaryInfo dictionaryInfo) throws IOException {
        this.dictionaryIndex = dictionaryIndex;
        this.dictionaryInfo = dictionaryInfo;

        RandomAccessFile file = new RandomAccessFile(dict, "r");
        FileChannel channel = file.getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, BUFFER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);


        initialize();
    }

    private void initialize() {
        // ok, so we read a short, because we only want to read two bytes. But java doesn't have unsigned types, so
        // the short gets interpreted as a negative number (the high bit indicates the sign.) When we cast to an int,
        // the upper bit from the short is copied over to the upper bit of the integer to preserve the sign ("sign
        // extension".) We never wanted that damned sign bit in the first place, so we & with 0xFFFF to chop off that
        // extra bit
        int id = getUnsignedShort();
        if (id != 0x8B1F) {
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

        long mtime = getUnsignedInt();

        // who cares
        short xfl = getUnsignedByte();
        short os = getUnsignedByte();

        int xlen = getUnsignedShort();
        byte[] extraData = new byte[xlen];
        buffer.get(extraData);

        String filename = null;
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
}
