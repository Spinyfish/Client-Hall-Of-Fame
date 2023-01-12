package dev.eternal.client.util.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class BloomUtil {

  public static HashMap<Integer, Integer> shadowCache = new HashMap<>();

  public static void bloom(int x, int y, int width, int height, int blurRadius, int bloomAlpha) {
    bloom(x, y, width, height, blurRadius, new Color(0, 0, 0, bloomAlpha), false);
  }

  public static void bloom(int x, int y, int width, int height, int blurRadius, Color color) {
    bloom(x, y, width, height, blurRadius, color, false);
  }

  public static void bloom(int x, int y, int width, int height, int blurRadius, Color color, boolean ignoreModule) {
    GlStateManager.pushAttrib();
    GlStateManager.pushMatrix();

    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);

    height = Math.max(0, height);
    width = Math.max(0, width);
    width = width + blurRadius * 2;
    height = height + blurRadius * 2;
    x = x - blurRadius;
    y = y - blurRadius;

    float _X = x - 0.25f;
    float _Y = y + 0.25f;

    int identifier = width * height + width + color.hashCode() * blurRadius + blurRadius;

    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    GL11.glEnable(GL11.GL_BLEND);

    int texId;
    if (shadowCache.containsKey(identifier)) {
      texId = shadowCache.get(identifier);

      GlStateManager.bindTexture(texId);
    } else {
      BufferedImage original = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

      Graphics g = original.getGraphics();
      g.setColor(color);
      g.fillRect(blurRadius, blurRadius, width - blurRadius * 2, height - blurRadius * 2);
      g.dispose();


    }

    GL11.glColor4f(1f, 1f, 1f, 1f);
    GL11.glBegin(GL11.GL_QUADS);
    GL11.glTexCoord2f(0, 0);
    GL11.glVertex2d(_X, _Y);
    GL11.glTexCoord2f(0, 1);
    GL11.glVertex2d(_X, _Y + height);
    GL11.glTexCoord2f(1, 1);
    GL11.glVertex2d(_X + width, _Y + height);
    GL11.glTexCoord2f(1, 0);
    GL11.glVertex2d(_X + width, _Y);
    GL11.glEnd();
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_ALPHA_TEST);
    GL11.glDisable(GL11.GL_BLEND);
    GlStateManager.popAttrib();
    GlStateManager.popMatrix();
  }
}
