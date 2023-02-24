package me.kansio.client.rank;

import lombok.Getter;
import net.minecraft.util.EnumChatFormatting;

@Getter
public enum UserRank {

    DEVELOPER("Developer", EnumChatFormatting.GOLD),
    BETA("Beta", EnumChatFormatting.LIGHT_PURPLE),
    USER("Normal", EnumChatFormatting.GRAY);

    private String displayName;
    private EnumChatFormatting color;

    UserRank(String name, EnumChatFormatting color) {
        this.displayName = name;
        this.color = color;
    }
}
