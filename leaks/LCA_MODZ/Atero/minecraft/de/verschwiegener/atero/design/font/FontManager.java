package de.verschwiegener.atero.design.font;

import java.util.ArrayList;

import de.verschwiegener.atero.design.font.fonts.ArrayListFont;
import de.verschwiegener.atero.design.font.fonts.TextFont;
import de.verschwiegener.atero.design.font.fonts.TextFontBold;
import de.verschwiegener.atero.design.font.fonts.TextFontItalic;
import de.verschwiegener.atero.design.font.fonts.WaterMarkFont;
import net.minecraft.client.Minecraft;

public class FontManager {

    ArrayList<Font> fonts = new ArrayList<>();

    public FontManager() {
	fonts.add(new ArrayListFont());
	fonts.add(new TextFont());
	fonts.add(new WaterMarkFont());
	fonts.add(new TextFontBold());
	fonts.add(new TextFontItalic());
    }

    public Font getFontByName(final String name) {
	return fonts.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
