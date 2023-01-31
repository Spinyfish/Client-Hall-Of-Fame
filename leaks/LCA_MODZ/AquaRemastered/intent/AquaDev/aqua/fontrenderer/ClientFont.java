package intent.AquaDev.aqua.fontrenderer;

import java.awt.Font;
import java.io.InputStream;

public class ClientFont {
   public static GlyphPageFontRenderer font(int size, String fontName, boolean ttf) {
      try {
         InputStream istream = ClientFont.class.getResourceAsStream("/resources/" + fontName + ".ttf");
         Font myFont = Font.createFont(0, istream).deriveFont((float)size);
         GlyphPage fontPage = new GlyphPage(ttf ? myFont : new Font(fontName, 0, size), true, true);
         char[] chars = new char[256];

         for(int i = 0; i < chars.length; ++i) {
            chars[i] = (char)i;
         }

         fontPage.generateGlyphPage(chars);
         fontPage.setupTexture();
         return new GlyphPageFontRenderer(fontPage, fontPage, fontPage, fontPage);
      } catch (Exception var9) {
         GlyphPage fontPage = new GlyphPage(new Font("Arial", 0, size), true, true);
         char[] chars = new char[256];

         for(int i = 0; i < chars.length; ++i) {
            chars[i] = (char)i;
         }

         fontPage.generateGlyphPage(chars);

         try {
            fontPage.setupTexture();
         } catch (Exception var8) {
         }

         return new GlyphPageFontRenderer(fontPage, fontPage, fontPage, fontPage);
      }
   }
}
