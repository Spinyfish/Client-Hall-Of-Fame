package dev.thread.api.util.render;

import dev.thread.api.util.IWrapper;
import dev.thread.api.util.math.MathUtil;
import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public class ColorUtil implements IWrapper {

    public static int interpolateTwoColors(float step, int color1, int color2) {
        return interpolateTwoColors(step, Color.getColor("", color1), Color.getColor("", color2));
    }

    public static int interpolateTwoColors(float step, Color color1, Color color2) {
        step = MathUtil.clamp(step, 0.0f, 1.0f);

        int red = (int) (color1.getRed() + (color2.getRed() - color1.getRed()) * step);
        int green = (int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * step);
        int blue = (int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * step);
        int alpha = (int) (color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * step);

        Color newColor = new Color(red, green, blue, alpha);

        return newColor.getRGB();
    }


}
