/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.WorldRenderer
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.shader.Framebuffer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.Vec3
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.GLU
 *  vip.astroline.client.storage.utils.render.ColorUtils
 */
package vip.astroline.client.storage.utils.render.render;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import vip.astroline.client.storage.utils.render.ColorUtils;

public class RenderUtil {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static int getColorFromPercentage(float current, float max) {
        float percentage = current / max / 3.0f;
        return Color.HSBtoRGB(percentage, 1.0f, 1.0f);
    }

    public static Framebuffer createFramebuffer(Framebuffer framebuffer, boolean depth) {
        if (framebuffer != null && framebuffer.framebufferWidth == RenderUtil.mc.displayWidth) {
            if (framebuffer.framebufferHeight == RenderUtil.mc.displayHeight) return framebuffer;
        }
        if (framebuffer == null) return new Framebuffer(RenderUtil.mc.displayWidth, RenderUtil.mc.displayHeight, depth);
        framebuffer.deleteFramebuffer();
        return new Framebuffer(RenderUtil.mc.displayWidth, RenderUtil.mc.displayHeight, depth);
    }

    public static void drawStack(FontRenderer font, boolean renderOverlay, ItemStack stack, float x, float y) {
        GL11.glPushMatrix();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null) {
            RenderHelper.enableGUIStandardItemLighting();
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.clear((int)256);
        GlStateManager.enableBlend();
        mc.getRenderItem().zLevel = -150.0f;
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, (int)x, (int)y);
        if (renderOverlay) {
            mc.getRenderItem().renderItemOverlayIntoGUI(font, stack, (int)x, (int)y, String.valueOf(stack.stackSize));
        }
        mc.getRenderItem().zLevel = 0.0f;
        GlStateManager.enableBlend();
        float z = 0.5f;
        GlStateManager.scale((float)0.5f, (float)0.5f, (float)0.5f);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GL11.glPopMatrix();
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float edgeRadius, int color, float borderWidth, int borderColor) {
        double angleRadians;
        int i;
        if (color == 0xFFFFFF) {
            color = ColorUtils.WHITE.c;
        }
        if (borderColor == 0xFFFFFF) {
            borderColor = ColorUtils.WHITE.c;
        }
        if (edgeRadius < 0.0f) {
            edgeRadius = 0.0f;
        }
        if (edgeRadius > width / 2.0f) {
            edgeRadius = width / 2.0f;
        }
        if (edgeRadius > height / 2.0f) {
            edgeRadius = height / 2.0f;
        }
        RenderUtil.drawRect(x + edgeRadius, y + edgeRadius, width - edgeRadius * 2.0f, height - edgeRadius * 2.0f, color);
        RenderUtil.drawRect(x + edgeRadius, y, width - edgeRadius * 2.0f, edgeRadius, color);
        RenderUtil.drawRect(x + edgeRadius, y + height - edgeRadius, width - edgeRadius * 2.0f, edgeRadius, color);
        RenderUtil.drawRect(x, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0f, color);
        RenderUtil.drawRect(x + width - edgeRadius, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0f, color);
        RenderUtil.enableRender2D();
        RenderUtil.color(color);
        GL11.glBegin((int)6);
        float centerX = x + edgeRadius;
        float centerY = y + edgeRadius;
        GL11.glVertex2d((double)centerX, (double)centerY);
        int vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        for (i = 0; i < vertices + 1; ++i) {
            angleRadians = Math.PI * 2 * (double)(i + 180) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
        }
        GL11.glEnd();
        GL11.glBegin((int)6);
        centerX = x + width - edgeRadius;
        centerY = y + edgeRadius;
        GL11.glVertex2d((double)centerX, (double)centerY);
        vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        for (i = 0; i < vertices + 1; ++i) {
            angleRadians = Math.PI * 2 * (double)(i + 90) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
        }
        GL11.glEnd();
        GL11.glBegin((int)6);
        centerX = x + edgeRadius;
        centerY = y + height - edgeRadius;
        GL11.glVertex2d((double)centerX, (double)centerY);
        vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        for (i = 0; i < vertices + 1; ++i) {
            angleRadians = Math.PI * 2 * (double)(i + 270) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
        }
        GL11.glEnd();
        GL11.glBegin((int)6);
        centerX = x + width - edgeRadius;
        centerY = y + height - edgeRadius;
        GL11.glVertex2d((double)centerX, (double)centerY);
        vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        for (i = 0; i < vertices + 1; ++i) {
            angleRadians = Math.PI * 2 * (double)i / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
        }
        GL11.glEnd();
        RenderUtil.color(borderColor);
        GL11.glLineWidth((float)borderWidth);
        GL11.glBegin((int)3);
        centerX = x + edgeRadius;
        centerY = y + edgeRadius;
        for (i = vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f); i >= 0; --i) {
            angleRadians = Math.PI * 2 * (double)(i + 180) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
        }
        GL11.glVertex2d((double)(x + edgeRadius), (double)y);
        GL11.glVertex2d((double)(x + width - edgeRadius), (double)y);
        centerX = x + width - edgeRadius;
        centerY = y + edgeRadius;
        for (i = vertices; i >= 0; --i) {
            angleRadians = Math.PI * 2 * (double)(i + 90) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
        }
        GL11.glVertex2d((double)(x + width), (double)(y + edgeRadius));
        GL11.glVertex2d((double)(x + width), (double)(y + height - edgeRadius));
        centerX = x + width - edgeRadius;
        centerY = y + height - edgeRadius;
        for (i = vertices; i >= 0; --i) {
            angleRadians = Math.PI * 2 * (double)i / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
        }
        GL11.glVertex2d((double)(x + width - edgeRadius), (double)(y + height));
        GL11.glVertex2d((double)(x + edgeRadius), (double)(y + height));
        centerX = x + edgeRadius;
        centerY = y + height - edgeRadius;
        i = vertices;
        while (true) {
            if (i < 0) {
                GL11.glVertex2d((double)x, (double)(y + height - edgeRadius));
                GL11.glVertex2d((double)x, (double)(y + edgeRadius));
                GL11.glEnd();
                RenderUtil.disableRender2D();
                return;
            }
            angleRadians = Math.PI * 2 * (double)(i + 270) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
            --i;
        }
    }

    public static void enableRender2D() {
        GL11.glEnable((int)3042);
        GL11.glDisable((int)2884);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glLineWidth((float)1.0f);
    }

    public static void disableRender2D() {
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2884);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)2848);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static Vec3 to2D(double x, double y, double z) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer((int)3);
        IntBuffer viewport = BufferUtils.createIntBuffer((int)16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer((int)16);
        FloatBuffer projection = BufferUtils.createFloatBuffer((int)16);
        GL11.glGetFloat((int)2982, (FloatBuffer)modelView);
        GL11.glGetFloat((int)2983, (FloatBuffer)projection);
        GL11.glGetInteger((int)2978, (IntBuffer)viewport);
        boolean result = GLU.gluProject((float)((float)x), (float)((float)y), (float)((float)z), (FloatBuffer)modelView, (FloatBuffer)projection, (IntBuffer)viewport, (FloatBuffer)screenCoords);
        if (!result) return null;
        return new Vec3((double)screenCoords.get(0), (double)((float)Display.getHeight() - screenCoords.get(1)), (double)screenCoords.get(2));
    }

    public static void drawTracerPointer(float x, float y, float size, float widthDiv, float heightDiv, int color) {
        boolean blend = GL11.glIsEnabled((int)3042);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glPushMatrix();
        RenderUtil.hexColor(color);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)(x - size / widthDiv), (double)(y + size));
        GL11.glVertex2d((double)x, (double)(y + size / heightDiv));
        GL11.glVertex2d((double)(x + size / widthDiv), (double)(y + size));
        GL11.glVertex2d((double)x, (double)y);
        GL11.glEnd();
        GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)0.8f);
        GL11.glBegin((int)2);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)(x - size / widthDiv), (double)(y + size));
        GL11.glVertex2d((double)x, (double)(y + size / heightDiv));
        GL11.glVertex2d((double)(x + size / widthDiv), (double)(y + size));
        GL11.glVertex2d((double)x, (double)y);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        if (!blend) {
            GL11.glDisable((int)3042);
        }
        GL11.glDisable((int)2848);
    }

    public static void hexColor(int hexColor) {
        float red = (float)(hexColor >> 16 & 0xFF) / 255.0f;
        float green = (float)(hexColor >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hexColor & 0xFF) / 255.0f;
        float alpha = (float)(hexColor >> 24 & 0xFF) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
    }

    public static double lerp(double x, double y, double z) {
        return (1.0 - z) * x + z * y;
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc((int)516, (float)((float)((double)limit * 0.01)));
    }

    public static void drawRectSized(float x, float y, float width, float height, int color) {
        RenderUtil.drawRect(x, y, x + width, y + height, color);
    }

    public static void resetColor() {
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static void bindTexture(int texture) {
        GL11.glBindTexture((int)3553, (int)texture);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer != null && framebuffer.framebufferWidth == RenderUtil.mc.displayWidth) {
            if (framebuffer.framebufferHeight == RenderUtil.mc.displayHeight) return framebuffer;
        }
        if (framebuffer == null) return new Framebuffer(RenderUtil.mc.displayWidth, RenderUtil.mc.displayHeight, true);
        framebuffer.deleteFramebuffer();
        return new Framebuffer(RenderUtil.mc.displayWidth, RenderUtil.mc.displayHeight, true);
    }

    public static void drawImg(ResourceLocation loc, double posX, double posY, double width, double height) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc((int)770, (int)771);
        Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
        float f = 1.0f / (float)width;
        float f1 = 1.0f / (float)height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(posX, posY + height, 0.0).tex((double)(0.0f * f), (double)((0.0f + (float)height) * f1)).endVertex();
        worldrenderer.pos(posX + width, posY + height, 0.0).tex((double)((0.0f + (float)width) * f), (double)((0.0f + (float)height) * f1)).endVertex();
        worldrenderer.pos(posX + width, posY, 0.0).tex((double)((0.0f + (float)width) * f), (double)(0.0f * f1)).endVertex();
        worldrenderer.pos(posX, posY, 0.0).tex((double)(0.0f * f), (double)(0.0f * f1)).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    public static void drawRoundedRect(float left, float top, float right, float bottom, int color) {
        RenderUtil.drawRect(left - 0.5f, top + 0.5f, left, bottom - 0.5f, color);
        RenderUtil.drawRect(right, top + 0.5f, right + 0.5f, bottom - 0.5f, color);
        RenderUtil.drawRect(left + 0.5f, top - 0.5f, right - 0.5f, top, color);
        RenderUtil.drawRect(left + 0.5f, bottom, right - 0.5f, bottom + 0.5f, color);
        RenderUtil.drawRect(left, top, right, bottom, color);
    }

    public static void drawBorderedRect(float left, float top, float right, float bottom, float thickness, int color) {
        RenderUtil.drawRect(left - thickness, top, left, bottom, color);
        RenderUtil.drawRect(right, top, right + thickness, bottom, color);
        RenderUtil.drawRect(left, top + thickness, right, top, color);
        RenderUtil.drawRect(left, bottom, right, bottom + thickness, color);
    }

    public static void drawImage(ResourceLocation image, float x, float y, float width, float height, float alpha) {
        GL11.glPushMatrix();
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDepthMask((boolean)false);
        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
        mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture((float)x, (float)y, (float)0.0f, (float)0.0f, (float)width, (float)height, (float)width, (float)height);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2929);
        GL11.glPopMatrix();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static void drawImage(ResourceLocation image, int x, int y, float width, float height, float alpha) {
        GL11.glPushMatrix();
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDepthMask((boolean)false);
        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
        GL11.glColor4f((float)1.0f, (float)0.0f, (float)0.0f, (float)alpha);
        mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture((int)x, (int)y, (float)0.0f, (float)0.0f, (int)((int)width), (int)((int)height), (float)width, (float)height);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2929);
        GL11.glPopMatrix();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static void drawImage(ResourceLocation image, float x, float y, float width, float height) {
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDepthMask((boolean)false);
        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture((float)x, (float)y, (float)0.0f, (float)0.0f, (float)width, (float)height, (float)width, (float)height);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2929);
    }

    public static int reAlpha(int color, float alpha) {
        try {
            Color c = new Color(color);
            float r = 0.003921569f * (float)c.getRed();
            float g = 0.003921569f * (float)c.getGreen();
            float b = 0.003921569f * (float)c.getBlue();
            return new Color(r, g, b, alpha).getRGB();
        }
        catch (Throwable e) {
            e.printStackTrace();
            return color;
        }
    }

    public static boolean isHovering(float mouseX, float mouseY, float xLeft, float yUp, float xRight, float yBottom) {
        return mouseX > xLeft && mouseX < xRight && mouseY > yUp && mouseY < yBottom;
    }

    public static boolean isHoveringBound(float mouseX, float mouseY, float xLeft, float yUp, float width, float height) {
        return mouseX > xLeft && mouseX < xLeft + width && mouseY > yUp && mouseY < yUp + height;
    }

    public static void drawRoundedRect(float x, float y, float right, float bottom, int borderC, int insideC) {
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
        RenderUtil.drawVLine(x *= 2.0f, (y *= 2.0f) + 1.0f, (bottom *= 2.0f) - 2.0f, borderC);
        RenderUtil.drawVLine((right *= 2.0f) - 1.0f, y + 1.0f, bottom - 2.0f, borderC);
        RenderUtil.drawHLine(x + 2.0f, right - 3.0f, y, borderC);
        RenderUtil.drawHLine(x + 2.0f, right - 3.0f, bottom - 1.0f, borderC);
        RenderUtil.drawHLine(x + 1.0f, x + 1.0f, y + 1.0f, borderC);
        RenderUtil.drawHLine(right - 2.0f, right - 2.0f, y + 1.0f, borderC);
        RenderUtil.drawHLine(right - 2.0f, right - 2.0f, bottom - 2.0f, borderC);
        RenderUtil.drawHLine(x + 1.0f, x + 1.0f, bottom - 2.0f, borderC);
        RenderUtil.drawRect(x + 1.0f, y + 1.0f, right - 1.0f, bottom - 1.0f, insideC);
        GL11.glScalef((float)2.0f, (float)2.0f, (float)2.0f);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glHint((int)3155, (int)4352);
    }

    public static void drawHLine(float x, float y, float right, int bottom) {
        if (y < x) {
            float var5 = x;
            x = y;
            y = var5;
        }
        RenderUtil.drawRect(x, right, y + 1.0f, right + 1.0f, bottom);
    }

    public static void drawVLine(float x, float y, float right, int bottom) {
        if (right < y) {
            float var5 = y;
            y = right;
            right = var5;
        }
        RenderUtil.drawRect(x, y + 1.0f, x + 1.0f, right, bottom);
    }

    public static void drawRect(float left, float top, float right, float bottom, int color) {
        float e;
        if (left < right) {
            e = left;
            left = right;
            right = e;
        }
        if (top < bottom) {
            e = top;
            top = bottom;
            bottom = e;
        }
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float b = (float)(color >> 16 & 0xFF) / 255.0f;
        float c = (float)(color >> 8 & 0xFF) / 255.0f;
        float d = (float)(color & 0xFF) / 255.0f;
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.color((float)b, (float)c, (float)d, (float)a);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos((double)left, (double)bottom, 0.0).endVertex();
        worldRenderer.pos((double)right, (double)bottom, 0.0).endVertex();
        worldRenderer.pos((double)right, (double)top, 0.0).endVertex();
        worldRenderer.pos((double)left, (double)top, 0.0).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static void drawOutlinedRect(float x, float y, float width, float height, float lineSize, int lineColor) {
        RenderUtil.drawRect(x, y, width, y + lineSize, lineColor);
        RenderUtil.drawRect(x, height - lineSize, width, height, lineColor);
        RenderUtil.drawRect(x, y + lineSize, x + lineSize, height - lineSize, lineColor);
        RenderUtil.drawRect(width - lineSize, y + lineSize, width, height - lineSize, lineColor);
    }

    public static void drawFastRoundedRect(int left, float top, int right, float bottom, float radius, int color) {
        int semicircle = 18;
        float f = 5.0f;
        GL11.glDisable((int)2884);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
        RenderUtil.color(color);
        GL11.glBegin((int)5);
        GL11.glVertex2f((float)((float)left + radius), (float)top);
        GL11.glVertex2f((float)((float)left + radius), (float)bottom);
        GL11.glVertex2f((float)((float)right - radius), (float)top);
        GL11.glVertex2f((float)((float)right - radius), (float)bottom);
        GL11.glEnd();
        GL11.glBegin((int)5);
        GL11.glVertex2f((float)left, (float)(top + radius));
        GL11.glVertex2f((float)((float)left + radius), (float)(top + radius));
        GL11.glVertex2f((float)left, (float)(bottom - radius));
        GL11.glVertex2f((float)((float)left + radius), (float)(bottom - radius));
        GL11.glEnd();
        GL11.glBegin((int)5);
        GL11.glVertex2f((float)right, (float)(top + radius));
        GL11.glVertex2f((float)((float)right - radius), (float)(top + radius));
        GL11.glVertex2f((float)right, (float)(bottom - radius));
        GL11.glVertex2f((float)((float)right - radius), (float)(bottom - radius));
        GL11.glEnd();
        GL11.glBegin((int)6);
        float f6 = (float)right - radius;
        float f7 = top + radius;
        GL11.glVertex2f((float)f6, (float)f7);
        int j = 0;
        for (j = 0; j <= 18; ++j) {
            float f8 = (float)j * 5.0f;
            GL11.glVertex2f((float)((float)((double)f6 + (double)radius * Math.cos(Math.toRadians(f8)))), (float)((float)((double)f7 - (double)radius * Math.sin(Math.toRadians(f8)))));
        }
        GL11.glEnd();
        GL11.glBegin((int)6);
        f6 = (float)left + radius;
        f7 = top + radius;
        GL11.glVertex2f((float)f6, (float)f7);
        for (j = 0; j <= 18; ++j) {
            float f9 = (float)j * 5.0f;
            GL11.glVertex2f((float)((float)((double)f6 - (double)radius * Math.cos(Math.toRadians(f9)))), (float)((float)((double)f7 - (double)radius * Math.sin(Math.toRadians(f9)))));
        }
        GL11.glEnd();
        GL11.glBegin((int)6);
        f6 = (float)left + radius;
        f7 = bottom - radius;
        GL11.glVertex2f((float)f6, (float)f7);
        for (j = 0; j <= 18; ++j) {
            float f10 = (float)j * 5.0f;
            GL11.glVertex2f((float)((float)((double)f6 - (double)radius * Math.cos(Math.toRadians(f10)))), (float)((float)((double)f7 + (double)radius * Math.sin(Math.toRadians(f10)))));
        }
        GL11.glEnd();
        GL11.glBegin((int)6);
        f6 = (float)right - radius;
        f7 = bottom - radius;
        GL11.glVertex2f((float)f6, (float)f7);
        j = 0;
        while (true) {
            if (j > 18) {
                GL11.glEnd();
                GL11.glEnable((int)3553);
                GL11.glEnable((int)2884);
                GL11.glDisable((int)3042);
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                return;
            }
            float f11 = (float)j * 5.0f;
            GL11.glVertex2f((float)((float)((double)f6 + (double)radius * Math.cos(Math.toRadians(f11)))), (float)((float)((double)f7 + (double)radius * Math.sin(Math.toRadians(f11)))));
            ++j;
        }
    }

    public static int width() {
        return new ScaledResolution(mc).getScaledWidth();
    }

    public static int height() {
        return new ScaledResolution(mc).getScaledHeight();
    }

    public static void drawRoundedRect(float x, float y, float x2, float y2, float round, int color) {
        x = (float)((double)x + ((double)(round / 2.0f) + 0.5));
        y = (float)((double)y + ((double)(round / 2.0f) + 0.5));
        x2 = (float)((double)x2 - ((double)(round / 2.0f) + 0.5));
        y2 = (float)((double)y2 - ((double)(round / 2.0f) + 0.5));
        RenderUtil.drawRect(x, y, x2, y2, color);
        RenderUtil.circle(x2 - round / 2.0f, y + round / 2.0f, round, color);
        RenderUtil.circle(x + round / 2.0f, y2 - round / 2.0f, round, color);
        RenderUtil.circle(x + round / 2.0f, y + round / 2.0f, round, color);
        RenderUtil.circle(x2 - round / 2.0f, y2 - round / 2.0f, round, color);
        RenderUtil.drawRect(x - round / 2.0f - 0.5f, y + round / 2.0f, x2, y2 - round / 2.0f, color);
        RenderUtil.drawRect(x, y + round / 2.0f, x2 + round / 2.0f + 0.5f, y2 - round / 2.0f, color);
        RenderUtil.drawRect(x + round / 2.0f, y - round / 2.0f - 0.5f, x2 - round / 2.0f, y2 - round / 2.0f, color);
        RenderUtil.drawRect(x + round / 2.0f, y, x2 - round / 2.0f, y2 + round / 2.0f + 0.5f, color);
    }

    public static void circle(float x, float y, float radius, int fill) {
        RenderUtil.arc(x, y, 0.0f, 360.0f, radius, fill);
    }

    public static void arc(float x, float y, float start, float end, float radius, int color) {
        RenderUtil.arcEllipse(x, y, start, end, radius, radius, color);
    }

    public static void color(int color) {
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
    }

    public static void arcEllipse(float x, float y, float start, float end, float w, float h, int color) {
        float ldy;
        float ldx;
        float i;
        GlStateManager.color((float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        float temp = 0.0f;
        if (start > end) {
            temp = end;
            end = start;
            start = temp;
        }
        float var11 = (float)(color >> 24 & 0xFF) / 255.0f;
        float var6 = (float)(color >> 16 & 0xFF) / 255.0f;
        float var7 = (float)(color >> 8 & 0xFF) / 255.0f;
        float var8 = (float)(color & 0xFF) / 255.0f;
        Tessellator var9 = Tessellator.getInstance();
        WorldRenderer var10 = var9.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.color((float)var6, (float)var7, (float)var8, (float)var11);
        if (var11 > 0.5f) {
            GL11.glEnable((int)2848);
            GL11.glLineWidth((float)2.0f);
            GL11.glBegin((int)3);
            for (i = end; i >= start; i -= 4.0f) {
                ldx = (float)Math.cos((double)i * Math.PI / 180.0) * (w * 1.001f);
                ldy = (float)Math.sin((double)i * Math.PI / 180.0) * (h * 1.001f);
                GL11.glVertex2f((float)(x + ldx), (float)(y + ldy));
            }
            GL11.glEnd();
            GL11.glDisable((int)2848);
        }
        GL11.glBegin((int)6);
        i = end;
        while (true) {
            if (!(i >= start)) {
                GL11.glEnd();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                return;
            }
            ldx = (float)Math.cos((double)i * Math.PI / 180.0) * w;
            ldy = (float)Math.sin((double)i * Math.PI / 180.0) * h;
            GL11.glVertex2f((float)(x + ldx), (float)(y + ldy));
            i -= 4.0f;
        }
    }

    public static void drawCircle(float x, float y, int start, int end, float radius, int color) {
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glEnable((int)2881);
        GL11.glBlendFunc((int)770, (int)771);
        RenderUtil.color(color);
        GL11.glBegin((int)9);
        int i = start;
        while (true) {
            if (i > end) {
                GL11.glEnd();
                GL11.glDisable((int)2881);
                GL11.glEnable((int)3553);
                GL11.glDisable((int)3042);
                return;
            }
            double x2 = Math.sin((double)i * Math.PI / 180.0) * (double)radius;
            double y2 = Math.cos((double)i * Math.PI / 180.0) * (double)radius;
            GL11.glVertex3d((double)((double)x + x2), (double)((double)y + y2), (double)0.0);
            ++i;
        }
    }

    public static void drawCircleWithTexture(float cX, float cY, int start, int end, float radius, ResourceLocation res, int color) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3553);
        mc.getTextureManager().bindTexture(res);
        GL11.glEnable((int)2881);
        GL11.glBlendFunc((int)770, (int)771);
        RenderUtil.color(color);
        GL11.glBegin((int)9);
        int i = start;
        while (true) {
            if (i >= end) {
                GL11.glEnd();
                GL11.glDisable((int)2881);
                GL11.glDisable((int)3553);
                GL11.glPopMatrix();
                return;
            }
            double radian = (double)i * (Math.PI / 180);
            double xsin = Math.sin(radian);
            double ycos = Math.cos(radian);
            double x = xsin * (double)radius;
            double y = ycos * (double)radius;
            double tx = xsin * 0.5 + 0.5;
            double ty = ycos * 0.5 + 0.5;
            GL11.glTexCoord2d((double)((double)cX + tx), (double)((double)cY + ty));
            GL11.glVertex2d((double)((double)cX + x), (double)((double)cY + y));
            ++i;
        }
    }

    public static void layeredRect(float right, float bottom, float x2, float y2, int outline, int inline, int background) {
        RenderUtil.drawRect(right, bottom, x2, y2, outline);
        RenderUtil.drawRect(right + 0.5f, bottom + 0.5f, x2 - 0.5f, y2 - 0.5f, inline);
        RenderUtil.drawRect(right + 1.0f, bottom + 1.0f, x2 - 1.0f, y2 - 1.0f, background);
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glLineWidth((float)1.0f);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)1.0f);
        RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        RenderUtil.drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawEntityESP(double x, double y, double z, double x1, double y1, double z1, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glLineWidth((float)1.0f);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)1.0f);
        RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x1, y1, z1));
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x1, y1, z1));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawEntityESP(AxisAlignedBB axisAlignedBB, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glLineWidth((float)1.0f);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)1.0f);
        RenderUtil.drawOutlinedBoundingBox(axisAlignedBB);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        RenderUtil.drawBoundingBox(axisAlignedBB);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void doGlScissor(float x, float y, float windowWidth2, float windowHeight2) {
        int scaleFactor = 1;
        float k = RenderUtil.mc.gameSettings.guiScale;
        if (k == 0.0f) {
            k = 1000.0f;
        }
        while ((float)scaleFactor < k && RenderUtil.mc.displayWidth / (scaleFactor + 1) >= 320 && RenderUtil.mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor((int)((int)(x * (float)scaleFactor)), (int)((int)((float)RenderUtil.mc.displayHeight - (y + windowHeight2) * (float)scaleFactor)), (int)((int)(windowWidth2 * (float)scaleFactor)), (int)((int)(windowHeight2 * (float)scaleFactor)));
    }

    public static void drawRect2(float left, float top, float right, float bottom, int col1) {
        float f = (float)(col1 >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(col1 >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(col1 >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(col1 & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glPushMatrix();
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)right, (double)top);
        GL11.glVertex2d((double)left, (double)top);
        GL11.glVertex2d((double)left, (double)bottom);
        GL11.glVertex2d((double)right, (double)bottom);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static int darker(int hexColor, int factor) {
        float alpha = hexColor >> 24 & 0xFF;
        float red = Math.max((float)(hexColor >> 16 & 0xFF) - (float)(hexColor >> 16 & 0xFF) / (100.0f / (float)factor), 0.0f);
        float green = Math.max((float)(hexColor >> 8 & 0xFF) - (float)(hexColor >> 8 & 0xFF) / (100.0f / (float)factor), 0.0f);
        float blue = Math.max((float)(hexColor & 0xFF) - (float)(hexColor & 0xFF) / (100.0f / (float)factor), 0.0f);
        return (int)((float)(((int)alpha << 24) + ((int)red << 16) + ((int)green << 8)) + blue);
    }

    public static void drawBorderedRect(float x, float y, float x2, float y2, float l1, int col1, int col2) {
        RenderUtil.drawRect(x, y, x2, y2, col2);
        float f = (float)(col1 >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(col1 >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(col1 >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(col1 & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glPushMatrix();
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
        GL11.glLineWidth((float)l1);
        GL11.glBegin((int)1);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x, (double)y2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glVertex2d((double)x2, (double)y);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x2, (double)y);
        GL11.glVertex2d((double)x, (double)y2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)255.0f);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
    }
}
