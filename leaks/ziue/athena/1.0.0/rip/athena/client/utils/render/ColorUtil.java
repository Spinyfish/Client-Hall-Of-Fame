package rip.athena.client.utils.render;

import org.lwjgl.opengl.*;
import java.awt.*;
import rip.athena.client.theme.impl.*;
import rip.athena.client.*;
import rip.athena.client.utils.*;

public interface ColorUtil
{
    default void glColor(final int hex) {
        final float a = (hex >> 24 & 0xFF) / 255.0f;
        final float r = (hex >> 16 & 0xFF) / 255.0f;
        final float g = (hex >> 8 & 0xFF) / 255.0f;
        final float b = (hex & 0xFF) / 255.0f;
        GL11.glColor4f(r, g, b, a);
    }
    
    default Color getClientColor(final int index, final int alpha) {
        for (final AccentTheme c : AccentTheme.values()) {
            if (c.equals(Athena.INSTANCE.getThemeManager().getTheme())) {
                return interpolateColorsBackAndForth(15, index, new Color(c.getFirstColor().getRed(), c.getFirstColor().getGreen(), c.getFirstColor().getBlue(), alpha), new Color(c.getSecondColor().getRed(), c.getSecondColor().getGreen(), c.getSecondColor().getBlue(), alpha), false);
            }
        }
        return interpolateColorsBackAndForth(15, index, new Color(234, 107, 149, alpha), new Color(238, 164, 123, alpha), false);
    }
    
    default Color interpolateColorsBackAndForth(final int speed, final int index, final Color start, final Color end, final boolean trueColor) {
        int angle = (int)((System.currentTimeMillis() / speed + index) % 360L);
        angle = ((angle >= 180) ? (360 - angle) : angle) * 2;
        return trueColor ? interpolateColorHue(start, end, angle / 360.0f) : getInterpolateColor(start, end, angle / 360.0f);
    }
    
    default Color getInterpolateColor(final Color color1, final Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount), interpolateInt(color1.getGreen(), color2.getGreen(), amount), interpolateInt(color1.getBlue(), color2.getBlue(), amount), interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }
    
    default Color interpolateColorHue(final Color color1, final Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        final float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        final float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
        final Color resultColor = Color.getHSBColor(interpolateFloat(color1HSB[0], color2HSB[0], amount), interpolateFloat(color1HSB[1], color2HSB[1], amount), interpolateFloat(color1HSB[2], color2HSB[2], amount));
        return new Color(resultColor.getRed(), resultColor.getGreen(), resultColor.getBlue(), interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }
    
    default Color interpolateColorC(final Color color1, final Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount), interpolateInt(color1.getGreen(), color2.getGreen(), amount), interpolateInt(color1.getBlue(), color2.getBlue(), amount), interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }
    
    default float interpolateFloat(final float oldValue, final float newValue, final double interpolationValue) {
        return interpolate(oldValue, newValue, (float)interpolationValue).floatValue();
    }
    
    default int interpolateInt(final int oldValue, final int newValue, final double interpolationValue) {
        return interpolate(oldValue, newValue, (float)interpolationValue).intValue();
    }
    
    default Double interpolate(final double oldValue, final double newValue, final double interpolationValue) {
        return oldValue + (newValue - oldValue) * interpolationValue;
    }
    
    default Color applyOpacity(final Color color, float opacity) {
        opacity = Math.min(1.0f, Math.max(0.0f, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * opacity));
    }
    
    default int applyOpacity(final int color, final float opacity) {
        final Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }
    
    default Color tripleColor(final int rgbValue) {
        return tripleColor(rgbValue, 1.0f);
    }
    
    default Color tripleColor(final int rgbValue, float alpha) {
        alpha = Math.min(1.0f, Math.max(0.0f, alpha));
        return new Color(rgbValue, rgbValue, rgbValue, (int)(255.0f * alpha));
    }
    
    default void glColor(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }
    
    default Color darker(final Color color, final float factor) {
        return new Color(Math.max((int)(color.getRed() * factor), 0), Math.max((int)(color.getGreen() * factor), 0), Math.max((int)(color.getBlue() * factor), 0), color.getAlpha());
    }
    
    default Color brighter(final Color color, final float factor) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        final int alpha = color.getAlpha();
        final int i = (int)(1.0f / (1.0f - factor));
        if (red == 0 && green == 0 && blue == 0) {
            return new Color(i, i, i, alpha);
        }
        if (red > 0 && red < i) {
            red = i;
        }
        if (green > 0 && green < i) {
            green = i;
        }
        if (blue > 0 && blue < i) {
            blue = i;
        }
        return new Color(Math.min((int)(red / factor), 255), Math.min((int)(green / factor), 255), Math.min((int)(blue / factor), 255), alpha);
    }
    
    default Color withRed(final Color color, final int red) {
        return new Color(red, color.getGreen(), color.getBlue());
    }
    
    default Color withGreen(final Color color, final int green) {
        return new Color(color.getRed(), green, color.getBlue());
    }
    
    default Color withBlue(final Color color, final int blue) {
        return new Color(color.getRed(), color.getGreen(), blue);
    }
    
    default Color withAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)MathUtil.clamp(0.0, 255.0, alpha));
    }
    
    default Color mixColors(final Color color1, final Color color2, final double percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int)(color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int)(color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int)(color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }
}
