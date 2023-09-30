package wtf.automn.fontrenderer;

import java.awt.*;
import java.io.InputStream;

public class ClientFont {

  public static GlyphPageFontRenderer font(int size, String fontName, boolean ttf) {
    try {
      InputStream istream = ClientFont.class.getResourceAsStream("/resources/" + fontName + ".ttf");
      Font myFont = Font.createFont(Font.TRUETYPE_FONT, istream).deriveFont((float) size);
      GlyphPage fontPage = new GlyphPage(ttf ? myFont : new Font(fontName, Font.PLAIN, size), true, true);
      char[] chars = new char[256];
      for (int i = 0; i < chars.length; i++) {
        chars[i] = (char) i;
      }
      fontPage.generateGlyphPage(chars);
      fontPage.setupTexture();
      GlyphPageFontRenderer fontrenderer = new GlyphPageFontRenderer(fontPage, fontPage, fontPage, fontPage);
      return fontrenderer;
    } catch (Exception e) {

      GlyphPage fontPage = new GlyphPage(new Font("Arial", Font.PLAIN, size), true, true);
      char[] chars = new char[256];
      for (int i = 0; i < chars.length; i++) {
        chars[i] = (char) i;
      }
      fontPage.generateGlyphPage(chars);
      try {
        fontPage.setupTexture();
      } catch (Exception e2) {
      }
      GlyphPageFontRenderer fontrenderer = new GlyphPageFontRenderer(fontPage, fontPage, fontPage, fontPage);
      return fontrenderer;
    }
  }
}
