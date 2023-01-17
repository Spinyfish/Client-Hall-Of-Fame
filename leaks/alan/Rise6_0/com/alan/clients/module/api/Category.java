package com.alan.clients.module.api;

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
    //    HOME("Home", 0x0, new HomeScreen()),
    SEARCH("category.search", FontManager.getIconsThree(17), "U", 0x1, new SearchScreen()),
    COMBAT("category.combat", FontManager.getIconsOne(17), "a", 0x2, new CategoryScreen()),
    MOVEMENT("category.movement", FontManager.getIconsOne(17), "b", 0x3, new CategoryScreen()),
    PLAYER("category.player", FontManager.getIconsOne(17), "c", 0x4, new CategoryScreen()),
    RENDER("category.render", FontManager.getIconsOne(17), "g", 0x5, new CategoryScreen()),
    EXPLOIT("category.exploit", FontManager.getIconsOne(17), "a", 0x6, new CategoryScreen()),
    GHOST("category.ghost", FontManager.getIconsOne(17), "f", 0x7, new CategoryScreen()),
    OTHER("category.other", FontManager.getIconsOne(17), "e", 0x8, new CategoryScreen()),
    SCRIPT("category.script", FontManager.getIconsThree(17), "m", 0x7, new CategoryScreen()),
    
    //    INFO("Info", FontManager.getIconsThree(17), "\uE038", 0x9, new InfoScreen()),
    LANGUAGE("category.language", FontManager.getIconsThree(17), "U", 0xA, new LanguageScreen());
//    THEME("Theme", FontManager.getIconsThree(17), "j", 0x9, new ThemeScreen());


//    COMMUNITY("Community", FontManager.getIconsThree(17), "j", 0x9, new CommunityScreen(), false),
//    POSTS("Posts", FontManager.getIconsThree(17), "j", 0x9, new ThemeScreen(), false);

//    LEGIT("Legit", FontManager.getIconsOne(17), "g", 0x7, new CategoryScreen()),
//    FORUM("Cloud", FontManager.getIconsOne(17), "f", 0x7, new ForumScreen());

    // name of category (in case we don't use enum names)
    private final String name;

    // icon character
    private final String icon;

    // optional color for every specific category (module list or click gui)
    private final int color;

    private final Font fontRenderer;

    public final Screen clickGUIScreen;
    public final boolean top;

    Category(final String name, final Font fontRenderer, final String icon, final int color, final Screen clickGUIScreen, boolean top) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.clickGUIScreen = clickGUIScreen;
        this.fontRenderer = fontRenderer;
        this.top = top;
    }

    Category(final String name, final Font fontRenderer, final String icon, final int color, final Screen clickGUIScreen) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.clickGUIScreen = clickGUIScreen;
        this.fontRenderer = fontRenderer;
        this.top = true;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
}