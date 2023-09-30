package dev.tenacity.utils.misc;

public class RomanNumeralUtils {

    private static final int MIN_VALUE = 1, MAX_VALUE = 3999;
    private static final String[] M = {"", "M", "MM", "MMM", "MMMM"},
            C = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"},
            X = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"},
            I = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

    public static String generate(int number) {
        if (number < MIN_VALUE || number > MAX_VALUE) {
            throw new IllegalArgumentException(
                    String.format("The number must be in the range [%d, %d]", MIN_VALUE, MAX_VALUE));
        }
        return M[number / 1000] + C[number % 1000 / 100] + X[number % 100 / 10] + I[number % 10];
    }

}
