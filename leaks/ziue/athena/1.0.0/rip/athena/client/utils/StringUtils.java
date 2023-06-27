package rip.athena.client.utils;

import java.util.*;
import java.security.*;
import java.io.*;

public class StringUtils
{
    public static boolean isInteger(final String content) {
        try {
            Integer.valueOf(content);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isDouble(final String content) {
        try {
            Double.valueOf(content);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isLong(final String content) {
        try {
            Long.valueOf(content);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static String sanitize(final String content) {
        return content.replace("\n", "\\n").replace("\r", "\\r");
    }
    
    public static UUID getUUID(final String string) {
        try {
            return UUID.fromString(string);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    public static String ii() throws NoSuchAlgorithmException, IOException {
        String s = "";
        final String main = System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim();
        final byte[] bytes = main.getBytes("UTF-8");
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        final byte[] md5 = messageDigest.digest(bytes);
        int i = 0;
        for (final byte b : md5) {
            s += Integer.toHexString((b & 0xFF) | 0x300).substring(0, 3);
            if (i != md5.length - 1) {
                s += "-";
            }
            ++i;
        }
        return s;
    }
}
