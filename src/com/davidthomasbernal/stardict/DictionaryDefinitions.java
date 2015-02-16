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

    private int dataOffset;

    private final DictionaryInfo dictionaryInfo;
    private final ByteBuffer buffer;

    private DzHeader header;

    private static final int BUFFER_SIZE = 1024 * 1024;

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
        DzHeaderParser parser = new DzHeaderParser();
        header = parser.parse(buffer);

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

}
