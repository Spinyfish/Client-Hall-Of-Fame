package de.verschwiegener.atero.design.font.fonts;

import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.util.Util;

public class WaterMarkFont extends Font{

    public WaterMarkFont() {
        super("WaterMarkFont", Util.getFontByName("OfficinaSans-Book"), 15F);
    }
}
