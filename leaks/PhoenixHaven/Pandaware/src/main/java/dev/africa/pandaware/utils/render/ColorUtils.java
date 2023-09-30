package dev.africa.pandaware.utils.render;

import dev.africa.pandaware.utils.math.apache.ApacheMath;
import lombok.experimental.UtilityClass;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.regex.Pattern;

@UtilityClass
public class ColorUtils {
    public Color getColorAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public Color rainbow(int delay, float saturation, double speed) {
        double rainbowState = ApacheMath.ceil(((System.currentTimeMillis() * speed) - delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), saturation, 1f);
    }

    public Color getColorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed) {
        long now = (long) (speed * System.currentTimeMillis() + -index * timePerIndex);

        float rd = (firstColor.getRed() - secondColor.getRed()) / time;
        float gd = (firstColor.getGreen() - secondColor.getGreen()) / time;
        float bd = (firstColor.getBlue() - secondColor.getBlue()) / time;

        float rd2 = (secondColor.getRed() - firstColor.getRed()) / time;
        float gd2 = (secondColor.getGreen() - firstColor.getGreen()) / time;
        float bd2 = (secondColor.getBlue() - firstColor.getBlue()) / time;

        int re1 = ApacheMath.round(secondColor.getRed() + rd * (now % (long) time));
        int ge1 = ApacheMath.round(secondColor.getGreen() + gd * (now % (long) time));
        int be1 = ApacheMath.round(secondColor.getBlue() + bd * (now % (long) time));
        int re2 = ApacheMath.round(firstColor.getRed() + rd2 * (now % (long) time));
        int ge2 = ApacheMath.round(firstColor.getGreen() + gd2 * (now % (long) time));
        int be2 = ApacheMath.round(firstColor.getBlue() + bd2 * (now % (long) time));

        if (now % ((long) time * 2L) < (long) time) {
            return new Color(getColor(255, re2, ge2, be2));
        } else {
            return new Color(getColor(255, re1, ge1, be1));
        }
    }

    public int getColor(int A, int R, int G, int B) {
        return (A & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
    }

    public void glColor(Color color) {
        float red = color.getRed() / 255F;
        float green = color.getGreen() / 255F;
        float blue = color.getBlue() / 255F;
        float alpha = color.getAlpha() / 255F;

        GL11.glColor4f(red, green, blue, alpha);
    }

    public String stripColorCodes(String input) {
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }
    private final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORX]");
}
