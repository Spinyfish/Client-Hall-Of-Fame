package de.lirium.util;

import de.lirium.util.render.FontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class FontLoader {

    private final HashMap<String, HashMap<Integer, FontRenderer>> fontMap = new HashMap<>();


    public FontRenderer get(final String name, final int size, final String path) {
        if (!fontMap.containsKey(name))
            fontMap.put(name, new HashMap<>());
        if (!fontMap.get(name).containsKey(size))
            fontMap.get(name).put(size, createFontRenderer(name, size, path));
        return fontMap.get(name).get(size);
    }

    public FontRenderer get(final String name, final int size) {
        return get(name, size, null);
    }

    public FontRenderer createFontRenderer(final String name, final int size, final String path) {
        if (path != null) {
            try {
                final InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(path)).getInputStream();
                final Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
                return new FontRenderer(font.deriveFont(size));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
        return new FontRenderer(new Font(name, Font.PLAIN, size));
    }
}

