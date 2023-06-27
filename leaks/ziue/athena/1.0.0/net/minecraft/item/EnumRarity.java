package net.minecraft.item;

import net.minecraft.util.*;

public enum EnumRarity
{
    COMMON(EnumChatFormatting.WHITE, "Common"), 
    UNCOMMON(EnumChatFormatting.YELLOW, "Uncommon"), 
    RARE(EnumChatFormatting.AQUA, "Rare"), 
    EPIC(EnumChatFormatting.LIGHT_PURPLE, "Epic");
    
    public final EnumChatFormatting rarityColor;
    public final String rarityName;
    
    private EnumRarity(final EnumChatFormatting color, final String name) {
        this.rarityColor = color;
        this.rarityName = name;
    }
}
