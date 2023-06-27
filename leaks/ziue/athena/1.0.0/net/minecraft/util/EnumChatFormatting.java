package net.minecraft.util;

import java.util.regex.*;
import java.util.*;
import com.google.common.collect.*;

public enum EnumChatFormatting
{
    BLACK("BLACK", '0', 0), 
    DARK_BLUE("DARK_BLUE", '1', 1), 
    DARK_GREEN("DARK_GREEN", '2', 2), 
    DARK_AQUA("DARK_AQUA", '3', 3), 
    DARK_RED("DARK_RED", '4', 4), 
    DARK_PURPLE("DARK_PURPLE", '5', 5), 
    GOLD("GOLD", '6', 6), 
    GRAY("GRAY", '7', 7), 
    DARK_GRAY("DARK_GRAY", '8', 8), 
    BLUE("BLUE", '9', 9), 
    GREEN("GREEN", 'a', 10), 
    AQUA("AQUA", 'b', 11), 
    RED("RED", 'c', 12), 
    LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13), 
    YELLOW("YELLOW", 'e', 14), 
    WHITE("WHITE", 'f', 15), 
    OBFUSCATED("OBFUSCATED", 'k', true), 
    BOLD("BOLD", 'l', true), 
    STRIKETHROUGH("STRIKETHROUGH", 'm', true), 
    UNDERLINE("UNDERLINE", 'n', true), 
    ITALIC("ITALIC", 'o', true), 
    RESET("RESET", 'r', -1);
    
    private static final Map<String, EnumChatFormatting> nameMapping;
    private static final Pattern formattingCodePattern;
    private final String name;
    private final char formattingCode;
    private final boolean fancyStyling;
    private final String controlString;
    private final int colorIndex;
    
    private static String func_175745_c(final String p_175745_0_) {
        return p_175745_0_.toLowerCase().replaceAll("[^a-z]", "");
    }
    
    private EnumChatFormatting(final String formattingName, final char formattingCodeIn, final int colorIndex) {
        this(formattingName, formattingCodeIn, false, colorIndex);
    }
    
    private EnumChatFormatting(final String formattingName, final char formattingCodeIn, final boolean fancyStylingIn) {
        this(formattingName, formattingCodeIn, fancyStylingIn, -1);
    }
    
    private EnumChatFormatting(final String formattingName, final char formattingCodeIn, final boolean fancyStylingIn, final int colorIndex) {
        this.name = formattingName;
        this.formattingCode = formattingCodeIn;
        this.fancyStyling = fancyStylingIn;
        this.colorIndex = colorIndex;
        this.controlString = "§" + formattingCodeIn;
    }
    
    public int getColorIndex() {
        return this.colorIndex;
    }
    
    public boolean isFancyStyling() {
        return this.fancyStyling;
    }
    
    public boolean isColor() {
        return !this.fancyStyling && this != EnumChatFormatting.RESET;
    }
    
    public String getFriendlyName() {
        return this.name().toLowerCase();
    }
    
    @Override
    public String toString() {
        return this.controlString;
    }
    
    public static String getTextWithoutFormattingCodes(final String text) {
        return (text == null) ? null : EnumChatFormatting.formattingCodePattern.matcher(text).replaceAll("");
    }
    
    public static EnumChatFormatting getValueByName(final String friendlyName) {
        return (friendlyName == null) ? null : EnumChatFormatting.nameMapping.get(func_175745_c(friendlyName));
    }
    
    public static EnumChatFormatting func_175744_a(final int p_175744_0_) {
        if (p_175744_0_ < 0) {
            return EnumChatFormatting.RESET;
        }
        for (final EnumChatFormatting enumchatformatting : values()) {
            if (enumchatformatting.getColorIndex() == p_175744_0_) {
                return enumchatformatting;
            }
        }
        return null;
    }
    
    public static Collection<String> getValidValues(final boolean p_96296_0_, final boolean p_96296_1_) {
        final List<String> list = (List<String>)Lists.newArrayList();
        for (final EnumChatFormatting enumchatformatting : values()) {
            if ((!enumchatformatting.isColor() || p_96296_0_) && (!enumchatformatting.isFancyStyling() || p_96296_1_)) {
                list.add(enumchatformatting.getFriendlyName());
            }
        }
        return list;
    }
    
    static {
        nameMapping = Maps.newHashMap();
        formattingCodePattern = Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-OR]");
        for (final EnumChatFormatting enumchatformatting : values()) {
            EnumChatFormatting.nameMapping.put(func_175745_c(enumchatformatting.name), enumchatformatting);
        }
    }
}
