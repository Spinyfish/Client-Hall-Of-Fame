package net.minecraft.util;

public class ChatAllowedCharacters
{
    public static final char[] allowedCharactersArray;
    
    public static boolean isAllowedCharacter(final char character) {
        return character != '§' && character >= ' ' && character != '\u007f';
    }
    
    public static String filterAllowedCharacters(final String input) {
        final StringBuilder stringbuilder = new StringBuilder();
        for (final char c0 : input.toCharArray()) {
            if (isAllowedCharacter(c0)) {
                stringbuilder.append(c0);
            }
        }
        return stringbuilder.toString();
    }
    
    static {
        allowedCharactersArray = new char[] { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
    }
}
