package me.kansio.client.utils.chat;

import java.util.Random;

public class NameUtil {

    private static final Random random = new Random();

    public static String generateName() {
        String name = "";
        final int nameLength = (int)Math.round(Math.random() * 4.0) + 5;
        final String vowels = "aeiouy";
        final String consonants = "bcdfghklmnprstvwz";
        int usedConsonants = 0;
        int usedVowels = 0;
        String lastLetter = "blah";
        for (int i = 0; i < nameLength; ++i) {
            String nextLetter = lastLetter;
            if ((random.nextBoolean() || usedConsonants == 1) && usedVowels < 2) {
                while (nextLetter.equals(lastLetter)) {
                    final int letterIndex = (int)(Math.random() * vowels.length() - 1.0);
                    nextLetter = vowels.substring(letterIndex, letterIndex + 1);
                }
                usedConsonants = 0;
                ++usedVowels;
            }
            else {
                while (nextLetter.equals(lastLetter)) {
                    final int letterIndex = (int)(Math.random() * consonants.length() - 1.0);
                    nextLetter = consonants.substring(letterIndex, letterIndex + 1);
                }
                ++usedConsonants;
                usedVowels = 0;
            }
            lastLetter = nextLetter;
            name = name.concat(nextLetter);
        }
        final int capitalMode = (int)Math.round(Math.random() * 2.0);
        if (capitalMode == 1) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        else if (capitalMode == 2) {
            for (int j = 0; j < nameLength; ++j) {
                if ((int)Math.round(Math.random() * 3.0) == 1) {
                    name = String.valueOf(name.substring(0, j)) + name.substring(j, j + 1).toUpperCase() + ((j == nameLength) ? "" : name.substring(j + 1));
                }
            }
        }
        final int numberLength = (int)Math.round(Math.random() * 3.0) + 1;
        final int numberMode = (int)Math.round(Math.random() * 3.0);
        final boolean number = random.nextBoolean();
        if (number) {
            if (numberLength == 1) {
                final int nextNumber = (int)Math.round(Math.random() * 9.0);
                name = name.concat(Integer.toString(nextNumber));
            }
            else if (numberMode == 0) {
                final int nextNumber = (int)(Math.round(Math.random() * 8.0) + 1L);
                for (int k = 0; k < numberLength; ++k) {
                    name = name.concat(Integer.toString(nextNumber));
                }
            }
            else if (numberMode == 1) {
                final int nextNumber = (int)(Math.round(Math.random() * 8.0) + 1L);
                name = name.concat(Integer.toString(nextNumber));
                for (int k = 1; k < numberLength; ++k) {
                    name = name.concat("0");
                }
            }
            else if (numberMode == 2) {
                int nextNumber = (int)(Math.round(Math.random() * 8.0) + 1L);
                name = name.concat(Integer.toString(nextNumber));
                for (int k = 0; k < numberLength; ++k) {
                    nextNumber = (int)Math.round(Math.random() * 9.0);
                    name = name.concat(Integer.toString(nextNumber));
                }
            }
            else if (numberMode == 3) {
                int nextNumber;
                for (nextNumber = 99999; Integer.toString(nextNumber).length() != numberLength; nextNumber = (int)(Math.round(Math.random() * 12.0) + 1L), nextNumber = (int)Math.pow(2.0, nextNumber)) {}
                name = name.concat(Integer.toString(nextNumber));
            }
        }
        final boolean leet = !number && random.nextBoolean();
        if (leet) {
            for (String oldName = name; name.equals(oldName); name = name.replace("l", "7"), name = name.replace("L", "7")) {
                final int leetMode = (int)Math.round(Math.random() * 7.0);
                if (leetMode == 0) {
                    name = name.replace("a", "4");
                    name = name.replace("A", "4");
                }
                if (leetMode == 1) {
                    name = name.replace("e", "3");
                    name = name.replace("E", "3");
                }
                if (leetMode == 2) {
                    name = name.replace("g", "6");
                    name = name.replace("G", "6");
                }
                if (leetMode == 3) {
                    name = name.replace("h", "4");
                    name = name.replace("H", "4");
                }
                if (leetMode == 4) {
                    name = name.replace("i", "1");
                    name = name.replace("I", "1");
                }
                if (leetMode == 5) {
                    name = name.replace("o", "0");
                    name = name.replace("O", "0");
                }
                if (leetMode == 6) {
                    name = name.replace("s", "5");
                    name = name.replace("S", "5");
                }
                if (leetMode == 7) {}
            }
        }
        final int special = (int)Math.round(Math.random() * 8.0);
        if (special == 3) {
            name = "xX".concat(name).concat("Xx");
        }
        else if (special == 4) {
            name = name.concat("LP");
        }
        else if (special == 5) {
            name = name.concat("HD");
        }
        return name;
    }

}
