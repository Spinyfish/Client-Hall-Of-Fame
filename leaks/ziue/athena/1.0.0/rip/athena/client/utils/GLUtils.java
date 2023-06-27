package rip.athena.client.utils;

import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.shader.*;

public class GLUtils
{
    private static Minecraft mc;
    
    public static void scissor(final float x, final float y, final float width, final float height) {
        final int scaleFactor = getScaleFactor();
        GL11.glScissor((int)(x * scaleFactor), (int)(GLUtils.mc.displayHeight - (y + height) * scaleFactor), (int)((x + width - x) * scaleFactor), (int)((y + height - y) * scaleFactor));
    }
    
    public static int getScaleFactor() {
        int scaleFactor = 1;
        final boolean isUnicode = GLUtils.mc.isUnicode();
        int guiScale = GLUtils.mc.gameSettings.guiScale;
        if (guiScale == 0) {
            guiScale = 1000;
        }
        while (scaleFactor < guiScale && GLUtils.mc.displayWidth / (scaleFactor + 1) >= 320 && GLUtils.mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        if (isUnicode && scaleFactor % 2 != 0 && scaleFactor != 1) {
            --scaleFactor;
        }
        return scaleFactor;
    }
    
    public static void setAlphaLimit(final float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, (float)(limit * 0.01));
    }
    
    public static Framebuffer createFrameBuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != GLUtils.mc.displayWidth || framebuffer.framebufferHeight != GLUtils.mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(GLUtils.mc.displayWidth, GLUtils.mc.displayHeight, true);
        }
        return framebuffer;
    }
    
    public static void bindTexture(final int texture) {
        GL11.glBindTexture(3553, texture);
    }
    
    public static void startScale(final float x, final float y, final float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0f);
        GlStateManager.scale(scale, scale, 1.0f);
        GlStateManager.translate(-x, -y, 0.0f);
    }
    
    public static void startScale(final float x, final float y, final float width, final float height, final float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((x + (x + width)) / 2.0f, (y + (y + height)) / 2.0f, 0.0f);
        GlStateManager.scale(scale, scale, 1.0f);
        GlStateManager.translate(-(x + (x + width)) / 2.0f, -(y + (y + height)) / 2.0f, 0.0f);
    }
    
    public static void stopScale() {
        GlStateManager.popMatrix();
    }
    
    public static void startTranslate(final float x, final float y) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0f);
    }
    
    public static void stopTranslate() {
        GlStateManager.popMatrix();
    }
    
    public static void fixEnchantment() {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }
    
    static {
        GLUtils.mc = Minecraft.getMinecraft();
    }
}
