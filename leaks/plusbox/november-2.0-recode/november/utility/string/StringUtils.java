package lol.november.utility.string;

import java.util.StringJoiner;

public class StringUtils {
    /**
     * Formats an enum to a string.
     */
    public static String formatCase(String s) {
        StringJoiner joiner = new StringJoiner(" ");

        for (String word : s.split("_")) {
            joiner.add(
                    Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase()
            );
        }

        return joiner.toString();
    }
}
