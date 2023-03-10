/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import lodomir.dev.event.impl.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.BooleanSetting;
import lodomir.dev.settings.ModeSetting;
import lodomir.dev.settings.NumberSetting;

public class HUD
extends Module {
    public static ModeSetting mode = new ModeSetting("Mode", "Plain", "Plain", "Exhibition", "Astolfo", "None", "November");
    public static NumberSetting red = new NumberSetting("Red", 0.0, 255.0, 197.0, 1.0);
    public static NumberSetting green = new NumberSetting("Green", 0.0, 255.0, 16.0, 1.0);
    public static NumberSetting blue = new NumberSetting("Blue", 0.0, 255.0, 16.0, 1.0);
    public static ModeSetting font = new ModeSetting("Font", "Client", "Minecraft", "Client");
    public static ModeSetting color = new ModeSetting("Color", "Dynamic", "Static", "Dynamic", "Astolfo", "Rainbow");
    public static BooleanSetting bps = new BooleanSetting("BPS", true);
    public static BooleanSetting fps = new BooleanSetting("FPS", false);
    public static BooleanSetting clock = new BooleanSetting("Clock", false);
    public static BooleanSetting ping = new BooleanSetting("Ping", false);
    public static BooleanSetting info = new BooleanSetting("Client Info", true);
    public static BooleanSetting effects = new BooleanSetting("Effects", true);
    public static BooleanSetting border = new BooleanSetting("Background", false);
    public static BooleanSetting outline = new BooleanSetting("Outline", false);
    public static BooleanSetting sidebar = new BooleanSetting("Sidebar", false);
    public static BooleanSetting chatfont = new BooleanSetting("Chat Font", true);
    public static BooleanSetting nochatbg = new BooleanSetting("Transparent Chat", true);
    public static int customcolor = new Color(red.getValueInt(), green.getValueInt(), blue.getValueInt()).getRGB();

    public HUD() {
        super("HUD", 0, Category.RENDER);
        this.addSettings(mode, red, green, blue, color, bps, fps, clock, ping, info, effects, border, sidebar, outline, font, nochatbg, chatfont);
    }

    public static int getColor() {
        return customcolor;
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (!color.isMode("Static") && !color.isMode("Dynamic")) {
            red.setVisible(false);
            green.setVisible(false);
            blue.setVisible(false);
        } else {
            red.setVisible(true);
            green.setVisible(true);
            blue.setVisible(true);
        }
        super.onUpdate(event);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}

