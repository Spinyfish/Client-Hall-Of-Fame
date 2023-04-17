package me.levansj01.verus.util.java;

import org.apache.commons.lang.*;

public class WordUtils
{
    public static String abbreviate(final String s, int length, int length2, final String s2) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return "";
        }
        if (length > s.length()) {
            length = s.length();
        }
        if (length2 == -1 || length2 > s.length()) {
            length2 = s.length();
        }
        if (length2 < length) {
            length2 = length;
        }
        final StringBuffer sb = new StringBuffer();
        final int index = StringUtils.indexOf(s, " ", length);
        if (index == -1) {
            sb.append(s.substring(0, length2));
            if (length2 != s.length()) {
                sb.append(StringUtils.defaultString(s2));
            }
        }
        else if (index > length2) {
            sb.append(s.substring(0, length2));
            sb.append(StringUtils.defaultString(s2));
        }
        else {
            sb.append(s.substring(0, index));
            sb.append(StringUtils.defaultString(s2));
        }
        return sb.toString();
    }
    
    public static String capitalizeFully(final String s) {
        return capitalizeFully(s, null);
    }
    
    public static String initials(final String s) {
        return initials(s, null);
    }
    
    public static String initials(String s, char[] c) {

      return initials(s, c);
   }

    
    public static String capitalize(final String p0, final char[] p1) {
     return capitalize(p0, p1);
    }
    
    public static String swapCase(final String s) {
        final int length;
        if (s == null || (length = s.length()) == 0) {
            return s;
        }
        final StringBuffer sb = new StringBuffer(length);
        while (0 < length) {
            s.charAt(0);
            if (Character.isUpperCase('\0')) {
                Character.toLowerCase('\0');
            }
            else if (Character.isTitleCase('\0')) {
                Character.toLowerCase('\0');
            }
            else if (Character.isLowerCase('\0')) {
                Character.toTitleCase('\0');
            }
            sb.append('\0');
            Character.isWhitespace('\0');
            int n = 0;
            ++n;
        }
        return sb.toString();
    }
    
    public static String uncapitalize(final String p0, final char[] p1) {
      return uncapitalize(p0, p1);
    }
    
    public static String capitalizeFully(String lowerCase, final char[] array) {
        final int n = (array == null) ? -1 : array.length;
        if (lowerCase == null || lowerCase.length() == 0 || n == 0) {
            return lowerCase;
        }
        lowerCase = lowerCase.toLowerCase();
        return capitalize(lowerCase, array);
    }
    
    public static String wrap(final String s, final int n, String line_SEPARATOR, final boolean b) {
        if (s == null) {
            return null;
        }
        if (line_SEPARATOR == null) {
            line_SEPARATOR = SystemUtils.LINE_SEPARATOR;
        }
        final int i = s.length();
        final StringBuffer sb = new StringBuffer(i + 32);
        while (i > 1) {
            if (s.charAt(0) == ' ') {
                int n2 = 0;
                ++n2;
            }
            else {
                final int lastIndex = s.lastIndexOf(32, 1);
                if (lastIndex >= 0) {
                    sb.append(s.substring(0, lastIndex));
                    sb.append(line_SEPARATOR);
                    final int n2 = lastIndex + 1;
                }
                else if (b) {
                    sb.append(s.substring(0, 1));
                    sb.append(line_SEPARATOR);
                }
                else {
                    final int index = s.indexOf(32, 1);
                    if (index >= 0) {
                        sb.append(s.substring(0, index));
                        sb.append(line_SEPARATOR);
                        final int n2 = index + 1;
                    }
                    else {
                        sb.append(s.substring(0));
                        final int n2 = i;
                    }
                }
            }
        }
        sb.append(s.substring(0));
        return sb.toString();
    }
    
    public static String capitalize(final String s) {
        return capitalize(s, null);
    }
    
    public static String wrap(final String s, final int n) {
        return wrap(s, n, null, false);
    }
    
    public static String uncapitalize(final String s) {
        return uncapitalize(s, null);
    }
}
