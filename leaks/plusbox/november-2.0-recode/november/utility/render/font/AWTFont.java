/* November.lol © 2023 */
package lol.november.utility.render.font;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static lol.november.utility.render.font.AWTFontRenderer.OFFSET;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.DynamicTexture;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class AWTFont {

  /**
   * The maximum amount of characters in the font to cache
   */
  private static final int MAX_CHARSET = 512;

  private final Font font;
  private final int style;

  private final double glyphPageWidth = 512.0;
  private double glyphPageHeight;
  private double spaceWidth;

  private final Glyph[] glyphs;
  private final DynamicTexture texture;

  private double fontHeight;

  public AWTFont(Font font, int style) {
    this.font = font.deriveFont(style);
    this.style = style;

    glyphs = new Glyph[MAX_CHARSET];
    texture = bitmap();
  }

  /**
   * Draws a font character from the bitmap
   *
   * @param glyph the {@link Glyph} object
   * @param x     the x render coordinate
   * @param y     the y render coordinate
   * @apiNote you must have already binded the bitmap texture before calling this
   */
  public void drawChar(Glyph glyph, double x, double y) {
    double textureX = glyph.getX() / glyphPageWidth;
    double textureY = (glyph.getY() / glyphPageHeight);
    double textureWidth = glyph.getWidth() / glyphPageWidth;
    double textureHeight = (glyph.getHeight() / glyphPageHeight);

    glBegin(GL_QUADS);
    {
      glTexCoord2d(textureX, textureY);
      glVertex2d(x, y);

      glTexCoord2d(textureX, textureY + textureHeight);
      glVertex2d(x, y + glyph.getHeight());

      glTexCoord2d(textureX + textureWidth, textureY + textureHeight);
      glVertex2d(x + glyph.getWidth(), y + glyph.getHeight());

      glTexCoord2d(textureX + textureWidth, textureY);
      glVertex2d(x + glyph.getWidth(), y);
    }
    glEnd();
  }

  /**
   * Generates a bitmap
   *
   * @return the {@link DynamicTexture} created from the {@link BufferedImage}
   */
  private DynamicTexture bitmap() {
    BufferedImage image = new BufferedImage(1, 1, TYPE_INT_ARGB);
    Graphics2D graphics = (Graphics2D) image.getGraphics();

    graphics.setFont(font);

    FontMetrics metrics = graphics.getFontMetrics();

    int x = 0, y = 0;

    for (int i = 0; i < glyphs.length; ++i) {
      char c = (char) i;
      if (!font.canDisplay(i) || i == font.getMissingGlyphCode()) {
        continue;
      }

      Rectangle bounds = metrics
        .getStringBounds(String.valueOf(c), graphics)
        .getBounds();

      if (c == ' ') {
        spaceWidth = bounds.getWidth();
        continue;
      }

      Glyph glyph = new Glyph(c);
      glyph.setWidth(bounds.getWidth() + OFFSET);
      glyph.setHeight(bounds.getHeight());

      double total = glyph.getHeight() + metrics.getAscent();
      if (total > fontHeight) {
        fontHeight = (int) (total / 2.0f);
      }

      if (x + glyph.getWidth() >= glyphPageWidth) {
        x = 0;
        y += fontHeight + OFFSET;
      }

      glyph.setX(x);
      glyph.setY(y);

      glyphs[i] = glyph;

      x += glyph.getWidth();
    }

    graphics.dispose();

    glyphPageHeight = y + fontHeight;
    image =
      new BufferedImage(
        (int) glyphPageWidth,
        (int) glyphPageHeight,
        TYPE_INT_ARGB
      );

    graphics = (Graphics2D) image.getGraphics();

    graphics.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );
    graphics.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    );
    graphics.setRenderingHint(
      RenderingHints.KEY_RESOLUTION_VARIANT,
      RenderingHints.VALUE_RESOLUTION_VARIANT_DPI_FIT
    );
    graphics.setRenderingHint(
      RenderingHints.KEY_FRACTIONALMETRICS,
      RenderingHints.VALUE_FRACTIONALMETRICS_ON
    );

    graphics.setFont(font);

    for (Glyph glyph : glyphs) {
      if (glyph == null) continue;

      graphics.drawString(
        String.valueOf(glyph.getCharacter()),
        (float) glyph.getX(),
        (float) (glyph.getY() + metrics.getAscent())
      );
    }

    DynamicTexture tex = new DynamicTexture(image);
    graphics.dispose();
    return tex;
  }

  public int getTextureId() {
    return texture.getGlTextureId();
  }

  public double getSpaceWidth() {
    return spaceWidth;
  }

  public int getStyle() {
    return style;
  }

  public double getFontHeight() {
    return fontHeight - OFFSET;
  }

  public Glyph get(char c) {
    if (!charExists(c)) return null;
    return glyphs[c];
  }

  private boolean charExists(char c) {
    if (c >= glyphs.length) return false;
    return glyphs[c] != null;
  }
}
