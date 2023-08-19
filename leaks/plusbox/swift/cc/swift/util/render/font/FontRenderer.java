/**
 * @project hakarware
 * @author CodeMan
 * @at 26.07.23, 17:33
 */

package cc.swift.util.render.font;

import cc.swift.util.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class FontRenderer implements IMinecraft {
    private final Map<String, Tuple<CFontRenderer, Integer>> FONTS = new HashMap<>();

    public CFontRenderer getFont(final String name, final int size) {
        if (FONTS.containsKey(name + " " + size))
            return FONTS.get(name + " " + size).getFirst();

        final CFontRenderer font = new CFontRenderer(getAWTFont(name, size), true, true);
        FONTS.put(name + " " + size, new Tuple<>(font, size));
        return font;
    }

    private Font getAWTFont(final String fontName, final int size) {
        try {
            return Font.createFont(0, mc.getResourceManager().getResource(new ResourceLocation("swift/fonts/" + fontName)).getInputStream()).deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            return new Font(fontName, Font.PLAIN, size);
        }
    }
}
