package intent.AquaDev.aqua.fontrenderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class GlyphPage {
   private final Font font;
   private final boolean antiAliasing;
   private final boolean fractionalMetrics;
   private final HashMap<Character, GlyphPage.Glyph> glyphCharacterMap = new HashMap<>();
   private int imgSize;
   private int maxFontHeight = -1;
   private BufferedImage bufferedImage;
   private DynamicTexture loadedTexture;

   public GlyphPage(Font font, boolean antiAliasing, boolean fractionalMetrics) {
      this.font = font;
      this.antiAliasing = antiAliasing;
      this.fractionalMetrics = fractionalMetrics;
   }

   public void generateGlyphPage(char[] chars) {
      double maxWidth = -1.0;
      double maxHeight = -1.0;
      AffineTransform affineTransform = new AffineTransform();
      FontRenderContext fontRenderContext = new FontRenderContext(affineTransform, this.antiAliasing, this.fractionalMetrics);

      for(char ch : chars) {
         Rectangle2D bounds = this.font.getStringBounds(Character.toString(ch), fontRenderContext);
         if (maxWidth < bounds.getWidth()) {
            maxWidth = bounds.getWidth();
         }

         if (maxHeight < bounds.getHeight()) {
            maxHeight = bounds.getHeight();
         }
      }

      maxWidth += 2.0;
      maxHeight += 2.0;
      this.imgSize = (int)Math.ceil(
            Math.max(
                  Math.ceil(Math.sqrt(maxWidth * maxWidth * (double)chars.length) / maxWidth),
                  Math.ceil(Math.sqrt(maxHeight * maxHeight * (double)chars.length) / maxHeight)
               )
               * Math.max(maxWidth, maxHeight)
         )
         + 1;
      this.bufferedImage = new BufferedImage(this.imgSize, this.imgSize, 2);
      Graphics2D g = (Graphics2D)this.bufferedImage.getGraphics();
      g.setFont(this.font);
      g.setColor(new Color(255, 255, 255, 0));
      g.fillRect(0, 0, this.imgSize, this.imgSize);
      g.setColor(Color.white);
      g.setRenderingHint(
         RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF
      );
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, this.antiAliasing ? RenderingHints.VALUE_ANTIALIAS_OFF : RenderingHints.VALUE_ANTIALIAS_ON);
      g.setRenderingHint(
         RenderingHints.KEY_TEXT_ANTIALIASING, this.antiAliasing ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
      );
      FontMetrics fontMetrics = g.getFontMetrics();
      int currentCharHeight = 0;
      int posX = 0;
      int posY = 1;

      for(char ch : chars) {
         GlyphPage.Glyph glyph = new GlyphPage.Glyph();
         Rectangle2D bounds = fontMetrics.getStringBounds(Character.toString(ch), g);
         glyph.width = bounds.getBounds().width + 8;
         glyph.height = bounds.getBounds().height;
         if (posY + glyph.height >= this.imgSize) {
         }

         if (posX + glyph.width >= this.imgSize) {
            posX = 0;
            posY += currentCharHeight;
            currentCharHeight = 0;
         }

         glyph.x = posX;
         glyph.y = posY;
         if (glyph.height > this.maxFontHeight) {
            this.maxFontHeight = glyph.height;
         }

         if (glyph.height > currentCharHeight) {
            currentCharHeight = glyph.height;
         }

         g.drawString(Character.toString(ch), posX + 2, posY + fontMetrics.getAscent());
         posX += glyph.width;
         this.glyphCharacterMap.put(ch, glyph);
      }
   }

   public void setupTexture() {
      this.loadedTexture = new DynamicTexture(this.bufferedImage);
   }

   public void bindTexture() {
      GlStateManager.bindTexture(this.loadedTexture.getGlTextureId());
   }

   public void unbindTexture() {
      GlStateManager.bindTexture(0);
   }

   public float drawChar(char ch, float x, float y) {
      GlyphPage.Glyph glyph = this.glyphCharacterMap.get(ch);
      if (glyph == null) {
         return 0.0F;
      } else {
         float pageX = (float)glyph.x / (float)this.imgSize;
         float pageY = (float)glyph.y / (float)this.imgSize;
         float pageWidth = (float)glyph.width / (float)this.imgSize;
         float pageHeight = (float)glyph.height / (float)this.imgSize;
         float width = (float)glyph.width;
         float height = (float)glyph.height;
         GL11.glBegin(4);
         GL11.glTexCoord2f(pageX + pageWidth, pageY);
         GL11.glVertex2f(x + width, y);
         GL11.glTexCoord2f(pageX, pageY);
         GL11.glVertex2f(x, y);
         GL11.glTexCoord2f(pageX, pageY + pageHeight);
         GL11.glVertex2f(x, y + height);
         GL11.glTexCoord2f(pageX, pageY + pageHeight);
         GL11.glVertex2f(x, y + height);
         GL11.glTexCoord2f(pageX + pageWidth, pageY + pageHeight);
         GL11.glVertex2f(x + width, y + height);
         GL11.glTexCoord2f(pageX + pageWidth, pageY);
         GL11.glVertex2f(x + width, y);
         GL11.glEnd();
         return width - 8.0F;
      }
   }

   public float getWidth(char ch) {
      try {
         return (float)this.glyphCharacterMap.get(ch).width;
      } catch (Exception var3) {
         return 0.0F;
      }
   }

   public int getMaxFontHeight() {
      return this.maxFontHeight;
   }

   public boolean isAntiAliasingEnabled() {
      return this.antiAliasing;
   }

   public boolean isFractionalMetricsEnabled() {
      return this.fractionalMetrics;
   }

   static class Glyph {
      private int x;
      private int y;
      private int width;
      private int height;

      Glyph(int x, int y, int width, int height) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }

      Glyph() {
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }
   }
}
