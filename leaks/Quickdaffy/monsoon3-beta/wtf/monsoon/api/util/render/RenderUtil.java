/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 */
package wtf.monsoon.api.util.render;

import java.awt.Color;
import me.surge.animation.Animation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.api.util.shader.Shader;
import wtf.monsoon.impl.module.hud.HUD;

public class RenderUtil {
    static Shader rectShader = new Shader(new ResourceLocation("monsoon/shader/rect.frag"));
    static Shader gradientShader = new Shader(new ResourceLocation("monsoon/shader/gradientRect.frag"));
    static Minecraft mc = Minecraft.getMinecraft();

    public static void renderItem(ItemStack stack, float x, float y, float size) {
        RenderItem ri = mc.getRenderItem();
        ri.renderItemAndEffectIntoGUI(stack, (int)x, (int)y);
    }

    public static void arrow(float centerX, float centerY, float sharpness, float size, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.field_181705_e);
        worldrenderer.pos(centerX, centerY - size - sharpness, 0.0).end();
        worldrenderer.pos(centerX + size, centerY + size, 0.0).end();
        worldrenderer.pos(centerX, centerY + size, 0.0).end();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void rect(float x, float y, float width, float height, Color color) {
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        ColorUtil.glColor(color.getRGB());
        GL11.glBegin((int)7);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glVertex2f((float)x, (float)(y + height));
        GL11.glVertex2f((float)(x + width), (float)(y + height));
        GL11.glVertex2f((float)(x + width), (float)y);
        GL11.glEnd();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
    }

    public static void renderTexture(float x, float y, float width, float height) {
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GL11.glDisable((int)3042);
    }

    public static void hollowRect(float x, float y, float w, float h, float width, Color color) {
        RenderUtil.rect(x, w, y, width, color);
        RenderUtil.rect(x, w, h, width, color);
        RenderUtil.rect(x, h, y, width, color);
        RenderUtil.rect(w, h, y, width, color);
    }

    public static void gradient(float x, float y, float width, float height, Color color1, Color color2, Color color3, Color color4, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        rectShader.init();
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)rectShader.getProgram(), (CharSequence)"color1"), (float)((float)color1.getRed() / 255.0f), (float)((float)color1.getGreen() / 255.0f), (float)((float)color1.getBlue() / 255.0f), (float)alpha);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)rectShader.getProgram(), (CharSequence)"color2"), (float)((float)color2.getRed() / 255.0f), (float)((float)color2.getGreen() / 255.0f), (float)((float)color2.getBlue() / 255.0f), (float)alpha);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)rectShader.getProgram(), (CharSequence)"color3"), (float)((float)color3.getRed() / 255.0f), (float)((float)color3.getGreen() / 255.0f), (float)((float)color3.getBlue() / 255.0f), (float)alpha);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)rectShader.getProgram(), (CharSequence)"color4"), (float)((float)color4.getRed() / 255.0f), (float)((float)color4.getGreen() / 255.0f), (float)((float)color4.getBlue() / 255.0f), (float)alpha);
        rectShader.bind(x, y, height, width);
        rectShader.finish();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }

    public static void verticalGradient(float x, float y, float width, float height, Color top, Color bottom) {
        GL11.glPushMatrix();
        GL11.glDisable((int)3553);
        GL11.glDisable((int)3008);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glShadeModel((int)7425);
        GL11.glBegin((int)7);
        ColorUtil.glColor(top.getRGB());
        GL11.glVertex2f((float)x, (float)y);
        ColorUtil.glColor(bottom.getRGB());
        GL11.glVertex2f((float)x, (float)(y + height));
        GL11.glVertex2f((float)(x + width), (float)(y + height));
        ColorUtil.glColor(top.getRGB());
        GL11.glVertex2f((float)(x + width), (float)y);
        GL11.glEnd();
        GL11.glShadeModel((int)7424);
        GL11.glEnable((int)3008);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3553);
        GL11.glPopMatrix();
    }

    public static void drawCircle(Entity entity, float partialTicks, double rad, Color color, float linewidth, double point) {
        GL11.glPushMatrix();
        GL11.glDisable((int)3553);
        RenderUtil.startSmooth();
        GL11.glLineWidth((float)linewidth);
        GL11.glBegin((int)3);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks - RenderUtil.mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks - RenderUtil.mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks - RenderUtil.mc.getRenderManager().viewerPosZ;
        int r = color.getRGB() >> 16 & 0xFF;
        int g = color.getRGB() >> 8 & 0xFF;
        int b = color.getRGB() & 0xFF;
        int a = color.getRGB() >> 24 & 0xFF;
        double pix2 = Math.PI * point;
        for (int i = 0; i <= 90; ++i) {
            GL11.glColor4f((float)((float)r / 255.0f), (float)((float)g / 255.0f), (float)((float)b / 255.0f), (float)((float)a / 255.0f));
            GL11.glVertex3d((double)(x + rad * Math.cos((double)i * pix2 / 45.0)), (double)y, (double)(z + rad * Math.sin((double)i * pix2 / 45.0)));
        }
        GL11.glEnd();
        RenderUtil.endSmooth();
        GL11.glEnable((int)3553);
        GL11.glPopMatrix();
    }

    public static void drawCircleWithY(Entity entity, float partialTicks, double rad, Color color, float linewidth, double point, double yPos) {
        GL11.glPushMatrix();
        GL11.glDisable((int)3553);
        RenderUtil.startSmooth();
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glLineWidth((float)linewidth);
        GL11.glBegin((int)3);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks - RenderUtil.mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks - RenderUtil.mc.getRenderManager().viewerPosY + yPos;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks - RenderUtil.mc.getRenderManager().viewerPosZ;
        float r = 0.003921569f * (float)color.getRed();
        float g = 0.003921569f * (float)color.getGreen();
        float b = 0.003921569f * (float)color.getBlue();
        double pix2 = Math.PI * point;
        for (int i = 0; i <= 90; ++i) {
            GL11.glColor3f((float)r, (float)g, (float)b);
            GL11.glVertex3d((double)(x + rad * Math.cos((double)i * pix2 / 45.0)), (double)y, (double)(z + rad * Math.sin((double)i * pix2 / 45.0)));
        }
        GL11.glEnd();
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2929);
        RenderUtil.endSmooth();
        GL11.glEnable((int)3553);
        GL11.glPopMatrix();
    }

    public static void startSmooth() {
        GL11.glEnable((int)2848);
        GL11.glEnable((int)2881);
        GL11.glEnable((int)2832);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
        GL11.glHint((int)3153, (int)4354);
    }

    public static void endSmooth() {
        GL11.glDisable((int)2848);
        GL11.glDisable((int)2881);
        GL11.glEnable((int)2832);
    }

    public static void drawRect(double x, double y, double width, double height, int color) {
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.field_181705_e);
        worldrenderer.pos(x, y + height, 0.0).end();
        worldrenderer.pos(x + width, y + height, 0.0).end();
        worldrenderer.pos(x + width, y, 0.0).end();
        worldrenderer.pos(x, y, 0.0).end();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawGradientRect(float x, float y, float width, float height, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x + width, y, 0.0).func_181666_a(f1, f2, f3, f).end();
        worldrenderer.pos(x, y, 0.0).func_181666_a(f1, f2, f3, f).end();
        worldrenderer.pos(x, y + height, 0.0).func_181666_a(f5, f6, f7, f4).end();
        worldrenderer.pos(x + width, y + height, 0.0).func_181666_a(f5, f6, f7, f4).end();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void pushScissor(double x, double y, double width, double height) {
        width = MathHelper.clamp_double(width, 0.0, width);
        height = MathHelper.clamp_double(height, 0.0, height);
        GL11.glPushAttrib((int)524288);
        RenderUtil.scissorRect(x, y, width, height);
        GL11.glEnable((int)3089);
    }

    public static void scissorRect(double x, double y, double width, double height) {
        ScaledResolution sr = new ScaledResolution(Wrapper.getMinecraft());
        double scale = sr.getScaleFactor();
        y = (double)sr.getScaledHeight() - y;
        GL11.glScissor((int)((int)(x *= scale)), (int)((int)((y *= scale) - (height *= scale))), (int)((int)(width *= scale)), (int)((int)height));
    }

    public static void popScissor() {
        GL11.glDisable((int)3089);
        GL11.glPopAttrib();
    }

    public static void scale(float x, float y, float[] scale, Runnable block) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0f);
        GlStateManager.scale(scale[0], scale[1], 1.0f);
        GlStateManager.translate(-x, -y, 0.0f);
        block.run();
        GlStateManager.popMatrix();
    }

    public static void scaleXY(float x, float y, Animation anim, Runnable block) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0f);
        GlStateManager.scale(anim.getAnimationFactor(), anim.getAnimationFactor(), 1.0);
        GlStateManager.translate(-x, -y, 0.0f);
        block.run();
        GlStateManager.popMatrix();
    }

    public static void scaleX(float x, float y, Animation anim, Runnable block) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0f);
        GlStateManager.scale(anim.getAnimationFactor(), 1.0, 1.0);
        GlStateManager.translate(-x, -y, 0.0f);
        block.run();
        GlStateManager.popMatrix();
    }

    public static void scaleY(float x, float y, Animation anim, Runnable block) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0f);
        GlStateManager.scale(1.0, anim.getAnimationFactor(), 1.0);
        GlStateManager.translate(-x, -y, 0.0f);
        block.run();
        GlStateManager.popMatrix();
    }

    public static void scale(float x, float y, float[] scale) {
        GlStateManager.translate(x, y, 0.0f);
        GlStateManager.scale(scale[0], scale[1], 1.0f);
        GlStateManager.translate(-x, -y, 0.0f);
    }

    public static void rotate(float x, float y, double rotate, Runnable block) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0f);
        GL11.glRotated((double)rotate, (double)0.0, (double)0.0, (double)-1.0);
        GlStateManager.translate(-x, -y, 0.0f);
        block.run();
        GlStateManager.popMatrix();
    }

    public static void getDefaultHudRenderer(HUDModule module) {
        RenderUtil.getDefaultHudRenderer(module.getX(), module.getY(), module.getWidth(), module.getHeight());
    }

    public static void getDefaultHudRenderer(float x, float y, float w, float h) {
        HUD hudInstance = Wrapper.getModule(HUD.class);
        boolean outline = hudInstance.hudModuleOutline.getValue();
        boolean shadow = hudInstance.hudModuleShadow.getValue();
        boolean background = hudInstance.hudModuleBackground.getValue();
        float radius = 10.0f;
        if (shadow) {
            RoundedUtils.shadowGradient(x, y, w, h, radius, 10.0f, 2.0f, ColorUtil.getAccent()[0], ColorUtil.getAccent()[1], ColorUtil.getAccent()[2], ColorUtil.getAccent()[3], false);
        }
        if (background) {
            RoundedUtils.round(x, y, w, h, radius, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.2f));
        }
        if (outline) {
            RoundedUtils.outline(x, y, w, h, radius, 2.0f, 2.0f, ColorUtil.getAccent()[0], ColorUtil.getAccent()[1], ColorUtil.getAccent()[2], ColorUtil.getAccent()[3]);
        }
    }
}

