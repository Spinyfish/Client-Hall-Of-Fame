package de.fanta.design.fonts;

import de.fanta.utils.UnicodeFontRenderer;

public class FontManager {

    public UnicodeFontRenderer unicodeBasicFontRenderer;
    public UnicodeFontRenderer arial;

    public FontManager() {

    unicodeBasicFontRenderer = unicodeBasicFontRenderer.getFontOnPC("Arial",22,0,0,1);
    arial = unicodeBasicFontRenderer.getFontOnPC("Arial",24,0,2,6);

}

}
