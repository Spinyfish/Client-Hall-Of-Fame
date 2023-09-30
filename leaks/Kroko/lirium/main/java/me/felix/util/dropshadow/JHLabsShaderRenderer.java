package me.felix.util.dropshadow;

import com.jhlabs.image.*;
import de.lirium.util.interfaces.IMinecraft;
import de.lirium.util.render.StencilUtil;
import de.lirium.util.render.shader.TextureRenderer;
import god.buddy.aot.BCompiler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class JHLabsShaderRenderer implements IMinecraft {

    private static final HashMap<Integer, Integer> shadowCache = new HashMap<>();

    public static void renderShadowOnObject(int x, int y, int width, int height, int radius, Color color, Runnable runnable) {
        StencilUtil.init();
        runnable.run();
        StencilUtil.readBuffer(0);
        renderShadow(x, y, width, height, radius, color);
        StencilUtil.uninit();
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static void renderShadow(int x, int y, int width, int height, int radius, Color color) {
        push();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
        renderShadowWithoutPrepare(x, y, width, height, radius, color);
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        pop();
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private static void renderShadowWithoutPrepare(final double posX, final double posY, double width, double height, final int radius, final Color color) {
        handleAlpha();

        final float[] positions = {(float) (posX - (double) radius - 0.25), (float) (posY - (double) radius + 0.25)};

        final int id = (int) ((width += radius * 2) * (height += radius * 2) + width + (double) (color.hashCode() * radius) + (double) radius);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);

        int texture;
        if (shadowCache.containsKey(id)) {
            texture = shadowCache.get(id);
            GlStateManager.bindTexture(texture);
        } else {

            width = MathHelper.clamp(width, 0.01, width);
            height = MathHelper.clamp(height, 0.01, height);
            final BufferedImage bufferedImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
            final Graphics g = bufferedImage.getGraphics();
            {
                g.setColor(color);
                g.fillRect(radius, radius, (int) width - radius * 2, (int) height - radius * 2);
                g.dispose();
            }
            final GaussianFilter gaussianFilter = new GaussianFilter(radius);
            final BufferedImage imageFilter = gaussianFilter.filter(bufferedImage, null);
            texture = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), imageFilter, true, true);
            shadowCache.put(id, texture);
        }

        TextureRenderer.drawTexture(positions[0], positions[1], (int) width, (int) height);
        GL11.glDisable(GL_TEXTURE_2D);

    }


    private static void handleAlpha() {
        GlStateManager.alphaFunc(GL11.GL_GREATER, (float) (0.1 * .01));
    }

    private static void push() {
        GlStateManager.pushMatrix();
    }

    private static void pop() {
        GlStateManager.popMatrix();
    }

}
