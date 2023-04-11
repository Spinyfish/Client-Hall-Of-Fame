package byron.Mono.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("NonAtomicOperationOnVolatileField")
public class FontUtil {
    public static volatile int completed;
    public static MinecraftFontRenderer normal;
    public static MinecraftFontRenderer normal2;
    public static MinecraftFontRenderer normal3;
    public static MinecraftFontRenderer litenormal;
    private static Font normal_;
    private static Font normal2_;
    private static Font normal3_;
    private static Font litenormal_;

    private static Font getFont(Map<String, Font> locationMap, String location, int size) {
        Font font = null;

        try {
            if (locationMap.containsKey(location)) {
                font = locationMap.get(location).deriveFont(Font.PLAIN, size);
            } else {
                InputStream is = Minecraft.getMinecraft().getResourceManager()
                        .getResource(new ResourceLocation("Mono/font/" + location)).getInputStream();
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
            normal_ = getFont(locationMap, "Raleway.ttf", 19);
            normal2_ = getFont(locationMap, "Raleway.ttf", 24);
            normal3_ = getFont(locationMap, "Raleway.ttf", 44);
            litenormal_ = getFont(locationMap, "sflight.ttf", 19);
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
        normal2 = new MinecraftFontRenderer(normal2_, true, true);
        normal3 = new MinecraftFontRenderer(normal3_, true, true);
        litenormal = new MinecraftFontRenderer(litenormal_, true, true);
    }
}