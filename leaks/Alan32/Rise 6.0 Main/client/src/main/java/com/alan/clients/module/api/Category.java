package com.alan.clients.module.api;

import com.alan.clients.Type;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.ui.click.standard.screen.impl.*;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.FontManager;
import lombok.Getter;

/**
 * @author Patrick
 * @since 10/19/2021
 */
@Getter
public enum Category {
    SEARCH("category.search", FontManager.getIconsThree(17), "U", 0x1, new SearchScreen(), Type.BOTH),
    COMBAT("category.combat", FontManager.getIconsOne(17), "a", 0x2, new CategoryScreen(), Type.RISE),
    MOVEMENT("category.movement", FontManager.getIconsOne(17), "b", 0x3, new CategoryScreen(), Type.RISE),
    PLAYER("category.player", FontManager.getIconsOne(17), "c", 0x4, new CategoryScreen(), Type.RISE),
    RENDER("category.render", FontManager.getIconsOne(17), "g", 0x5, new CategoryScreen(), Type.RISE),
    EXPLOIT("category.exploit", FontManager.getIconsOne(17), "a", 0x6, new CategoryScreen(), Type.RISE),
    GHOST("category.ghost", FontManager.getIconsOne(17), "f", 0x7, new CategoryScreen(), Type.RISE),
    OTHER("category.other", FontManager.getIconsOne(17), "e", 0x8, new CategoryScreen(), Type.RISE),
    SCRIPT("category.script", FontManager.getIconsThree(17), "m", 0x7, new CategoryScreen(), Type.RISE),

    THEME("category.themes", FontManager.getIconsThree(17), "U", 0xA, new ThemeScreen(), Type.BOTH),

    LANGUAGE("category.language",FontManager.getIconsThree(17), "U",0xA,new LanguageScreen(), Type.BOTH),

    COMMUNITY("category.irc", FontManager.getIconsThree(17), "j", 0x9, new CommunityScreen(), Type.RISE);

    // name of category (in case we don't use enum names)
    private final String name;

    // icon character
    private final String icon;

    // optional color for every specific category (module list or click gui)
    private final int color;

    private final Font fontRenderer;

    public final Screen clickGUIScreen;
    public final Type clientType;

    Category(final String name, final Font fontRenderer, final String icon, final int color, final Screen clickGUIScreen, Type clientType) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.clickGUIScreen = clickGUIScreen;
        this.fontRenderer = fontRenderer;
        this.clientType = clientType;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
}