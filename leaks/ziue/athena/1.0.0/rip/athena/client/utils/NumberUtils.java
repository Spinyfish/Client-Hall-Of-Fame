package rip.athena.client.utils;

import java.text.*;

public class NumberUtils
{
    public static String getStringValueOfLong(final long value, final Format format) {
        return String.valueOf(new DecimalFormat(getDecimalFormat(format)).format(value));
    }
    
    public static String getStringValueOfFloat(final float value, final Format format) {
        return String.valueOf(new DecimalFormat(getDecimalFormat(format)).format(value));
    }
    
    public static String getDecimalFormat(final Format decimalFormat) {
        if (decimalFormat == Format.Ones) {
            return "#";
        }
        if (decimalFormat == Format.Tenths) {
            return "#.#";
        }
        if (decimalFormat == Format.Hundredths) {
            return "#.##";
        }
        return null;
    }
    
    public enum Format
    {
        Ones, 
        Tenths, 
        Hundredths;
    }
}
