/*
 * Decompiled with CFR 0_132.
 */
package kevin.persional.milk.guis.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;

public class FontLoaders {

    private static final HashMap<String, HashMap<Float, UnicodeFontRenderer>> fonts = new HashMap<>();

    public static CFontRenderer novo16 = new CFontRenderer(FontLoaders.getNovolineFont(16), true, true);
    public static CFontRenderer novo18 = new CFontRenderer(FontLoaders.getNovolineFont(18), true, true);
    public static CFontRenderer novo20 = new CFontRenderer(FontLoaders.getNovolineFont(20), true, true);
    public static CFontRenderer novo22 = new CFontRenderer(FontLoaders.getNovolineFont(22), true, true);

    private static Font getDefault(int size) {
        return new Font("default", 0, size);
    }

    public static UnicodeFontRenderer getUniFont(String s, float size, boolean b2) {
        UnicodeFontRenderer unicodeFontRenderer = null;

        try {
            if (fonts.containsKey(s) && fonts.get(s).containsKey(size)) {
                return fonts.get(s).get(size);
            }

            Class<FontLoaders> class1 = FontLoaders.class;
            StringBuilder append = (new StringBuilder()).append("fonts/").append(s);
            String s2;
            if (b2) {
                s2 = ".otf";
            } else {
                s2 = ".ttf";
            }


            unicodeFontRenderer = new UnicodeFontRenderer(Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("client/msyh.ttf")).getInputStream()).deriveFont(size), size, -1, -1, false);
            unicodeFontRenderer.setUnicodeFlag(true);
            unicodeFontRenderer.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
            HashMap<Float, UnicodeFontRenderer> hashMap = new HashMap<>();
            if (fonts.containsKey(s)) {
                hashMap.putAll(fonts.get(s));
            }

            hashMap.put(size, unicodeFontRenderer);
            fonts.put(s, hashMap);
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return unicodeFontRenderer;
    }

    private static Font getNovolineFont(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("Novo.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

}

