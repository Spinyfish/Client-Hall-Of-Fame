package net.optifine.util;

import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FontUtils {
    public static Properties readFontProperties(final ResourceLocation locationFontTexture) {
        final String s = locationFontTexture.getResourcePath();
        final Properties properties = new PropertiesOrdered();
        final String s1 = ".png";

        if (!s.endsWith(s1)) {
            return properties;
        } else {
            final String s2 = s.substring(0, s.length() - s1.length()) + ".properties";

            try {
                final ResourceLocation resourcelocation = new ResourceLocation(locationFontTexture.getResourceDomain(), s2);
                final InputStream inputstream = Config.getResourceStream(Config.getResourceManager(), resourcelocation);

                if (inputstream == null) {
                    return properties;
                }

                Config.log("Loading " + s2);
                properties.load(inputstream);
            } catch (final FileNotFoundException var7) {
            } catch (final IOException ioexception) {
                ioexception.printStackTrace();
            }

            return properties;
        }
    }

    public static void readCustomCharWidths(final Properties props, final float[] charWidth) {
        for (final Object e : props.keySet()) {
            final String s = (String) e;
            final String s1 = "width.";

            if (s.startsWith(s1)) {
                final String s2 = s.substring(s1.length());
                final int i = Config.parseInt(s2, -1);

                if (i >= 0 && i < charWidth.length) {
                    final String s3 = props.getProperty(s);
                    final float f = Config.parseFloat(s3, -1.0F);

                    if (f >= 0.0F) {
                        charWidth[i] = f;
                    }
                }
            }
        }
    }

    public static float readFloat(final Properties props, final String key, final float defOffset) {
        final String s = props.getProperty(key);

        if (s == null) {
            return defOffset;
        } else {
            final float f = Config.parseFloat(s, Float.MIN_VALUE);

            if (f == Float.MIN_VALUE) {
                Config.warn("Invalid value for " + key + ": " + s);
                return defOffset;
            } else {
                return f;
            }
        }
    }

    public static boolean readBoolean(final Properties props, final String key, final boolean defVal) {
        final String s = props.getProperty(key);

        if (s == null) {
            return defVal;
        } else {
            final String s1 = s.toLowerCase().trim();

            if (!s1.equals("true") && !s1.equals("on")) {
                if (!s1.equals("false") && !s1.equals("off")) {
                    Config.warn("Invalid value for " + key + ": " + s);
                    return defVal;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    public static ResourceLocation getHdFontLocation(final ResourceLocation fontLoc) {
        if (!Config.isCustomFonts()) {
            return fontLoc;
        } else if (fontLoc == null) {
            return fontLoc;
        } else if (!Config.isMinecraftThread()) {
            return fontLoc;
        } else {
            String s = fontLoc.getResourcePath();
            final String s1 = "textures/";
            final String s2 = "mcpatcher/";

            if (!s.startsWith(s1)) {
                return fontLoc;
            } else {
                s = s.substring(s1.length());
                s = s2 + s;
                final ResourceLocation resourcelocation = new ResourceLocation(fontLoc.getResourceDomain(), s);
                return Config.hasResource(Config.getResourceManager(), resourcelocation) ? resourcelocation : fontLoc;
            }
        }
    }
}
