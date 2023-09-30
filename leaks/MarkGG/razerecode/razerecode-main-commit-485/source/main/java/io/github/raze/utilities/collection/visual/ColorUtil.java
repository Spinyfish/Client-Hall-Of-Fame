/*
 * Copyright (c) 2023. Syncinus / Liticane
 * You cannot redistribute these files
 * without my written permission.
 */

package io.github.raze.utilities.collection.visual;

import java.awt.*;

public class ColorUtil {

    public static Color mix(Color first, Color second) {
        return new Color(
                (first.getRed() + second.getRed()) / 2,
                (first.getGreen() + second.getGreen()) / 2,
                (first.getBlue() + second.getBlue()) / 2,
                (first.getAlpha() + second.getAlpha()) / 2
        );
    }

    public static Color getRainbow(float seconds, float saturation, float brightness, long index) {
        float values = ((System.currentTimeMillis() + index) % (int) (seconds * 1000)) / (seconds * 1000);
        return new Color(Color.HSBtoRGB(values, saturation, brightness));
    }

    public static Color getRainbow(float seconds, float saturation, float brightness) {
        return getRainbow(seconds, saturation, brightness, 0);
    }

}
