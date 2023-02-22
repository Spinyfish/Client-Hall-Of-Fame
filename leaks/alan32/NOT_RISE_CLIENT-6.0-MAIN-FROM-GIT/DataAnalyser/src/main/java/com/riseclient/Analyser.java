package com.riseclient;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;

public class Analyser {
    private String PATH = "Ban Data/";
    private final HashMap<String, Integer> result = new HashMap<>();

    public Analyser(String path) {
        PATH += path;
    }

    @SneakyThrows
    public void run() {
        File[] files = new File(PATH).listFiles();
        if (files == null) throw new FileNotFoundException();

        for (File comparingFile1 : files) {
            for (File comparingFile2 : files) {
                String[] content1 = FileUtils.readFileToString(comparingFile1).split("\n");
                String[] content2 = FileUtils.readFileToString(comparingFile2).split("\n");

                boolean bypassing1 = content1[0].contains("true");
                boolean bypassing2 = content2[0].contains("true");

                if (!(bypassing1 && !bypassing2)) continue;

                for (String line2 : content2) {
                    if (line2.contains("Bypassing")) continue;

                    if (!Arrays.asList(content1).contains(line2)) {

                        Integer occurrences = result.get(line2);
                        occurrences = occurrences == null ? 0 : occurrences;

                        result.remove(line2);
                        result.put(line2, occurrences + 1);

                    }
                }
            }
        }

        System.out.println(result);

    }

}
