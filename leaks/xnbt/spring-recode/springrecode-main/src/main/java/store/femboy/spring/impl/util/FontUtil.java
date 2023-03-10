package store.femboy.spring.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import store.femboy.spring.impl.fontrenderer.MinecraftFontRenderer;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontUtil {
    public static volatile int completed;
    public static MinecraftFontRenderer normal, springTitle, clickGui, icons, nunito21, icons45, nunitobold22;
    private static Font normal_, springTitle_, clickGui_, icons_, nunito21_, icons45_, nunitobold22_;

    private static Font getFont(Map<String, Font> locationMap, String location, int size) {
        Font font = null;

        try {
            if (locationMap.containsKey(location)) {
                font = locationMap.get(location).deriveFont(Font.PLAIN, size);
            } else {
                InputStream is = Minecraft.getMinecraft().getResourceManager()
                        .getResource(new ResourceLocation("spring/font/" + location)).getInputStream();
                font = Font.createFont(0, is);
                locationMap.put(location, font);
                font = font.deriveFont(Font.PLAIN, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, +10);
        }

        return font;
    }

    public static boolean hasLoaded() {
        return completed >= 3;
    }

    public static void bootstrap() {
        new Thread(() ->
        {
            Map<String, Font> locationMap = new HashMap<>();
            normal_ = getFont(locationMap, "nunitobold.ttf", 20);
            nunito21_ = getFont(locationMap, "nunitoregular.ttf", 21);
            nunitobold22_ = getFont(locationMap, "nunitobold.ttf", 22);
            springTitle_ = getFont(locationMap, "nunitobold.ttf", 30);
            clickGui_ = getFont(locationMap, "menlo.ttf", 20);
            icons_ = getFont(locationMap, "icons.ttf", 30);
            icons45_ = getFont(locationMap, "icons.ttf", 45);
            completed++;
        }).start();
        new Thread(() ->
        {
            Map<String, Font> locationMap = new HashMap<>();
            completed++;
        }).start();
        new Thread(() ->
        {
            Map<String, Font> locationMap = new HashMap<>();
            completed++;
        }).start();

        while (!hasLoaded()) {
            try {
                //noinspection BusyWait
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        normal = new MinecraftFontRenderer(normal_, true, true);
        springTitle = new MinecraftFontRenderer(springTitle_, true, true);
        icons = new MinecraftFontRenderer(icons_, true, true);
        clickGui = new MinecraftFontRenderer(clickGui_, true, true);
        nunito21 = new MinecraftFontRenderer(nunito21_, true, true);
        icons45 = new MinecraftFontRenderer(icons45_, true, true);
        nunitobold22 = new MinecraftFontRenderer(nunitobold22_, true, true);
    }
}
