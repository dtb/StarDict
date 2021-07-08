package com.davidthomasbernal.stardict;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.zip.DataFormatException;

public class Main {

    public static void main(String[] args) throws IOException, DataFormatException {
	    Dictionary dictionary = Dictionary.fromIfo("/Users/david/IdeaProjects/StarDict/dictionaries/stardict-dictd-web1913-2.4.2/dictd_www.dict.org_web1913.ifo", false);

        String[] words = new String[] {
                "c",
                "ca",
                "cat"
        };
        for (String word : words) {
            long start = System.nanoTime();
            Set<String> res = dictionary.searchForWord(word);
            long duration = (System.nanoTime() - start) / 1000000;
            System.out.println(String.format("That took %d ms", duration));
        }

        System.out.println();
    }
}
