/* November.lol © 2023 */
package lol.november.utility.render;

import static org.lwjgl.opengl.GL11.*;

import lol.november.utility.render.shader.Shader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class RenderUtils {

  /**
   * The {@link ScaledResolution} set in {@link net.minecraft.client.gui.GuiIngame#renderGameOverlay(float)}
   */
  public static ScaledResolution resolution;

  /**
   * A {@link Shader} to render a rounded rectangle
   */
  private static final Shader roundedRect = new Shader(
    "/assets/november/shader/vertex.vsh",
    "/assets/november/shader/roundedrect.fsh",
    shader -> {
      shader.createUniform("size");
      shader.createUniform("color");
      shader.createUniform("radius");
      shader.createUniform("softness");
    }
  );

  private static final Shader blur = new Shader(
          "/assets/november/shader/vertex.vsh",
          "/assets/november/shader/blur.fsh",
          shader -> {
            shader.createUniform("size");
            shader.createUniform("color");
            shader.createUniform("radius");
            shader.createUniform("softness");
          }
  );

  /**
   * Runs a scissor box
   *
   * @param x      the starting x scissor coordinate
   * @param y      the starting y scissor coordinate
   * @param width  the width of the scissor box
   * @param height the height of the scissor box
   * @author TerrificTable
   */
  public static void scissor(double x, double y, double width, double height) {
    double scale = resolution.getScaleFactor();

    y = resolution.getScaledHeight() - y;

    x *= scale;
    y *= scale;
    width *= scale;
    height *= scale;

    glEnable(GL_SCISSOR_TEST);
    glScissor((int) x, (int) (y - height), (int) width, (int) height);
  }

  /**
   * Ends the previously enabled scissor box
   */
  public static void endScissor() {
    glDisable(GL_SCISSOR_TEST);
  }

  /**
   * Gets the ARGB values of a hex color
   *
   * @param color the color hex
   * @return a float[] of 4 elements in the format of ARGB
   */
  public static float[] colorFrom(int color) {
    float alpha = (color >> 24 & 0xff) / 255.0f;
    float red = (color >> 16 & 0xff) / 255.0f;
    float green = (color >> 8 & 0xff) / 255.0f;
    float blue = (color & 0xff) / 255.0f;

    return new float[] { alpha, red, green, blue };
  }

  /**
   * Sets an ARGB color to the {@link GlStateManager}
   *
   * @param color the color hex
   */
  public static void colorTo(int color) {
    float alpha = (color >> 24 & 0xff) / 255.0f;
    float red = (color >> 16 & 0xff) / 255.0f;
    float green = (color >> 8 & 0xff) / 255.0f;
    float blue = (color & 0xff) / 255.0f;

    GlStateManager.color(red, green, blue, alpha);
  }

  public static void line(
    double x,
    double y,
    double x2,
    double y2,
    float width,
    int color
  ) {
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    OpenGlHelper.glBlendFunc(
      GL_SRC_ALPHA,
      GL_ONE_MINUS_SRC_ALPHA,
      GL_ZERO,
      GL_ONE
    );

    glEnable(GL_LINE_SMOOTH);
    glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
    glLineWidth(width);

    float[] argb = colorFrom(color);

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer renderer = tessellator.getWorldRenderer();
    renderer.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);
    renderer
      .pos(x, y, 0.0)
      .color(argb[0], argb[1], argb[2], argb[3])
      .endVertex();
    renderer
      .pos(x2, y2, 0.0)
      .color(argb[0], argb[1], argb[2], argb[3])
      .endVertex();
    tessellator.draw();

    glLineWidth(1.0f);
    glDisable(GL_LINE_SMOOTH);

    GlStateManager.enableTexture2D();
  }

  public static void rect(double x, double y, double w, double h, int color) {
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    OpenGlHelper.glBlendFunc(
      GL_SRC_ALPHA,
      GL_ONE_MINUS_SRC_ALPHA,
      GL_ZERO,
      GL_ONE
    );

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer buffer = tessellator.getWorldRenderer();

    float alpha = (color >> 24 & 0xff) / 255.0f;
    float red = (color >> 16 & 0xff) / 255.0f;
    float green = (color >> 8 & 0xff) / 255.0f;
    float blue = (color & 0xff) / 255.0f;

    buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
    buffer.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
    buffer.pos(x, y + h, 0.0).color(red, green, blue, alpha).endVertex();
    buffer.pos(x + w, y + h, 0.0).color(red, green, blue, alpha).endVertex();
    buffer.pos(x + w, y, 0.0).color(red, green, blue, alpha).endVertex();
    tessellator.draw();

    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }

  /**
   * Draws a rounded rectangle
   *
   * @param x      the x render coordinate
   * @param y      the y render coordinate
   * @param width  the width of the rectangle
   * @param height the height of the rectangle
   * @param radius the radius of the edges of the rectangle
   * @param color  the color of the rectangle
   */
  public static void roundRect(
    double x,
    double y,
    double width,
    double height,
    float radius,
    int color
  ) {
    GlStateManager.enableTexture2D();
    GlStateManager.enableBlend();
    OpenGlHelper.glBlendFunc(
      GL_SRC_ALPHA,
      GL_ONE_MINUS_SRC_ALPHA,
      GL_ZERO,
      GL_ONE
    );

    float[] argb = colorFrom(color);
    roundedRect.use();
    {
      roundedRect.set(
        "size",
        (float) (width * resolution.getScaleFactor()),
        (float) (height * resolution.getScaleFactor())
      );
      roundedRect.set("color", argb[1], argb[2], argb[3], argb[0]);
      roundedRect.set("radius", radius * resolution.getScaleFactor());
      roundedRect.set("softness", 1.0f);

      glBegin(GL_QUADS);
      {
        glTexCoord2d(0.0, 0.0);
        glVertex2d(x, y);
        glTexCoord2d(0.0, 1.0);
        glVertex2d(x, y + height);
        glTexCoord2d(1.0, 1.0);
        glVertex2d(x + width, y + height);
        glTexCoord2d(1.0, 0.0);
        glVertex2d(x + width, y);
      }
      glEnd();
    }
    roundedRect.stop();

    GlStateManager.disableBlend();
  }
}
