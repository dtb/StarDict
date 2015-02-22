package com.davidthomasbernal.stardict;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.zip.DataFormatException;

public class Main {

    public static void main(String[] args) throws IOException, DataFormatException {
	    Dictionary dictionary = Dictionary.fromIfo("/Users/david/IdeaProjects/StarDict/dictionaries/stardict-dictd_www.dict.org_wn-2.4.2/dictd_www.dict.org_wn.ifo");

        List<String> words = dictionary.getWords();
//        for (int i = 0; i < words.size(); i++) {
//            System.out.println(words.get(i));
//        }


        System.out.println(dictionary.getName());
        System.out.println(dictionary.getWordCount());
        List<String> defs = dictionary.getDefinitions("end");
        System.out.println(defs.size());
        System.out.println(defs);

        List<String> defs1 = dictionary.getDefinitions("pathos");
        System.out.println(defs1.size());
        System.out.println(defs1);

        List<String> defs2 = dictionary.getDefinitions("contact");
        System.out.println(defs2.size());
        System.out.println(defs2);

        List<String> defs3 = dictionary.getDefinitions("cat");
        System.out.println(defs3.size());
        System.out.println(defs3);
    }
}
