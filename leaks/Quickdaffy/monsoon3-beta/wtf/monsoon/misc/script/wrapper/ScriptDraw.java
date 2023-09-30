/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.misc.script.wrapper;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import wtf.monsoon.Wrapper;

public class ScriptDraw {
    public static void drawRect(double x, double y, double width, double height, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
    }

    public static void drawString(String text, double x, double y, int color) {
        Wrapper.getFont().drawString(text, (float)x, (float)y, new Color(color), false);
    }

    public static void drawStringWithShadow(String text, double x, double y, int color) {
        Wrapper.getFont().drawStringWithShadow(text, (float)x, (float)y, new Color(color));
    }

    public static double getStringWidth(String text) {
        return Wrapper.getFont().getStringWidth(text);
    }

    public static double getFontHeight() {
        return Wrapper.getFont().getHeight();
    }
}

