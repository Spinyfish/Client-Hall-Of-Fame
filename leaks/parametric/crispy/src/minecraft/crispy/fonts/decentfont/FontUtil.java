package crispy.fonts.decentfont;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("NonAtomicOperationOnVolatileField")
public class FontUtil {
    public static volatile int completed;
    public static MinecraftFontRenderer cleanmedium, clean, cleanSmall, testFont;
    private static Font  cleanmedium_, clean_, cleanSmall_, _testFont;

    private static Font getFont(Map<String, Font> locationMap, String location, int size) {
        Font font = null;

        try {
            if (locationMap.containsKey(location)) {
                font = locationMap.get(location).deriveFont(Font.PLAIN, size);
            } else {
                InputStream is = Minecraft.getMinecraft().getResourceManager()
                        .getResource(new ResourceLocation("Client/fonts/" + location)).getInputStream();
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
            clean_ = getFont(locationMap, "Applefnt.ttf", 24);
            cleanmedium_ = getFont(locationMap, "Applefnt.ttf", 20);
            cleanSmall_ = getFont(locationMap, "Applefnt.ttf", 17);
            _testFont = getFont(locationMap, "Dreamscape.ttf", 45);
            completed = 3;
        }).start();


        while (!hasLoaded()) {
            try {
                //noinspection BusyWait
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        cleanmedium = new MinecraftFontRenderer(cleanmedium_, true, true);
        clean = new MinecraftFontRenderer(clean_, true, true);
        cleanSmall = new MinecraftFontRenderer(cleanSmall_, true, true);
        testFont = new MinecraftFontRenderer(_testFont, true, true);

    }
}