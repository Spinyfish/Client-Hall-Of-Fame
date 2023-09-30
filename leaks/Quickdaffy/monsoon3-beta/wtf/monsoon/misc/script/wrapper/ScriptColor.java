/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.misc.script.wrapper;

import java.awt.Color;
import wtf.monsoon.api.util.render.ColorUtil;

public class ScriptColor {
    public static int getFromRGB(int red, int green, int blue) {
        return new Color(red, green, blue).getRGB();
    }

    public static int getFromRGBA(int red, int green, int blue, int a) {
        return new Color(red, green, blue, a).getRGB();
    }

    public static int getThemeColorForArray(int yOffset, int yTotal) {
        int i = System.currentTimeMillis() % 2L == 0L ? 0 : 1;
        return ColorUtil.getClientAccentTheme(yOffset, yTotal)[i].getRGB();
    }

    public static int getThemeColor() {
        int i = System.currentTimeMillis() % 2L == 0L ? 0 : 1;
        return ColorUtil.getClientAccentTheme()[i].getRGB();
    }
}

