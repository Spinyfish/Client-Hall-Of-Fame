package com.craftworks.pearclient.util.font;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class FontUtil {
    public static volatile int completed;
    public static MinecraftFontRenderer regular;
    public static MinecraftFontRenderer bold;
    public static MinecraftFontRenderer bold_small;
    public static MinecraftFontRenderer regsmall;
    public static MinecraftFontRenderer regmed;
    public static MinecraftFontRenderer bold_large;
    public static Font regular_;
    public static Font bold_;
    public static Font bold_small_;
    public static Font regsmall_;
    public static Font regmed_;
    public static Font bold_large_;
    public static Font normal_;

    public FontUtil() {
    }

    private static Font getFont(Map<String, Font> locationMap, String location, int size) {
        Font font = null;

        try {
            if (locationMap.containsKey(location)) {
                font = ((Font)locationMap.get(location)).deriveFont(0, (float)size);
            } else {
                InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("pearclient/font/" + location)).getInputStream();
                font = Font.createFont(0, is);
                locationMap.put(location, font);
                font = font.deriveFont(0, (float)size);
            }
        } catch (Exception var5) {
            var5.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, 10);
        }

        return font;
    }

    public static boolean hasLoaded() {
        return completed >= 3;
    }

    public static void bootstrap() {
        (new Thread(() -> {
            Map<String, Font> locationMap = new HashMap();
            bold_small_ = getFont(locationMap, "Roboto-Bold.ttf", 14);
            regular_ = getFont(locationMap, "Roboto-Regular.ttf", 19);
            bold_ = getFont(locationMap, "Roboto-Bold.ttf", 19);
            bold_large_ = getFont(locationMap, "Roboto-Bold.ttf", 30);
            regsmall_ = getFont(locationMap, "Roboto-Thin.ttf", 10);
            regmed_ = getFont(locationMap, "Roboto-Thin.ttf", 15);
            ++completed;
        })).start();
        (new Thread(() -> {
            new HashMap();
            ++completed;
        })).start();
        (new Thread(() -> {
            new HashMap();
            ++completed;
        })).start();

        while(!hasLoaded()) {
            try {
                Thread.sleep(5L);
            } catch (InterruptedException var1) {
                var1.printStackTrace();
            }
        }

        regular = new MinecraftFontRenderer(regular_, true, true);
        bold_small = new MinecraftFontRenderer(bold_small_, true, true);
        bold = new MinecraftFontRenderer(bold_, true, true);
        regsmall = new MinecraftFontRenderer(regsmall_, true, true);
        regmed = new MinecraftFontRenderer(regmed_, true, true);
        bold_large = new MinecraftFontRenderer(bold_large_, true, true);
    }
}
