package com.davidthomasbernal.stardict;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Created by david on 2/16/15.
 */
class DzHeaderParser {
    public DzHeader parse(ByteBuffer buffer) {
        DzHeaderMut header = new DzHeaderMut();

        header.id = getUnsignedShort(buffer);
        if (header.id != DzHeader.GZIP_ID) {
            throw new RuntimeException("Missing id values in header");
        }

        header.compressionMethod = getUnsignedByte(buffer);
        if (header.compressionMethod != DzHeader.COMPRESSION_METHOD_DEFLATE) {
            throw new RuntimeException("Unknown compression method");
        }

        header.flags = getUnsignedByte(buffer);

        header.mTime = getUnsignedInt(buffer);
        header.xfl = getUnsignedByte(buffer);
        header.os = getUnsignedByte(buffer);

        parseExtraData(buffer, header);

        if (header.hasFileName()) {
            header.filename = getString(buffer);
        }

        if (header.hasComment()) {
            header.comment = getString(buffer);
        }

        if (header.hasCrc()) {
            header.crc = getUnsignedShort(buffer);
        }

        return header.asImmutable();
    }

    private void parseExtraData(ByteBuffer buffer, DzHeaderMut header) {
        if (!header.hasExtra()) {
            throw new RuntimeException("I need the dz to have an extra!");
        }

        int remainingExtra = skipToRAData(buffer);
        if (remainingExtra == -1) {
            throw new RuntimeException("Failed to find RA data!");
        }

        int raRead = parseRaChunks(buffer, header);

        // skip through the rest of extraData
        for(int i = 0; i < (remainingExtra - raRead); i++) {
            getUnsignedByte(buffer);
        }
    }

    private int parseRaChunks(ByteBuffer buffer, DzHeaderMut header) {
        int raRead = 0;

        int raSize = getUnsignedShort(buffer);
        raRead += 2;

        int version = getUnsignedShort(buffer);
        raRead += 2;

        if (version != 1) {
            throw new RuntimeException("Unknown dict.dz version!");
        }

        header.chLen = getUnsignedShort(buffer);
        raRead += 2;

        int chcnt = getUnsignedShort(buffer);
        raRead += 2;

        if ((raSize - (raRead - 2)) != chcnt * 2) {
            throw new RuntimeException("Subfield size remaining too small for chunk count");
        }

        header.raChunks = new int[chcnt];
        for (int i = 0; i < chcnt; i++) {
            header.raChunks[i] = getUnsignedShort(buffer);
            raRead += 2;
        }

        return raRead;
    }

    protected int skipToRAData(ByteBuffer buffer) {
        int xlen = getUnsignedShort(buffer);

        boolean foundRaField = false;

        int bytesRead = 0;
        while (bytesRead < xlen) {
            int subFieldId = getUnsignedShort(buffer);
            bytesRead += 2;

            if (subFieldId == DzHeader.RA_ID) {
                break;
            } else {
                int subFieldLength = getUnsignedShort(buffer);
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

    protected int getUnsignedShort(ByteBuffer buffer) {
        return (buffer.getShort() & 0xFFFF);
    }

    protected short getUnsignedByte(ByteBuffer buffer) {
        return (short) (buffer.get() & 0xFF);
    }

    protected long getUnsignedInt(ByteBuffer buffer) {
        return (buffer.getInt() & 0xFFFFFFFFL);
    }

    protected String getString(ByteBuffer buffer) {
        ByteArrayOutputStream stringBytes = new ByteArrayOutputStream();
        byte b;
        while ((b = buffer.get()) != 0) {
            stringBytes.write(b);
        }
        return new String(stringBytes.toByteArray(), StandardCharsets.ISO_8859_1);
    }

    private static class DzHeaderMut {
        private short compressionMethod;
        private short flags;
        private long mTime;
        private short xfl;
        private short os;

        private int chLen;
        private int[] raChunks;

        private String filename;
        private String comment;

        private int crc;
        public int id;

        public DzHeader asImmutable() {
            return new DzHeader(
                    id,
                    compressionMethod,
                    flags,
                    mTime,
                    xfl,
                    os,
                    chLen,
                    raChunks,
                    filename,
                    comment,
                    crc
            );
        }

        public boolean hasFileName() {
            return (flags & DzHeader.FORMAT_FNAME) != 0;
        }

        public boolean hasComment() {
            return (flags & DzHeader.FORMAT_COMMENT) != 0;
        }

        public boolean hasExtra() {
            return (flags & DzHeader.FORMAT_EXTRA) != 0;
        }

        public boolean hasCrc() {
            return (flags & DzHeader.FORMAT_CRC) != 0;
        }
    }
}
