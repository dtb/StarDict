package com.davidthomasbernal.stardict;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class DictionaryDefinitions {

    private int dataOffset;

    private final DictionaryInfo dictionaryInfo;
    private final ByteBuffer buffer;

    private DzHeader header;

    public DictionaryDefinitions(ByteBuffer buffer, DictionaryInfo dictionaryInfo) throws DataFormatException {
        this.buffer = buffer;
        this.dictionaryInfo = dictionaryInfo;

        initialize();
    }

    public DictionaryDefinitions(File dict, DictionaryInfo dictionaryInfo) throws IOException, DataFormatException {
        this.dictionaryInfo = dictionaryInfo;

        RandomAccessFile file = new RandomAccessFile(dict, "r");
        FileChannel channel = file.getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        initialize();
    }

    private void initialize() throws DataFormatException {
        DzHeaderParser parser = new DzHeaderParser();
        header = parser.parse(buffer);

        // here we are, we're at the first data blog omg this is great.
        // store the position so that we can use it to look up some fucking words later, hell yeah
        dataOffset = buffer.position();
    }

    public List<String> getDefinitions(IndexEntry entry) throws DataFormatException, IOException {
        int firstChunk = (int) (entry.dataOffset / header.getChlen());
        int lastChunk = (int) ((entry.dataOffset + entry.dataSize) / header.getChlen());

        int totalChunks = 1 + (firstChunk - lastChunk);

        byte[] chunkData = new byte[totalChunks * header.getChlen()];

        for (int chunkIndex = firstChunk, bytesRead = 0;
             chunkIndex <= lastChunk;
             chunkIndex++, bytesRead += header.getChlen()) {
            readChunkAtIndex(chunkIndex, chunkData, bytesRead);
        }

        int chunkOffset = (int) entry.dataOffset - firstChunk * header.getChlen();

        return readDefinitions(chunkData, chunkOffset, (int) entry.dataSize);
    }

    protected void readChunkAtIndex(int chunkIndex, byte[] outputBuffer, int writeOffset) throws DataFormatException {
        int offset = getChunkOffset(chunkIndex);

        byte[] chunk = new byte[header.getRaChunks()[chunkIndex]];
        buffer.position(offset);
        buffer.get(chunk);

        Inflater inflater = new Inflater(true);
        inflater.setInput(chunk);

        inflater.inflate(outputBuffer, writeOffset, header.getChlen());
    }

    protected int getChunkOffset(int chunkIndex) {
        int offset = dataOffset;
        for (int i = 0; i < chunkIndex; i++) {
            offset += header.getRaChunks()[i];
        }

        return offset;
    }

    protected List<String> readDefinitions(byte[] bytes, int startIndex, int maxBytes) {
        List<String> strings = new LinkedList<String>();

        int bytesRead = 0;
        int wordStartIndex = startIndex;
        while (bytesRead < maxBytes) {
            bytesRead++;
            if (bytes[startIndex + bytesRead] == 0) {
                strings.add(new String(bytes, wordStartIndex, startIndex + bytesRead - 1, StandardCharsets.UTF_8));
                bytesRead++;
                wordStartIndex = startIndex + bytesRead;
            }
        }

        if (startIndex + bytesRead - wordStartIndex > 0) {
            strings.add(new String(bytes, wordStartIndex, startIndex + bytesRead - wordStartIndex, StandardCharsets.UTF_8));
        }

        return strings;
    }


}
