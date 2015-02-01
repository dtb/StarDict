package com.davidthomasbernal.stardict;

import java.io.*;

class IfoParser {
    File ifo;

    public IfoParser(File ifo) {
        this.ifo = ifo;
    }

    public DictionaryInfo parse() throws IOException {

        DictionaryInfo result = new DictionaryInfo();

        BufferedReader reader = new BufferedReader(new FileReader(ifo));
        try {
            String line = reader.readLine();
            while (line != null) {
                String[] kv = line.split("=");
                if (kv.length != 2) {
                    throw new RuntimeException("ifo file is malformed");
                }

                String key = kv[0].trim();
                String value = kv[1].trim();

                setField(result, key, value);

                line = reader.readLine();
            }
        } finally {
            reader.close();
        }

        return result;
    }

    private void setField(DictionaryInfo result, String key, String value) {

    }
}
