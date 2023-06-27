package net.minecraft.util;

import java.util.regex.*;

public class StringUtils
{
    private static final Pattern patternControlCode;
    
    public static String ticksToElapsedTime(final int ticks) {
        int i = ticks / 20;
        final int j = i / 60;
        i %= 60;
        return (i < 10) ? (j + ":0" + i) : (j + ":" + i);
    }
    
    public static String stripControlCodes(final String p_76338_0_) {
        return StringUtils.patternControlCode.matcher(p_76338_0_).replaceAll("");
    }
    
    public static boolean isNullOrEmpty(final String string) {
        return org.apache.commons.lang3.StringUtils.isEmpty((CharSequence)string);
    }
    
    static {
        patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    }
}
