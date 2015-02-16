package com.davidthomasbernal.stardict;

import java.io.IOException;
import java.util.zip.DataFormatException;

public class Main {

    public static void main(String[] args) throws IOException, DataFormatException {
	    Dictionary dictionary = Dictionary.fromIfo("/Users/david/IdeaProjects/StarDict/dictionaries/stardict-dictd-web1913-2.4.2/dictd_www.dict.org_web1913.ifo");

        System.out.println(dictionary.getName());
        System.out.println(dictionary.getWordCount());
    }
}
