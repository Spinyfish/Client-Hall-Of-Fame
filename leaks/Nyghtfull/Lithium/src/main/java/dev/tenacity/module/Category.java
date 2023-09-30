package dev.tenacity.module;

import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.objects.Drag;
import dev.tenacity.utils.objects.Scroll;
import lombok.Getter;

public enum Category {

    COMBAT("Combat", FontUtil.BOMB),
    MOVEMENT("Movement", FontUtil.WHEELCHAIR),
    RENDER("Render", FontUtil.EYE),
    PLAYER("Player", FontUtil.PERSON),
    EXPLOIT("Exploit", FontUtil.BUG),
    MISC("Misc", FontUtil.LIST),
    SCRIPTS("Scripts", FontUtil.SCRIPT);

    public final String name;
    public final String icon;
    public final int posX;
    public final boolean expanded;

    @Getter
    private final Scroll scroll = new Scroll();

    @Getter
    private final Drag drag;
    public int posY = 20;

    Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
        posX = 20 + (Module.categoryCount * 154);
        drag = new Drag(posX, posY);
        expanded = true;
        Module.categoryCount++;
    }

}
