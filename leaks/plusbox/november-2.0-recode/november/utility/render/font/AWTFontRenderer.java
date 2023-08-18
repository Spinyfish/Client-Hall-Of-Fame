/* November.lol © 2023 */
package lol.november.utility.render.font;

import static java.awt.Font.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import lol.november.utility.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class AWTFontRenderer extends FontRenderer {

  public static final int OFFSET = 8;

  private static final Minecraft mc = Minecraft.getMinecraft();

  private static final String COLOR_INDICES = "0123456789abcdefklmnor";
  private static final char SECTION = '\u00A7';
  private static final int DEFAULT_COLOR = 16777215;

  /**
   * The {@link AWTFont} objects for each style for the {@link Font}
   */
  private final AWTFont normal, bold, italic, boldItalic;

  public AWTFontRenderer(Font font) {
    super(
      mc.gameSettings,
      new ResourceLocation("textures/font/ascii.png"),
      mc.getTextureManager(),
      false
    );
    // yikes
    normal = new AWTFont(font, PLAIN);
    bold = new AWTFont(font, BOLD);
    italic = new AWTFont(font, ITALIC);
    boldItalic = new AWTFont(font, BOLD + ITALIC);

    FONT_HEIGHT = (int) (normal.getFontHeight() / 2.0) + 1;
  }

  public double alignV(double y, double height) {
    return y + (height / 2.0) - (FONT_HEIGHT / 2.0);
  }

  @Override
  public int drawString(
    String text,
    float x,
    float y,
    int color,
    boolean dropShadow
  ) {
    int posX;
    if (dropShadow) {
      posX = renderString(text, x + 0.5f, y + 0.5f, color, true);
      posX = Math.max(posX, renderString(text, x, y, color, false));
    } else {
      posX = renderString(text, x, y, color, false);
    }

    return posX;
  }

  public int drawStringWithShadow(String text, double x, double y, int color) {
    return drawStringWithShadow(text, (float) x, (float) y, color);
  }

  @Override
  public int renderString(
    String text,
    float x,
    float y,
    int color,
    boolean dropShadow
  ) {
    if ((color & -67108864) == 0) {
      color |= -16777216;
    }

    if (dropShadow) {
      color = (color & 16579836) >> 2 | color & -16777216;
    }

    glPushMatrix();

    GlStateManager.enableBlend();
    GlStateManager.enableTexture2D();

    GlStateManager.enableRescaleNormal();

    glTranslated(x, y - 2, 0.0);
    glScaled(0.5, 0.5, 0.5);

    int textColor = color;
    RenderUtils.colorTo(textColor);
    float alpha = (textColor >> 24 & 0xff) / 255.0f;

    AWTFont renderer = normal;
    GlStateManager.bindTexture(renderer.getTextureId());

    // styles
    boolean strikethrough = false;
    boolean underline = false;

    double posX = 0.0;
    char[] chars = text.toCharArray();
    for (int i = 0; i < chars.length; ++i) {
      char c = chars[i];

      if (c == SECTION) {
        if (i + 1 > chars.length) continue;
        char at = chars[i + 1];

        boolean changedRenderer = false;

        switch (at) {
          case 'l' -> {
            changedRenderer = true;
            if (renderer.getStyle() == ITALIC) {
              renderer = boldItalic;
            } else {
              renderer = bold;
            }
          }
          case 'o' -> {
            changedRenderer = true;
            if (renderer.getStyle() == BOLD) {
              renderer = boldItalic;
            } else {
              renderer = italic;
            }
          }
          case 'm' -> strikethrough = true;
          case 'n' -> underline = true;
          case 'r' -> {
            changedRenderer = true;
            strikethrough = false;
            underline = false;

            textColor = color;
            RenderUtils.colorTo(color);
            renderer = normal;
          }
          default -> {
            if (at < 'k') {
              textColor = colorFrom(at, dropShadow);
              if (textColor == -1) {
                // TODO: custom colors
                textColor = DEFAULT_COLOR;
              }

              float red = (float) (textColor >> 16) / 255.0f;
              float green = (float) (textColor >> 8 & 255) / 255.0f;
              float blue = (float) (textColor & 255) / 255.0f;

              setColor(red, green, blue, alpha);
            }
          }
        }

        if (changedRenderer) GlStateManager.bindTexture(
          renderer.getTextureId()
        );

        ++i;
        continue;
      }

      Glyph glyph = renderer.get(c);
      if (glyph != null) renderer.drawChar(glyph, posX, 0.0);

      double glyphWidth = glyph == null
        ? renderer.getSpaceWidth()
        : glyph.getWidth() - OFFSET;

      if (strikethrough) {
        double posY = y + (FONT_HEIGHT * 1.5);
        RenderUtils.line(posX, posY, posX + glyphWidth, posY, 1.5f, textColor);
      }

      if (underline) {
        double posY = y + (FONT_HEIGHT * 2.5);
        RenderUtils.line(posX, posY, posX + glyphWidth, posY, 1.5f, textColor);
      }

      posX += glyphWidth;
    }

    glPopMatrix();

    return (int) ((x + posX) / 2.0);
  }

  @Override
  public int getStringWidth(String text) {
    char[] chars = text.toCharArray();
    double width = 0.0;

    AWTFont renderer = normal;
    for (int i = 0; i < chars.length; ++i) {
      char c = chars[i];
      if (c == ' ') {
        width += renderer.getSpaceWidth();
      } else if (c == SECTION) {
        char at = chars[i + 1];

        switch (at) {
          case 'l' -> {
            if (renderer.getStyle() == ITALIC) {
              renderer = boldItalic;
            } else {
              renderer = bold;
            }
          }
          case 'o' -> {
            if (renderer.getStyle() == BOLD) {
              renderer = boldItalic;
            } else {
              renderer = italic;
            }
          }
          case 'r' -> renderer = normal;
        }

        continue;
      }

      Glyph glyph = renderer.get(c);
      if (glyph == null) continue;

      width += glyph.getWidth() - OFFSET;
    }

    return (int) (width / 2.0);
  }

  public int colorFrom(char c, boolean dropShadow) {
    int colorIndex = COLOR_INDICES.indexOf(c);
    if (colorIndex == -1) {
      // TODO: custom colors

      return -1;
    }

    if (dropShadow) colorIndex += 16;
    return colorCode[colorIndex];
  }
}
