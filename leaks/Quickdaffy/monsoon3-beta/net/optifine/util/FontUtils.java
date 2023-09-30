/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.PropertiesOrdered;

public class FontUtils {
    public static Properties readFontProperties(ResourceLocation locationFontTexture) {
        String s = locationFontTexture.getResourcePath();
        PropertiesOrdered properties = new PropertiesOrdered();
        String s1 = ".png";
        if (!s.endsWith(s1)) {
            return properties;
        }
        String s2 = s.substring(0, s.length() - s1.length()) + ".properties";
        try {
            ResourceLocation resourcelocation = new ResourceLocation(locationFontTexture.getResourceDomain(), s2);
            InputStream inputstream = Config.getResourceStream(Config.getResourceManager(), resourcelocation);
            if (inputstream == null) {
                return properties;
            }
            Config.log("Loading " + s2);
            properties.load(inputstream);
        }
        catch (FileNotFoundException resourcelocation) {
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        return properties;
    }

    public static void readCustomCharWidths(Properties props, float[] charWidth) {
        for (Object e : props.keySet()) {
            String s3;
            float f;
            String s2;
            int i;
            String s1;
            String s = (String)e;
            if (!s.startsWith(s1 = "width.") || (i = Config.parseInt(s2 = s.substring(s1.length()), -1)) < 0 || i >= charWidth.length || !((f = Config.parseFloat(s3 = props.getProperty(s), -1.0f)) >= 0.0f)) continue;
            charWidth[i] = f;
        }
    }

    public static float readFloat(Properties props, String key, float defOffset) {
        String s = props.getProperty(key);
        if (s == null) {
            return defOffset;
        }
        float f = Config.parseFloat(s, Float.MIN_VALUE);
        if (f == Float.MIN_VALUE) {
            Config.warn("Invalid value for " + key + ": " + s);
            return defOffset;
        }
        return f;
    }

    public static boolean readBoolean(Properties props, String key, boolean defVal) {
        String s = props.getProperty(key);
        if (s == null) {
            return defVal;
        }
        String s1 = s.toLowerCase().trim();
        if (!s1.equals("true") && !s1.equals("on")) {
            if (!s1.equals("false") && !s1.equals("off")) {
                Config.warn("Invalid value for " + key + ": " + s);
                return defVal;
            }
            return false;
        }
        return true;
    }

    public static ResourceLocation getHdFontLocation(ResourceLocation fontLoc) {
        if (!Config.isCustomFonts()) {
            return fontLoc;
        }
        if (fontLoc == null) {
            return fontLoc;
        }
        if (!Config.isMinecraftThread()) {
            return fontLoc;
        }
        String s = fontLoc.getResourcePath();
        String s1 = "textures/";
        String s2 = "mcpatcher/";
        if (!s.startsWith(s1)) {
            return fontLoc;
        }
        s = s.substring(s1.length());
        s = s2 + s;
        ResourceLocation resourcelocation = new ResourceLocation(fontLoc.getResourceDomain(), s);
        return Config.hasResource(Config.getResourceManager(), resourcelocation) ? resourcelocation : fontLoc;
    }
}

