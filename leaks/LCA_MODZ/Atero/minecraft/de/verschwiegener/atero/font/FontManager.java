package de.verschwiegener.atero.font;

public class FontManager {

    public static GlyphPageFontRenderer ROBOTOTHIN_20;
    public static GlyphPageFontRenderer VistolSans_Light;
    public static GlyphPageFontRenderer VistolSans_Light2;

    public FontManager() {
        ROBOTOTHIN_20 = GlyphPageFontRenderer.create("Roboto-Thin", 20, false, false, false, true);
        VistolSans_Light = GlyphPageFontRenderer.create("VistolSans_Light", 20, false, false, false, true);
        VistolSans_Light2 = GlyphPageFontRenderer.create("VistolSans_Light", 25, false, false, false, true);

    }

}
