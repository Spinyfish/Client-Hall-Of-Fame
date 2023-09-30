package me.jinthium.straight.impl.modules.visual;

import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.settings.ColorSetting;
import me.jinthium.straight.impl.settings.ModeSetting;

import java.awt.*;

public class Glint extends Module {

    public final ModeSetting colorMode = new ModeSetting("Color Mode", "Sync", "Sync", "Custom");
    public final ColorSetting color = new ColorSetting("Color", Color.PINK);

    public Glint() {
        super("Glint", Category.VISUALS);
        color.addParent(colorMode, modeSetting -> modeSetting.is("Custom"));
        addSettings(colorMode, color);
    }

    public Color getColor() {
        Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
        Color customColor = Color.WHITE;
        switch (colorMode.getMode()) {
            case "Sync" -> {
               customColor = hud.getHudColor((float) System.currentTimeMillis() / 600);
            }
            case "Custom" -> customColor = color.getColor();
        }
        return customColor;
    }

}
