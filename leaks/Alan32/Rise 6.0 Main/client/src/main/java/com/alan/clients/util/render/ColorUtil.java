package com.alan.clients.util.render;

import com.alan.clients.util.math.MathUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public interface ColorUtil {

    /**
     * Method which colors using a hex code
     *
     * @param hex used hex code
     */
    static void glColor(final int hex) {
        final float a = (hex >> 24 & 0xFF) / 255.0F;
        final float r = (hex >> 16 & 0xFF) / 255.0F;
        final float g = (hex >> 8 & 0xFF) / 255.0F;
        final float b = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, a);
    }

    /**
     * Method which colors using a color
     *
     * @param color used color
     */
    static void glColor(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
    }

    static Color darker(final Color color, final float factor) {
        return new Color(Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0),
                color.getAlpha());
    }

    static Color brighter(final Color color, final float factor) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        final int alpha = color.getAlpha();

        final int i = (int) (1 / (1 - factor));
        if (red == 0 && green == 0 && blue == 0) {
            return new Color(i, i, i, alpha);
        }

        if (red > 0 && red < i) red = i;
        if (green > 0 && green < i) green = i;
        if (blue > 0 && blue < i) blue = i;

        return new Color(Math.min((int) (red / factor), 255),
                Math.min((int) (green / factor), 255),
                Math.min((int) (blue / factor), 255),
                alpha);
    }

    static Color withRed(final Color color, final int red) {
        return new Color(red, color.getGreen(), color.getBlue());
    }

    static Color withGreen(final Color color, final int green) {
        return new Color(color.getRed(), green, color.getBlue());
    }

    static Color withBlue(final Color color, final int blue) {
        return new Color(color.getRed(), color.getGreen(), blue);
    }

    static Color withAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) MathUtil.clamp(0, 255, alpha));
    }

    static Color mixColors(final Color color1, final Color color2, final double percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }
}
