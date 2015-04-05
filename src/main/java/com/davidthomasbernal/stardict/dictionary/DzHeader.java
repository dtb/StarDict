package com.davidthomasbernal.stardict.dictionary;

/**
 * Created by david on 2/16/15.
 */
final public class DzHeader {
    public static final int FORMAT_TEXT = 1;
    public static final int FORMAT_CRC = 2;
    public static final int FORMAT_EXTRA = 4;
    public static final int FORMAT_FNAME = 8;
    public static final int FORMAT_COMMENT = 16;

    public static final int COMPRESSION_METHOD_DEFLATE = 0x08;

    /* "RA" for random access, but backwards b/c little endian */
    public static final int RA_ID = 0x4152;

    public static final int GZIP_ID = 0x8B1F;

    private final int id;
    private final short compressionMethod;
    private final short flags;
    private final long mTime;
    private final short xfl;
    private final short os;

    private final int chlen;
    private final int[] raChunks;

    private final String filename;
    private final String comment;

    private final int crc;

    public DzHeader(int id,
                    short compressionMethod,
                    short flags,
                    long mTime,
                    short xfl,
                    short os,
                    int chlen,
                    int[] raChunks,
                    String filename,
                    String comment,
                    int crc) {

        this.id = id;
        this.compressionMethod = compressionMethod;
        this.flags = flags;
        this.mTime = mTime;
        this.xfl = xfl;
        this.os = os;
        this.chlen = chlen;
        this.raChunks = raChunks;
        this.filename = filename;
        this.comment = comment;
        this.crc = crc;
    }

    public short getCompressionMethod() {
        return compressionMethod;
    }

    public short getFlags() {
        return flags;
    }

    public long getmTime() {
        return mTime;
    }

    public short getXfl() {
        return xfl;
    }

    public short getOs() {
        return os;
    }

    public int[] getRaChunks() {
        return raChunks;
    }

    public String getFilename() {
        return filename;
    }

    public String getComment() {
        return comment;
    }

    public int getCrc() {
        return crc;
    }

    public int getChlen() {
        return chlen;
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
