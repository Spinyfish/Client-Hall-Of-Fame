/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class StrUtils {
    public static boolean equalsMask(String str, String mask, char wildChar, char wildCharSingle) {
        if (mask != null && str != null) {
            String s1;
            if (mask.indexOf(wildChar) < 0) {
                return mask.indexOf(wildCharSingle) < 0 ? mask.equals(str) : StrUtils.equalsMaskSingle(str, mask, wildCharSingle);
            }
            ArrayList<String> list = new ArrayList<String>();
            String s = "" + wildChar;
            if (mask.startsWith(s)) {
                list.add("");
            }
            StringTokenizer stringtokenizer = new StringTokenizer(mask, s);
            while (stringtokenizer.hasMoreElements()) {
                list.add(stringtokenizer.nextToken());
            }
            if (mask.endsWith(s)) {
                list.add("");
            }
            if (!StrUtils.startsWithMaskSingle(str, s1 = (String)list.get(0), wildCharSingle)) {
                return false;
            }
            String s2 = (String)list.get(list.size() - 1);
            if (!StrUtils.endsWithMaskSingle(str, s2, wildCharSingle)) {
                return false;
            }
            int i = 0;
            for (int j = 0; j < list.size(); ++j) {
                String s3 = (String)list.get(j);
                if (s3.length() <= 0) continue;
                int k = StrUtils.indexOfMaskSingle(str, s3, i, wildCharSingle);
                if (k < 0) {
                    return false;
                }
                i = k + s3.length();
            }
            return true;
        }
        return mask == str;
    }

    private static boolean equalsMaskSingle(String str, String mask, char wildCharSingle) {
        if (str != null && mask != null) {
            if (str.length() != mask.length()) {
                return false;
            }
            for (int i = 0; i < mask.length(); ++i) {
                char c0 = mask.charAt(i);
                if (c0 == wildCharSingle || str.charAt(i) == c0) continue;
                return false;
            }
            return true;
        }
        return str == mask;
    }

    private static int indexOfMaskSingle(String str, String mask, int startPos, char wildCharSingle) {
        if (str != null && mask != null) {
            if (startPos >= 0 && startPos <= str.length()) {
                if (str.length() < startPos + mask.length()) {
                    return -1;
                }
                int i = startPos;
                while (i + mask.length() <= str.length()) {
                    String s = str.substring(i, i + mask.length());
                    if (StrUtils.equalsMaskSingle(s, mask, wildCharSingle)) {
                        return i;
                    }
                    ++i;
                }
                return -1;
            }
            return -1;
        }
        return -1;
    }

    private static boolean endsWithMaskSingle(String str, String mask, char wildCharSingle) {
        if (str != null && mask != null) {
            if (str.length() < mask.length()) {
                return false;
            }
            String s = str.substring(str.length() - mask.length(), str.length());
            return StrUtils.equalsMaskSingle(s, mask, wildCharSingle);
        }
        return str == mask;
    }

    private static boolean startsWithMaskSingle(String str, String mask, char wildCharSingle) {
        if (str != null && mask != null) {
            if (str.length() < mask.length()) {
                return false;
            }
            String s = str.substring(0, mask.length());
            return StrUtils.equalsMaskSingle(s, mask, wildCharSingle);
        }
        return str == mask;
    }

    public static boolean equalsMask(String str, String[] masks, char wildChar) {
        for (int i = 0; i < masks.length; ++i) {
            String s = masks[i];
            if (!StrUtils.equalsMask(str, s, wildChar)) continue;
            return true;
        }
        return false;
    }

    public static boolean equalsMask(String str, String mask, char wildChar) {
        if (mask != null && str != null) {
            String s1;
            if (mask.indexOf(wildChar) < 0) {
                return mask.equals(str);
            }
            ArrayList<String> list = new ArrayList<String>();
            String s = "" + wildChar;
            if (mask.startsWith(s)) {
                list.add("");
            }
            StringTokenizer stringtokenizer = new StringTokenizer(mask, s);
            while (stringtokenizer.hasMoreElements()) {
                list.add(stringtokenizer.nextToken());
            }
            if (mask.endsWith(s)) {
                list.add("");
            }
            if (!str.startsWith(s1 = (String)list.get(0))) {
                return false;
            }
            String s2 = (String)list.get(list.size() - 1);
            if (!str.endsWith(s2)) {
                return false;
            }
            int i = 0;
            for (int j = 0; j < list.size(); ++j) {
                String s3 = (String)list.get(j);
                if (s3.length() <= 0) continue;
                int k = str.indexOf(s3, i);
                if (k < 0) {
                    return false;
                }
                i = k + s3.length();
            }
            return true;
        }
        return mask == str;
    }

    public static String[] split(String str, String separators) {
        if (str != null && str.length() > 0) {
            if (separators == null) {
                return new String[]{str};
            }
            ArrayList<String> list = new ArrayList<String>();
            int i = 0;
            for (int j = 0; j < str.length(); ++j) {
                char c0 = str.charAt(j);
                if (!StrUtils.equals(c0, separators)) continue;
                list.add(str.substring(i, j));
                i = j + 1;
            }
            list.add(str.substring(i, str.length()));
            return list.toArray(new String[list.size()]);
        }
        return new String[0];
    }

    private static boolean equals(char ch, String matches) {
        for (int i = 0; i < matches.length(); ++i) {
            if (matches.charAt(i) != ch) continue;
            return true;
        }
        return false;
    }

    public static boolean equalsTrim(String a, String b) {
        if (a != null) {
            a = a.trim();
        }
        if (b != null) {
            b = b.trim();
        }
        return StrUtils.equals(a, (Object)b);
    }

    public static boolean isEmpty(String string) {
        return string == null ? true : string.trim().length() <= 0;
    }

    public static String stringInc(String str) {
        int i = StrUtils.parseInt(str, -1);
        if (i == -1) {
            return "";
        }
        String s = "" + ++i;
        return s.length() > str.length() ? "" : StrUtils.fillLeft("" + i, str.length(), '0');
    }

    public static int parseInt(String s, int defVal) {
        if (s == null) {
            return defVal;
        }
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException var3) {
            return defVal;
        }
    }

    public static boolean isFilled(String string) {
        return !StrUtils.isEmpty(string);
    }

    public static String addIfNotContains(String target, String source) {
        for (int i = 0; i < source.length(); ++i) {
            if (target.indexOf(source.charAt(i)) >= 0) continue;
            target = target + source.charAt(i);
        }
        return target;
    }

    public static String fillLeft(String s, int len, char fillChar) {
        if (s == null) {
            s = "";
        }
        if (s.length() >= len) {
            return s;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = len - s.length();
        while (stringbuffer.length() < i) {
            stringbuffer.append(fillChar);
        }
        return stringbuffer.toString() + s;
    }

    public static String fillRight(String s, int len, char fillChar) {
        if (s == null) {
            s = "";
        }
        if (s.length() >= len) {
            return s;
        }
        StringBuffer stringbuffer = new StringBuffer(s);
        while (stringbuffer.length() < len) {
            stringbuffer.append(fillChar);
        }
        return stringbuffer.toString();
    }

    public static boolean equals(Object a, Object b) {
        return a == b ? true : (a != null && a.equals(b) ? true : b != null && b.equals(a));
    }

    public static boolean startsWith(String str, String[] prefixes) {
        if (str == null) {
            return false;
        }
        if (prefixes == null) {
            return false;
        }
        for (int i = 0; i < prefixes.length; ++i) {
            String s = prefixes[i];
            if (!str.startsWith(s)) continue;
            return true;
        }
        return false;
    }

    public static boolean endsWith(String str, String[] suffixes) {
        if (str == null) {
            return false;
        }
        if (suffixes == null) {
            return false;
        }
        for (int i = 0; i < suffixes.length; ++i) {
            String s = suffixes[i];
            if (!str.endsWith(s)) continue;
            return true;
        }
        return false;
    }

    public static String removePrefix(String str, String prefix) {
        if (str != null && prefix != null) {
            if (str.startsWith(prefix)) {
                str = str.substring(prefix.length());
            }
            return str;
        }
        return str;
    }

    public static String removeSuffix(String str, String suffix) {
        if (str != null && suffix != null) {
            if (str.endsWith(suffix)) {
                str = str.substring(0, str.length() - suffix.length());
            }
            return str;
        }
        return str;
    }

    public static String replaceSuffix(String str, String suffix, String suffixNew) {
        if (str != null && suffix != null) {
            if (!str.endsWith(suffix)) {
                return str;
            }
            if (suffixNew == null) {
                suffixNew = "";
            }
            str = str.substring(0, str.length() - suffix.length());
            return str + suffixNew;
        }
        return str;
    }

    public static String replacePrefix(String str, String prefix, String prefixNew) {
        if (str != null && prefix != null) {
            if (!str.startsWith(prefix)) {
                return str;
            }
            if (prefixNew == null) {
                prefixNew = "";
            }
            str = str.substring(prefix.length());
            return prefixNew + str;
        }
        return str;
    }

    public static int findPrefix(String[] strs, String prefix) {
        if (strs != null && prefix != null) {
            for (int i = 0; i < strs.length; ++i) {
                String s = strs[i];
                if (!s.startsWith(prefix)) continue;
                return i;
            }
            return -1;
        }
        return -1;
    }

    public static int findSuffix(String[] strs, String suffix) {
        if (strs != null && suffix != null) {
            for (int i = 0; i < strs.length; ++i) {
                String s = strs[i];
                if (!s.endsWith(suffix)) continue;
                return i;
            }
            return -1;
        }
        return -1;
    }

    public static String[] remove(String[] strs, int start, int end) {
        if (strs == null) {
            return strs;
        }
        if (end > 0 && start < strs.length) {
            if (start >= end) {
                return strs;
            }
            ArrayList<String> list = new ArrayList<String>(strs.length);
            for (int i = 0; i < strs.length; ++i) {
                String s = strs[i];
                if (i >= start && i < end) continue;
                list.add(s);
            }
            String[] astring = list.toArray(new String[list.size()]);
            return astring;
        }
        return strs;
    }

    public static String removeSuffix(String str, String[] suffixes) {
        if (str != null && suffixes != null) {
            String s;
            int i = str.length();
            for (int j = 0; j < suffixes.length && (str = StrUtils.removeSuffix(str, s = suffixes[j])).length() == i; ++j) {
            }
            return str;
        }
        return str;
    }

    public static String removePrefix(String str, String[] prefixes) {
        if (str != null && prefixes != null) {
            String s;
            int i = str.length();
            for (int j = 0; j < prefixes.length && (str = StrUtils.removePrefix(str, s = prefixes[j])).length() == i; ++j) {
            }
            return str;
        }
        return str;
    }

    public static String removePrefixSuffix(String str, String[] prefixes, String[] suffixes) {
        str = StrUtils.removePrefix(str, prefixes);
        str = StrUtils.removeSuffix(str, suffixes);
        return str;
    }

    public static String removePrefixSuffix(String str, String prefix, String suffix) {
        return StrUtils.removePrefixSuffix(str, new String[]{prefix}, new String[]{suffix});
    }

    public static String getSegment(String str, String start, String end) {
        if (str != null && start != null && end != null) {
            int i = str.indexOf(start);
            if (i < 0) {
                return null;
            }
            int j = str.indexOf(end, i);
            return j < 0 ? null : str.substring(i, j + end.length());
        }
        return null;
    }

    public static String addSuffixCheck(String str, String suffix) {
        return str != null && suffix != null ? (str.endsWith(suffix) ? str : str + suffix) : str;
    }

    public static String addPrefixCheck(String str, String prefix) {
        return str != null && prefix != null ? (str.endsWith(prefix) ? str : prefix + str) : str;
    }

    public static String trim(String str, String chars) {
        if (str != null && chars != null) {
            str = StrUtils.trimLeading(str, chars);
            str = StrUtils.trimTrailing(str, chars);
            return str;
        }
        return str;
    }

    public static String trimLeading(String str, String chars) {
        if (str != null && chars != null) {
            int i = str.length();
            for (int j = 0; j < i; ++j) {
                char c0 = str.charAt(j);
                if (chars.indexOf(c0) >= 0) continue;
                return str.substring(j);
            }
            return "";
        }
        return str;
    }

    public static String trimTrailing(String str, String chars) {
        if (str != null && chars != null) {
            int i;
            char c0;
            int j;
            for (j = i = str.length(); j > 0 && chars.indexOf(c0 = str.charAt(j - 1)) >= 0; --j) {
            }
            return j == i ? str : str.substring(0, j);
        }
        return str;
    }
}

