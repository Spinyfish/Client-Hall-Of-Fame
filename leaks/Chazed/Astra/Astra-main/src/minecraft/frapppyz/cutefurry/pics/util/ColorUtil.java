package frapppyz.cutefurry.pics.util;

import java.awt.*;

public class ColorUtil {
    public static int getAstolfoColors(int i) {
        float speed = 2900F;
        float hue = (float) ((System.currentTimeMillis() + i) % (int)speed) + (-4 * 7);
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5F - (hue - 0.5f);
        }
        hue -= 0.5F;
        return Color.HSBtoRGB(hue, 0.64F, 1F);
    }

    public static int getAstraColors(int i) {
        float speed = 2900F;
        float hue = (float) ((System.currentTimeMillis() + i) % (int)speed)+12;
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        return Color.HSBtoRGB(hue+3, 0.74F, 1F);
    }
}
