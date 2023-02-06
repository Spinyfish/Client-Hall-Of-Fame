package dev.elysium.base.mods.settings;

import dev.elysium.base.mods.Mod;
import dev.elysium.client.utils.render.BColor;

public class ColorSetting extends Setting{

    public BColor color;

    public ColorSetting(String name, int color, Mod parent) {
        super(name, parent);
        this.color = new BColor(color);
    }
}
