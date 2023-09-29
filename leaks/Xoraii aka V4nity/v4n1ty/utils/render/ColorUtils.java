package v4n1ty.utils.render;


import net.minecraft.entity.EntityLivingBase;
import v4n1ty.utils.math.MathUtils;

import javax.vecmath.Vector2d;
import java.awt.*;
import java.awt.image.BufferedImage;
import static v4n1ty.utils.math.MathUtils.*;

public class ColorUtils {

    public static Color getColorAlpha(Color color, int alpha) {
        return getColorAlpha(color.getRGB(), alpha);
    }

    public static Color getColorAlpha(int color, int alpha) {
        Color color2 = new Color(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(),
                alpha);
        return color2;
    }

    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int) (opacity * 255))));
    }

    public static Color averageColor(BufferedImage bi, int width, int height, int pixelStep) {

        int[] color = new int[3];
        for (int x = 0; x < width; x += pixelStep) {
            for (int y = 0; y < height; y += pixelStep) {
                Color pixel = new Color(bi.getRGB(x, y));
                color[0] += pixel.getRed();
                color[1] += pixel.getGreen();
                color[2] += pixel.getBlue();
            }
        }
        int num = width * height;
        return new Color(color[0] / num, color[1] / num, color[2] / num);
    }

    public static int getHealthColor(final EntityLivingBase player) {
        final float f = player.getHealth();
        final float f2 = player.getMaxHealth();
        final float f3 = Math.max(0.0f, Math.min(f, f2) / f2);
        return Color.HSBtoRGB(f3 / 3.0f, 1.0f, 0.75f) | 0xFF000000;
    }

    public static Color tripleColor(int rgbValue) {
        return tripleColor(rgbValue, 1);
    }

    public static Color brighter(Color color, float FACTOR) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int) (1.0 / (1.0 - FACTOR));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return new Color(Math.min((int) (r / FACTOR), 255),
                Math.min((int) (g / FACTOR), 255),
                Math.min((int) (b / FACTOR), 255),
                alpha);
    }

    public static Color tripleColor(int rgbValue, float alpha) {
        alpha = Math.min(1, Math.max(0, alpha));
        return new Color(rgbValue, rgbValue, rgbValue, (int) (255 * alpha));
    }


    public static Color withAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) clamp(0, 255, alpha));
    }

    public static int interpolateColor(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return interpolateColorC(color1, color2, amount).getRGB();
    }

    public static int interpolateColor(int color1, int color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        Color cColor1 = new Color(color1);
        Color cColor2 = new Color(color2);
        return interpolateColorC(cColor1, cColor2, amount).getRGB();
    }

    public static int getOppositeColor(int color) {
        int r = bitChangeColor(color, 0);
        int g = bitChangeColor(color, 8);
        int b = bitChangeColor(color, 16);
        int a = bitChangeColor(color, 24);
        r = 255 - r;
        g = 255 - g;
        b = 255 - b;
        return r + (g << 8) + (b << 16) + (a << 24);
    }

    private static int bitChangeColor(int color, int bitChange) {
        return (color >> bitChange) & 255;
    }

    public static Color getOppositeColor(Color color) {
        return new Color(getOppositeColor(color.getRGB()));
    }

    public static Color blend(Color first, Color second, Vector2d screenCoordinates) {
        return ColorUtils.mixColors(first, second, getBlendFactor(screenCoordinates));
    }

    public static double getBlendFactor(Vector2d screenCoordinates) {
        return Math.sin(System.currentTimeMillis() / 600.0D
                + screenCoordinates.getX() * 0.005D
                + screenCoordinates.getY() * 0.06D
        ) * 0.5D + 0.5D;
    }

    public static Color mixColors(final Color color1, final Color color2, final double percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
    }

    public static Color toColor(float[] colors){
        return new Color(colors[0], colors[1], colors[2], colors[3]);
    }

    public static float[] toGLColor(final int color) {
        final float f = (float) (color >> 16 & 255) / 255.0F;
        final float f1 = (float) (color >> 8 & 255) / 255.0F;
        final float f2 = (float) (color & 255) / 255.0F;
        final float f3 = (float) (color >> 24 & 255) / 255.0F;
        return new float[]{f, f1, f2, f3};
    }

    public static Color colorSwitch(
            Color firstColor,
            Color secondColor,
            float index,
            double speed
    ) {
        long now = (long) (speed * System.currentTimeMillis() + -index * 10L);

        float time = 85.0f;

        float rd = (firstColor.getRed() - secondColor.getRed()) / time;
        float gd = (firstColor.getGreen() - secondColor.getGreen()) / time;
        float bd = (firstColor.getBlue() - secondColor.getBlue()) / time;

        float rd2 = (secondColor.getRed() - firstColor.getRed()) / time;
        float gd2 = (secondColor.getGreen() - firstColor.getGreen()) / time;
        float bd2 = (secondColor.getBlue() - firstColor.getBlue()) / time;

        int re1 = Math.round(secondColor.getRed() + rd * (now % (long) time));
        int ge1 = Math.round(secondColor.getGreen() + gd * (now % (long) time));
        int be1 = Math.round(secondColor.getBlue() + bd * (now % (long) time));
        int re2 = Math.round(firstColor.getRed() + rd2 * (now % (long) time));
        int ge2 = Math.round(firstColor.getGreen() + gd2 * (now % (long) time));
        int be2 = Math.round(firstColor.getBlue() + bd2 * (now % (long) time));

        if (now % ((long) time * 2L) < (long) time) {
            return new Color(re2, ge2, be2);
        } else {
            return new Color(re1, ge1, be1);
        }
    }

    public static int fadeBetween(int startColour, int endColour, double progress) {
        if (progress > 1) progress = 1 - progress % 1;
        return fadeTo(startColour, endColour, progress);
    }

    public static int fadeTo(int startColour, int endColour, double progress) {
        double invert = 1.0 - progress;
        int r = (int) ((startColour >> 16 & 0xFF) * invert +
                (endColour >> 16 & 0xFF) * progress);
        int g = (int) ((startColour >> 8 & 0xFF) * invert +
                (endColour >> 8 & 0xFF) * progress);
        int b = (int) ((startColour & 0xFF) * invert +
                (endColour & 0xFF) * progress);
        int a = (int) ((startColour >> 24 & 0xFF) * invert +
                (endColour >> 24 & 0xFF) * progress);
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                (b & 0xFF);
    }

    public static int fadeBetween(int startColour, int endColour, long offset) {
        return fadeBetween(startColour, endColour, ((System.currentTimeMillis() + offset) % 2000L) / 1000.0);
    }

    public static Color fade(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0f + (float)index / (float)count * 2.0f) % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;
        hsb[2] = brightness % 2.0f;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount),
                interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }


    public static Color fade(Color color, float minFactor, float factor) {
        float r = (float) color.getRed();
        float g = (float) color.getGreen();
        float b = (float) color.getBlue();

        return new Color(
                (int) Math.max(r * factor, r * minFactor),
                (int) Math.max(g * factor, g * minFactor),
                (int) Math.max(b * factor, b * minFactor)
        );
    }
}