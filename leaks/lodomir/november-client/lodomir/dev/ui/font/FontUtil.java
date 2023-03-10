/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.font;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import lodomir.dev.ui.HUD;
import lodomir.dev.ui.font.FontRenderer;

public class FontUtil {
    public static volatile int completed;
    public static FontRenderer normal;
    public static FontRenderer two;
    public static FontRenderer small;
    private static Font normal_;
    private static Font two_;
    private static Font small_;

    private static Font getFont(Map<String, Font> locationMap, String location, int size, int fonttype) {
        Font font = null;
        try {
            if (locationMap.containsKey(location)) {
                font = locationMap.get(location).deriveFont(0, size);
            } else {
                InputStream is = HUD.class.getResourceAsStream("/assets/november/fonts/" + location);
                assert (is != null);
                font = Font.createFont(0, is);
                locationMap.put(location, font);
                font = font.deriveFont(fonttype, size);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("SFR", 0, size);
        }
        return font;
    }

    public static boolean hasLoaded() {
        return completed >= 3;
    }

    public static void bootstrap() {
        new Thread(() -> {
            HashMap<String, Font> locationMap = new HashMap<String, Font>();
            normal_ = FontUtil.getFont(locationMap, "gilroy.otf", 19, 0);
            two_ = FontUtil.getFont(locationMap, "gilroy.otf", 30, 0);
            small_ = FontUtil.getFont(locationMap, "gilroybold.otf", 14, 1);
            ++completed;
        }).start();
        new Thread(() -> {
            HashMap locationMap = new HashMap();
            ++completed;
        }).start();
        new Thread(() -> {
            HashMap locationMap = new HashMap();
            ++completed;
        }).start();
        while (!FontUtil.hasLoaded()) {
            try {
                Thread.sleep(5L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        normal = new FontRenderer(normal_, true, true);
        two = new FontRenderer(normal_, true, true);
        small = new FontRenderer(small_, true, true);
    }
}

