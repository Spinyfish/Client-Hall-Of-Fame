/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.minecraft.util.ResourceLocation;
import optifine.Config;

public class FontUtils {
    public static Properties readFontProperties(ResourceLocation p_readFontProperties_0_) {
        String s = p_readFontProperties_0_.getResourcePath();
        Properties properties = new Properties();
        String s1 = ".png";
        if (!s.endsWith(s1)) {
            return properties;
        }
        String s2 = s.substring(0, s.length() - s1.length()) + ".properties";
        try {
            ResourceLocation resourcelocation = new ResourceLocation(p_readFontProperties_0_.getResourceDomain(), s2);
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

    public static void readCustomCharWidths(Properties p_readCustomCharWidths_0_, float[] p_readCustomCharWidths_1_) {
        for (Object s0 : p_readCustomCharWidths_0_.keySet()) {
            String s3;
            float f;
            String s2;
            int i;
            String s1;
            String s = (String)s0;
            if (!s.startsWith(s1 = "width.") || (i = Config.parseInt(s2 = s.substring(s1.length()), -1)) < 0 || i >= p_readCustomCharWidths_1_.length || !((f = Config.parseFloat(s3 = p_readCustomCharWidths_0_.getProperty(s), -1.0f)) >= 0.0f)) continue;
            p_readCustomCharWidths_1_[i] = f;
        }
    }

    public static float readFloat(Properties p_readFloat_0_, String p_readFloat_1_, float p_readFloat_2_) {
        String s = p_readFloat_0_.getProperty(p_readFloat_1_);
        if (s == null) {
            return p_readFloat_2_;
        }
        float f = Config.parseFloat(s, Float.MIN_VALUE);
        if (f == Float.MIN_VALUE) {
            Config.warn("Invalid value for " + p_readFloat_1_ + ": " + s);
            return p_readFloat_2_;
        }
        return f;
    }

    public static ResourceLocation getHdFontLocation(ResourceLocation p_getHdFontLocation_0_) {
        if (!Config.isCustomFonts()) {
            return p_getHdFontLocation_0_;
        }
        if (p_getHdFontLocation_0_ == null) {
            return p_getHdFontLocation_0_;
        }
        String s = p_getHdFontLocation_0_.getResourcePath();
        String s1 = "textures/";
        String s2 = "mcpatcher/";
        if (!s.startsWith(s1)) {
            return p_getHdFontLocation_0_;
        }
        s = s.substring(s1.length());
        s = s2 + s;
        ResourceLocation resourcelocation = new ResourceLocation(p_getHdFontLocation_0_.getResourceDomain(), s);
        return Config.hasResource(Config.getResourceManager(), resourcelocation) ? resourcelocation : p_getHdFontLocation_0_;
    }
}

