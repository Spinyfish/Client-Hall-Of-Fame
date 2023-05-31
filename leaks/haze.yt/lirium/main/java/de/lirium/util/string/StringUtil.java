package de.lirium.util.string;

import god.buddy.aot.BCompiler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static String[] getArguments(String input) {
        ArrayList<String> arguments = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
        while (m.find()) arguments.add(m.group(1).replace("\"", ""));
        return arguments.toArray(new String[0]);
    }

    public static boolean isNumber(String input) {
        return Pattern.compile("\\d+(\\.\\d+)*").matcher(input).find();
    }

    public static boolean isBoolean(String input) {
        return Pattern.compile("true|false").matcher(input).find();
    }

    public static boolean startsWith(String input, List<String> check, boolean ignoreCase) {
        return getMatching(input, check, ignoreCase) != null;
    }

    public static String getMatching(String input, List<String> check, boolean ignoreCase) {
        for (String s : check) {
            if (ignoreCase && input.toLowerCase().startsWith(s.toLowerCase())) return s;
            if (input.startsWith(s)) return s;
        }
        return null;
    }
}