/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.utils.render;

import java.awt.Color;

public class ColorUtils {
    public static int randomColor() {
        return -16777216 | (int)(Math.random() * 1.6777215E7);
    }

    public static int transparency(int color, double alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * (float)c.getRed();
        float g = 0.003921569f * (float)c.getGreen();
        float b = 0.003921569f * (float)c.getBlue();
        return new Color(r, g, b, (float)alpha).getRGB();
    }

    public static int transparency(Color color, double alpha) {
        return new Color((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), (float)alpha).getRGB();
    }

    public static Color rainbow(long offset, float fade) {
        float hue = (float)(System.nanoTime() + offset) / 1.0E10f % 1.0f;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
        Color c = new Color((int)color);
        return new Color((float)c.getRed() / 255.0f * fade, (float)c.getGreen() / 255.0f * fade, (float)c.getBlue() / 255.0f * fade, (float)c.getAlpha() / 255.0f);
    }


    
    public static float[] getRGBA(int color) {
        float a = (float)(color >> 24 & 255) / 255.0f;
        float r = (float)(color >> 16 & 255) / 255.0f;
        float g = (float)(color >> 8 & 255) / 255.0f;
        float b = (float)(color & 255) / 255.0f;
        return new float[]{r, g, b, a};
    }

    public static int intFromHex(String hex) {
        try {
            if (hex.equalsIgnoreCase("rainbow")) {
                return ColorUtils.rainbow(0L, 1.0f).getRGB();
            }
            return Integer.parseInt(hex, 16);
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String hexFromInt(int color) {
        return ColorUtils.hexFromInt(new Color(color));
    }

    public static String hexFromInt(Color color) {
        return Integer.toHexString(color.getRGB()).substring(2);
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float)ratio;
        float ir = 1.0f - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        Color color3 = new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir);
        return color3;
    }

    public static Color blend(Color color1, Color color2) {
        return ColorUtils.blend(color1, color2, 0.5);
    }

    public static Color darker(Color color, double fraction) {
        int red = (int)Math.round((double)color.getRed() * (1.0 - fraction));
        int green = (int)Math.round((double)color.getGreen() * (1.0 - fraction));
        int blue = (int)Math.round((double)color.getBlue() * (1.0 - fraction));
        if (red < 0) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }
        int alpha = color.getAlpha();
        return new Color(red, green, blue, alpha);
    }

    public static Color lighter(Color color, double fraction) {
        int red = (int)Math.round((double)color.getRed() * (1.0 + fraction));
        int green = (int)Math.round((double)color.getGreen() * (1.0 + fraction));
        int blue = (int)Math.round((double)color.getBlue() * (1.0 + fraction));
        if (red < 0) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }
        int alpha = color.getAlpha();
        return new Color(red, green, blue, alpha);
    }

    public static String getHexName(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        String rHex = Integer.toString(r, 16);
        String gHex = Integer.toString(g, 16);
        String bHex = Integer.toString(b, 16);
        return String.valueOf(String.valueOf(rHex.length() == 2 ? new StringBuilder().append(rHex).toString() : new StringBuilder("0").append(rHex).toString())) + (gHex.length() == 2 ? new StringBuilder().append(gHex).toString() : new StringBuilder("0").append(gHex).toString()) + (bHex.length() == 2 ? new StringBuilder().append(bHex).toString() : new StringBuilder("0").append(bHex).toString());
    }

    public static double colorDistance(double r1, double g1, double b1, double r2, double g2, double b2) {
        double a = r2 - r1;
        double b3 = g2 - g1;
        double c = b2 - b1;
        return Math.sqrt(a * a + b3 * b3 + c * c);
    }

    public static double colorDistance(double[] color1, double[] color2) {
        return ColorUtils.colorDistance(color1[0], color1[1], color1[2], color2[0], color2[1], color2[2]);
    }

    public static double colorDistance(Color color1, Color color2) {
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        return ColorUtils.colorDistance(rgb1[0], rgb1[1], rgb1[2], rgb2[0], rgb2[1], rgb2[2]);
    }

    public static boolean isDark(double r, double g, double b) {
        double dWhite = ColorUtils.colorDistance(r, g, b, 1.0, 1.0, 1.0);
        double dBlack = ColorUtils.colorDistance(r, g, b, 0.0, 0.0, 0.0);
        if (dBlack < dWhite) {
            return true;
        }
        return false;
    }

    public static boolean isDark(Color color) {
        float r = (float)color.getRed() / 255.0f;
        float g = (float)color.getGreen() / 255.0f;
        float b = (float)color.getBlue() / 255.0f;
        return ColorUtils.isDark(r, g, b);
    }

    public static Color getColorWithOpacity(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color getHealthColor(float health, float maxHealth) {
        float[] fractions = new float[]{0.0f, 0.5f, 1.0f};
        Color[] colors = new Color[]{new Color(108, 0, 0), new Color(255, 51, 0), Color.GREEN};
        float progress = health / maxHealth;
        return ColorUtils.blendColors(fractions, colors, progress).brighter();
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions.length == colors.length) {
            int[] indices = ColorUtils.getFractionIndices(fractions, progress);
            float[] range = new float[]{fractions[indices[0]], fractions[indices[1]]};
            Color[] colorRange = new Color[]{colors[indices[0]], colors[indices[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            Color color = ColorUtils.blend(colorRange[0], colorRange[1], 1.0f - weight);
            return color;
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }
    public static int[] getFractionIndices(float[] fractions, float progress) {
        int startPoint;
        int[] range = new int[2];
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }


    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569F * (float) c.getRed();
        float g = 0.003921569F * (float) c.getGreen();
        float b = 0.003921569F * (float) c.getBlue();
        return (new Color(r, g, b, alpha)).getRGB();
    }
}

