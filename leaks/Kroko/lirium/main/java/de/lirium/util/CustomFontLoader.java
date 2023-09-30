package de.lirium.util;

import de.lirium.util.interfaces.Logger;
import de.lirium.util.render.FontRenderer;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class CustomFontLoader {

    private final HashMap<String, HashMap<Integer, FontRenderer>> fontMap = new HashMap<>();

    public FontRenderer sfui10, panagram25;

    public CustomFontLoader() {
        final Map<String, Font> locationMap = new HashMap<>();
        sfui10 = new FontRenderer(getFont(locationMap, "SFUI.otf", 10));
        panagram25 = new FontRenderer(getFont(locationMap, "panagram.ttf", 20));
    }

    private Font getFont(Map<String, Font> locationMap, String location, int size) {
        Font font;

        try {
            if (locationMap.containsKey(location)) {
                font = locationMap.get(location).deriveFont(Font.PLAIN, size);
            } else {
                final InputStream is = getClass().getResourceAsStream("/assets/minecraft/lirium/fonts/" + location);
                font = Font.createFont(0, is);
                locationMap.put(location, font);
                font = font.deriveFont(Font.PLAIN, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.print("Error Loading font");
            Logger.print("Location: " + location);
            font = new Font("default", Font.PLAIN, +10);
        }

        return font;
    }

}

