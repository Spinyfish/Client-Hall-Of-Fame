/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.storage.utils.gui.clickgui.AbstractGuiScreen
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.storage.utils.render.render;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.storage.utils.gui.clickgui.AbstractGuiScreen;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class GuiRenderUtils {
    public static Minecraft mc = Minecraft.getMinecraft();
    private static float scissorX;
    private static float scissorY;
    private static float scissorWidth;
    private static float scissorHeight;
    private static float scissorSF;
    private static boolean isScissoring;

    public static float[] getScissor() {
        if (!isScissoring) return new float[]{-1.0f};
        return new float[]{scissorX, scissorY, scissorWidth, scissorHeight, scissorSF};
    }

    public static void beginCrop(float x, float y, float width, float height) {
        float scaleFactor = GuiRenderUtils.getScaleFactor();
        GL11.glEnable((int)3089);
        GL11.glScissor((int)((int)(x * scaleFactor)), (int)((int)((float)Display.getHeight() - y * scaleFactor)), (int)((int)(width * scaleFactor)), (int)((int)(height * scaleFactor)));
        isScissoring = true;
        scissorX = x;
        scissorY = y;
        scissorWidth = width;
        scissorHeight = height;
        scissorSF = scaleFactor;
    }

    public static void beginCropFixed(float x, float y, float width, float height) {
        float scaleFactor = GuiRenderUtils.getScaleFactor();
        GL11.glEnable((int)3089);
        GL11.glScissor((int)((int)(x * scaleFactor)), (int)((int)((float)Display.getHeight() - y * scaleFactor)), (int)((int)(width * scaleFactor)), (int)((int)(height * scaleFactor)));
        isScissoring = true;
        scissorX = x;
        scissorY = y;
        scissorWidth = width;
        scissorHeight = height;
        scissorSF = scaleFactor;
    }

    public static void beginCrop(float x, float y, float width, float height, float scaleFactor) {
        GL11.glEnable((int)3089);
        GL11.glScissor((int)((int)(x * scaleFactor)), (int)((int)((float)Display.getHeight() - y * scaleFactor)), (int)((int)(width * scaleFactor)), (int)((int)(height * scaleFactor)));
        isScissoring = true;
        scissorX = x;
        scissorY = y;
        scissorWidth = width;
        scissorHeight = height;
        scissorSF = scaleFactor;
    }

    public static void endCrop() {
        GL11.glDisable((int)3089);
        isScissoring = false;
    }

    public static void drawImageSpread(ResourceLocation image, float x, float y, float width, float height, float alpha) {
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GL11.glDepthMask((boolean)false);
        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
        mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture((float)x, (float)y, (float)0.0f, (float)0.0f, (float)width, (float)height, (float)25.0f, (float)25.0f);
        GL11.glDepthMask((boolean)true);
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.resetColor();
    }

    public static void doGlScissor(int x, int y, float width, float height, float scale) {
        int scaleFactor = 1;
        while ((float)scaleFactor < scale && GuiRenderUtils.mc.displayWidth / (scaleFactor + 1) >= 320 && GuiRenderUtils.mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor((int)(x * scaleFactor), (int)((int)((float)GuiRenderUtils.mc.displayHeight - ((float)y + height) * (float)scaleFactor)), (int)((int)(width * (float)scaleFactor)), (int)((int)(height * (float)scaleFactor)));
    }

    public static void drawLine3D(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        GuiRenderUtils.drawLine3D(x1, y1, z1, x2, y2, z2, color, true);
    }

    public static void drawLine3D(double x1, double y1, double z1, double x2, double y2, double z2, int color, boolean disableDepth) {
        GuiRenderUtils.enableRender3D(disableDepth);
        GuiRenderUtils.setColor(color);
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)x1, (double)y1, (double)z1);
        GL11.glVertex3d((double)x2, (double)y2, (double)z2);
        GL11.glEnd();
        GuiRenderUtils.disableRender3D(disableDepth);
    }

    public static void drawLine2D(double x1, double y1, double x2, double y2, float width, int color) {
        GuiRenderUtils.enableRender2D();
        GuiRenderUtils.setColor(color);
        GL11.glLineWidth((float)width);
        GL11.glBegin((int)1);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GuiRenderUtils.disableRender2D();
    }

    public static void drawPoint(int x, int y, float size, int color) {
        GuiRenderUtils.enableRender2D();
        GuiRenderUtils.setColor(color);
        GL11.glPointSize((float)size);
        GL11.glEnable((int)2832);
        GL11.glBegin((int)0);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glEnd();
        GL11.glDisable((int)2832);
        GuiRenderUtils.disableRender2D();
    }

    public static float getScaleFactor() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        return scaledResolution.getScaleFactor();
    }

    public static float getScaleFactorForAbstractGuiScreen() {
        return GuiRenderUtils.mc.currentScreen instanceof AbstractGuiScreen ? ((AbstractGuiScreen)GuiRenderUtils.mc.currentScreen).scale : 2.0f;
    }

    public static void drawOutlinedBox(AxisAlignedBB boundingBox, int color) {
        GuiRenderUtils.drawOutlinedBox(boundingBox, color, true);
    }

    public static void drawOutlinedBox(AxisAlignedBB boundingBox, int color, boolean disableDepth) {
        if (boundingBox == null) return;
        GuiRenderUtils.enableRender3D(disableDepth);
        GuiRenderUtils.setColor(color);
        GL11.glBegin((int)3);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)3);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GuiRenderUtils.disableRender3D(disableDepth);
    }

    public static void drawBox(AxisAlignedBB boundingBox, int color) {
        GuiRenderUtils.drawBox(boundingBox, color, true);
    }

    public static void drawBox(AxisAlignedBB boundingBox, int color, boolean disableDepth) {
        if (boundingBox == null) return;
        GuiRenderUtils.enableRender3D(disableDepth);
        GuiRenderUtils.setColor(color);
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GuiRenderUtils.disableRender3D(disableDepth);
    }

    public static void enableRender3D(boolean disableDepth) {
        if (disableDepth) {
            GL11.glDepthMask((boolean)false);
            GL11.glDisable((int)2929);
        }
        GL11.glDisable((int)3008);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)1.0f);
    }

    public static void disableRender3D(boolean enableDepth) {
        if (enableDepth) {
            GL11.glDepthMask((boolean)true);
            GL11.glEnable((int)2929);
        }
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3008);
        GL11.glDisable((int)2848);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
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

    public static void setColor(int colorHex) {
        float alpha = (float)(colorHex >> 24 & 0xFF) / 255.0f;
        float red = (float)(colorHex >> 16 & 0xFF) / 255.0f;
        float green = (float)(colorHex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(colorHex & 0xFF) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
    }

    public static void drawBorderedRect(float x, float y, float width, float height, float borderWidth, Color rectColor, Color borderColor) {
        GuiRenderUtils.drawBorderedRect(x, y, width, height, borderWidth, rectColor.getRGB(), borderColor.getRGB());
    }

    public static void drawBorderedRect(float x, float y, float width, float height, float borderWidth, int rectColor, int borderColor) {
        GuiRenderUtils.drawRect(x + borderWidth, y + borderWidth, width - borderWidth * 2.0f, height - borderWidth * 2.0f, rectColor);
        GuiRenderUtils.drawRect(x, y, width, borderWidth, borderColor);
        GuiRenderUtils.drawRect(x, y + borderWidth, borderWidth, height - borderWidth, borderColor);
        GuiRenderUtils.drawRect(x + width - borderWidth, y + borderWidth, borderWidth, height - borderWidth, borderColor);
        GuiRenderUtils.drawRect(x + borderWidth, y + height - borderWidth, width - borderWidth * 2.0f, borderWidth, borderColor);
    }

    public static void drawBorder(float x, float y, float width, float height, float borderWidth, int borderColor) {
        GuiRenderUtils.drawRect(x + borderWidth, y + borderWidth, width - borderWidth * 2.0f, borderWidth, borderColor);
        GuiRenderUtils.drawRect(x, y + borderWidth, borderWidth, height - borderWidth, borderColor);
        GuiRenderUtils.drawRect(x + width - borderWidth, y + borderWidth, borderWidth, height - borderWidth, borderColor);
        GuiRenderUtils.drawRect(x + borderWidth, y + height - borderWidth, width - borderWidth * 2.0f, borderWidth, borderColor);
    }

    public static void drawRect(float x, float y, float width, float height, Color color) {
        GuiRenderUtils.drawRect(x, y, width, height, color.getRGB());
    }

    public static void drawRect(float x, float y, float width, float height, int color) {
        GuiRenderUtils.enableRender2D();
        GuiRenderUtils.setColor(color);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glVertex2d((double)(x + width), (double)(y + height));
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glEnd();
        GuiRenderUtils.disableRender2D();
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
        GuiRenderUtils.drawRect(x + edgeRadius, y + edgeRadius, width - edgeRadius * 2.0f, height - edgeRadius * 2.0f, color);
        GuiRenderUtils.drawRect(x + edgeRadius, y, width - edgeRadius * 2.0f, edgeRadius, color);
        GuiRenderUtils.drawRect(x + edgeRadius, y + height - edgeRadius, width - edgeRadius * 2.0f, edgeRadius, color);
        GuiRenderUtils.drawRect(x, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0f, color);
        GuiRenderUtils.drawRect(x + width - edgeRadius, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0f, color);
        GuiRenderUtils.enableRender2D();
        RenderUtil.color((int)color);
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
        RenderUtil.color((int)borderColor);
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
                GuiRenderUtils.disableRender2D();
                return;
            }
            angleRadians = Math.PI * 2 * (double)(i + 270) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
            --i;
        }
    }

    public static int getDisplayWidth() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int displayWidth = scaledResolution.getScaledWidth();
        return displayWidth;
    }

    public static int getDisplayHeight() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int displayHeight = scaledResolution.getScaledHeight();
        return displayHeight;
    }

    public static void drawCircle(float x, float y, float radius, float lineWidth, int color) {
        GuiRenderUtils.enableRender2D();
        GuiRenderUtils.setColor(color);
        GL11.glLineWidth((float)lineWidth);
        int vertices = (int)Math.min(Math.max(radius, 45.0f), 360.0f);
        GL11.glBegin((int)2);
        int i = 0;
        while (true) {
            if (i >= vertices) {
                GL11.glEnd();
                GuiRenderUtils.disableRender2D();
                return;
            }
            double angleRadians = Math.PI * 2 * (double)i / (double)vertices;
            GL11.glVertex2d((double)((double)x + Math.sin(angleRadians) * (double)radius), (double)((double)y + Math.cos(angleRadians) * (double)radius));
            ++i;
        }
    }

    public static void drawFilledCircle(float x, float y, float radius, int color) {
        GuiRenderUtils.enableRender2D();
        GuiRenderUtils.setColor(color);
        int vertices = (int)Math.min(Math.max(radius, 45.0f), 360.0f);
        GL11.glBegin((int)9);
        int i = 0;
        while (true) {
            if (i >= vertices) {
                GL11.glEnd();
                GuiRenderUtils.disableRender2D();
                GuiRenderUtils.drawCircle(x, y, radius, 1.5f, 0xFFFFFF);
                return;
            }
            double angleRadians = Math.PI * 2 * (double)i / (double)vertices;
            GL11.glVertex2d((double)((double)x + Math.sin(angleRadians) * (double)radius), (double)((double)y + Math.cos(angleRadians) * (double)radius));
            ++i;
        }
    }

    public static void drawFilledCircleNoBorder(float x, float y, float radius, int color) {
        GuiRenderUtils.enableRender2D();
        GuiRenderUtils.setColor(color);
        int vertices = (int)Math.min(Math.max(radius, 45.0f), 360.0f);
        GL11.glBegin((int)9);
        int i = 0;
        while (true) {
            if (i >= vertices) {
                GL11.glEnd();
                GuiRenderUtils.disableRender2D();
                return;
            }
            double angleRadians = Math.PI * 2 * (double)i / (double)vertices;
            GL11.glVertex2d((double)((double)x + Math.sin(angleRadians) * (double)radius), (double)((double)y + Math.cos(angleRadians) * (double)radius));
            ++i;
        }
    }

    public static int darker(int hexColor, int factor) {
        float alpha = hexColor >> 24 & 0xFF;
        float red = Math.max((float)(hexColor >> 16 & 0xFF) - (float)(hexColor >> 16 & 0xFF) / (100.0f / (float)factor), 0.0f);
        float green = Math.max((float)(hexColor >> 8 & 0xFF) - (float)(hexColor >> 8 & 0xFF) / (100.0f / (float)factor), 0.0f);
        float blue = Math.max((float)(hexColor & 0xFF) - (float)(hexColor & 0xFF) / (100.0f / (float)factor), 0.0f);
        return (int)((float)(((int)alpha << 24) + ((int)red << 16) + ((int)green << 8)) + blue);
    }

    public static int opacity(int hexColor, int factor) {
        float alpha = Math.max((float)(hexColor >> 24 & 0xFF) - (float)(hexColor >> 24 & 0xFF) / (100.0f / (float)factor), 0.0f);
        float red = hexColor >> 16 & 0xFF;
        float green = hexColor >> 8 & 0xFF;
        float blue = hexColor & 0xFF;
        return (int)((float)(((int)alpha << 24) + ((int)red << 16) + ((int)green << 8)) + blue);
    }
}
