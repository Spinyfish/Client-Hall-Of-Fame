/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.layout.altMgr;

public class EmailAllowedCharacters {
    public static final char[] allowedCharactersArray = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

    public static boolean isAllowedCharacter(char character) {
        return character >= ' ' && character != '\u007f';
    }

    public static String filterAllowedCharacters(String input) {
        StringBuilder var1 = new StringBuilder();
        char[] var2 = input.toCharArray();
        int var3 = var2.length;
        int var4 = 0;
        while (var4 < var3) {
            char var5 = var2[var4];
            if (EmailAllowedCharacters.isAllowedCharacter(var5)) {
                var1.append(var5);
            }
            ++var4;
        }
        return var1.toString();
    }
}
