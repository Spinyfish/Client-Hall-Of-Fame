/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.render.ColorUtils;

public class Interface
extends Module {
    public static ModeSetting mode = new ModeSetting("Mode", "November", "November", "November Lite", "Rise", "Plain", "Exhibition");
    public static NumberSetting red = new NumberSetting("Red", 0.0, 255.0, 255.0, 1.0);
    public static NumberSetting green = new NumberSetting("Green", 0.0, 255.0, 0.0, 1.0);
    public static NumberSetting blue = new NumberSetting("Blue", 0.0, 255.0, 0.0, 1.0);
    public static NumberSetting red2 = new NumberSetting("Red 2", 0.0, 255.0, 255.0, 1.0);
    public static NumberSetting green2 = new NumberSetting("Green 2", 0.0, 255.0, 120.0, 1.0);
    public static NumberSetting blue2 = new NumberSetting("Blue 2", 0.0, 255.0, 0.0, 1.0);
    public static BooleanSetting watermark = new BooleanSetting("Watermark", true);
    public static NumberSetting size = new NumberSetting("Watermark Size", 18.0, 40.0, 20.0, 2.0);
    public static BooleanSetting norender = new BooleanSetting("Hide Render", true);
    public static ModeSetting color = new ModeSetting("Color", "Blend", "Static", "Dynamic", "Blend", "Rainbow");
    public static ModeSetting font = new ModeSetting("Font", "Client", "Minecraft", "Client");
    public static BooleanSetting bps = new BooleanSetting("BPS", true);
    public static BooleanSetting fps = new BooleanSetting("FPS", false);
    public static BooleanSetting pos = new BooleanSetting("Position", false);
    public static BooleanSetting clock = new BooleanSetting("Clock", false);
    public static BooleanSetting info = new BooleanSetting("Client Info", true);
    public static BooleanSetting effects = new BooleanSetting("Effects", true);
    public static BooleanSetting border = new BooleanSetting("Background", false);
    public static ModeSetting sidebar = new ModeSetting("Sidebar", "None", "None", "Left", "Right", "Outline");
    public static NumberSetting offset = new NumberSetting("Offset", 0.0, 5.0, 1.0, 0.5);
    public static BooleanSetting chatfont = new BooleanSetting("Chat Font", true);
    public static BooleanSetting nochatbg = new BooleanSetting("Transparent Chat", true);

    public Interface() {
        super("Interface", 0, Category.RENDER);
        this.addSettings(mode, color, red, green, blue, red2, green2, blue2, watermark, size, norender, bps, fps, pos, clock, info, effects, border, offset, sidebar, font, nochatbg, chatfont);
    }

    public static int getColorInt() {
        return color.isMode("Blend") ? ColorUtils.getColorSwitch(new Color(red.getValueInt(), green.getValueInt(), blue.getValueInt()), new Color(red2.getValueInt(), green2.getValueInt(), blue2.getValueInt()), 3000.0f, 100, 55L, 4.0).getRGB() : (color.isMode("Rainbow") ? ColorUtils.rainbow(4, 0.7f, 1.0).getRGB() : (color.isMode("Dynamic") ? ColorUtils.fade(new Color(red.getValueInt(), green.getValueInt(), blue.getValueInt()), 100, 100).getRGB() : new Color(red.getValueInt(), green.getValueInt(), blue.getValueInt()).getRGB()));
    }

    public static Color getColor() {
        return color.isMode("Blend") ? ColorUtils.getColorSwitch(new Color(red.getValueInt(), green.getValueInt(), blue.getValueInt()), new Color(red2.getValueInt(), green2.getValueInt(), blue2.getValueInt()), 3000.0f, 100, 55L, 4.0) : (color.isMode("Rainbow") ? ColorUtils.rainbow(4, 0.7f, 1.0) : (color.isMode("Dynamic") ? ColorUtils.fade(new Color(red.getValueInt(), green.getValueInt(), blue.getValueInt()), 100, 100) : new Color(red.getValueInt(), green.getValueInt(), blue.getValueInt())));
    }

    @Override
    @Subscribe
    public void onGuiUpdate(EventUpdate e) {
        if (watermark.isEnabled()) {
            size.setVisible(true);
        } else {
            size.setVisible(false);
        }
        if (!(color.isMode("Static") || color.isMode("Dynamic") || color.isMode("Blend"))) {
            red.setVisible(false);
            green.setVisible(false);
            blue.setVisible(false);
        } else {
            red.setVisible(true);
            green.setVisible(true);
            blue.setVisible(true);
        }
        if (color.isMode("Blend")) {
            red2.setVisible(true);
            green2.setVisible(true);
            blue2.setVisible(true);
        } else {
            red2.setVisible(false);
            green2.setVisible(false);
            blue2.setVisible(false);
        }
        if (!mode.isMode("Rise")) {
            clock.setVisible(true);
            sidebar.setVisible(true);
            border.setVisible(true);
        } else {
            clock.setVisible(false);
            sidebar.setVisible(false);
            border.setVisible(false);
        }
        if (!mode.isMode("November Lite")) {
            norender.setVisible(true);
            watermark.setVisible(true);
            sidebar.setVisible(true);
            border.setVisible(true);
            bps.setVisible(true);
            fps.setVisible(true);
            clock.setVisible(true);
            info.setVisible(true);
            effects.setVisible(true);
        } else {
            norender.setVisible(false);
            border.setVisible(false);
            watermark.setVisible(false);
            sidebar.setVisible(false);
            bps.setVisible(false);
            fps.setVisible(false);
            clock.setVisible(false);
            info.setVisible(false);
            effects.setVisible(false);
        }
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

