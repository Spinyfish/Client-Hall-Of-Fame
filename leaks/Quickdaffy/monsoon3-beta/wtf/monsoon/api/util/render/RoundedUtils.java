/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 */
package wtf.monsoon.api.util.render;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.util.Util;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.shader.Shader;

public class RoundedUtils
extends Util {
    private static final Shader rectShader = new Shader(new ResourceLocation("monsoon/shader/roundedRect.frag"));
    private static final Shader outlineShader = new Shader(new ResourceLocation("monsoon/shader/roundedOutline.frag"));
    private static final Shader outlineGradientShader = new Shader(new ResourceLocation("monsoon/shader/roundedGradientOutline.frag"));
    private static final Shader textureShader = new Shader(new ResourceLocation("monsoon/shader/texture.frag"));
    private static final Shader gradientShader = new Shader(new ResourceLocation("monsoon/shader/roundedGradientRect.frag"));
    private static final Shader shadowShader = new Shader(new ResourceLocation("monsoon/shader/shadowRect.frag"));
    private static final Shader shadowGradientShader = new Shader(new ResourceLocation("monsoon/shader/shadowGradient.frag"));
    private static final Shader testShader = new Shader(new ResourceLocation("monsoon/shader/test.frag"));

    public static void glRound(float x, float y, float width, float height, float radius, int color1, int color2, int color3, int color4) {
        int rad;
        GL11.glShadeModel((int)7425);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glHint((int)3155, (int)4354);
        GL11.glBegin((int)9);
        ColorUtil.glColor(color1);
        for (rad = 0; rad <= 90; ++rad) {
            GL11.glVertex2d((double)((double)(x + radius) + Math.sin((double)rad * Math.PI / 180.0) * (double)radius * -1.0), (double)((double)(y + radius) + Math.cos((double)rad * Math.PI / 180.0) * (double)radius * -1.0));
        }
        ColorUtil.glColor(color2);
        for (rad = 90; rad <= 180; ++rad) {
            GL11.glVertex2d((double)((double)(x + radius) + Math.sin((double)rad * Math.PI / 180.0) * (double)radius * -1.0), (double)((double)(y + height - radius) + Math.cos((double)rad * Math.PI / 180.0) * (double)radius * -1.0));
        }
        ColorUtil.glColor(color3);
        for (rad = 0; rad <= 90; ++rad) {
            GL11.glVertex2d((double)((double)(x + width - radius) + Math.sin((double)rad * Math.PI / 180.0) * (double)radius), (double)((double)(y + height - radius) + Math.cos((double)rad * Math.PI / 180.0) * (double)radius));
        }
        ColorUtil.glColor(color4);
        for (rad = 90; rad <= 180; ++rad) {
            GL11.glVertex2d((double)((double)(x + width - radius) + Math.sin((double)rad * Math.PI / 180.0) * (double)radius), (double)((double)(y + radius) + Math.cos((double)rad * Math.PI / 180.0) * (double)radius));
        }
        GL11.glEnd();
        GL11.glEnable((int)3553);
        GL11.glShadeModel((int)7424);
        GlStateManager.resetColor();
    }

    public static void glRound(HUDModule parent, float radius, Color color) {
        RoundedUtils.glRound(parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight(), radius, color.getRGB());
    }

    public static void glRound(float x, float y, float width, float height, float radius, int color) {
        RoundedUtils.glRound(x, y, width, height, radius, color, color, color, color);
    }

    public static void circle(float x, float y, float radius, Color color) {
        RoundedUtils.round(x - radius, y - radius, radius * 2.0f, radius * 2.0f, radius - 1.0f, color);
    }

    public static void gradient(float x, float y, float width, float height, float radius, float opacity, Color[] colours) {
        RoundedUtils.gradient(x, y, width, height, radius, opacity, colours[0], colours[1], colours[2], colours[3]);
    }

    public static void gradient(float x, float y, float width, float height, float radius, float opacity, Color c1, Color c2, Color c3, Color c4) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL20.glUseProgram((int)gradientShader.getProgram());
        GL20.glUniform2f((int)GL20.glGetUniformLocation((int)gradientShader.getProgram(), (CharSequence)"size"), (float)width, (float)height);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)gradientShader.getProgram(), (CharSequence)"radius"), (float)radius);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)gradientShader.getProgram(), (CharSequence)"alpha"), (float)opacity);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)gradientShader.getProgram(), (CharSequence)"color1"), (float)((float)c1.getRed() / 255.0f), (float)((float)c1.getGreen() / 255.0f), (float)((float)c1.getBlue() / 255.0f), (float)1.0f);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)gradientShader.getProgram(), (CharSequence)"color2"), (float)((float)c2.getRed() / 255.0f), (float)((float)c2.getGreen() / 255.0f), (float)((float)c2.getBlue() / 255.0f), (float)1.0f);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)gradientShader.getProgram(), (CharSequence)"color3"), (float)((float)c3.getRed() / 255.0f), (float)((float)c3.getGreen() / 255.0f), (float)((float)c3.getBlue() / 255.0f), (float)1.0f);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)gradientShader.getProgram(), (CharSequence)"color4"), (float)((float)c4.getRed() / 255.0f), (float)((float)c4.getGreen() / 255.0f), (float)((float)c4.getBlue() / 255.0f), (float)1.0f);
        RoundedUtils.rect(x, y, width, height);
        GL20.glUseProgram((int)0);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void texture(float x, float y, float width, float height, float radius, float opacity) {
        GlStateManager.resetColor();
        GL20.glUseProgram((int)textureShader.getProgram());
        GL20.glUniform2f((int)GL20.glGetUniformLocation((int)textureShader.getProgram(), (CharSequence)"size"), (float)width, (float)height);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)textureShader.getProgram(), (CharSequence)"radius"), (float)radius);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)textureShader.getProgram(), (CharSequence)"alpha"), (float)opacity);
        GL20.glUniform1i((int)GL20.glGetUniformLocation((int)textureShader.getProgram(), (CharSequence)"texture"), (int)0);
        RoundedUtils.rect(x, y, width, height);
        GL20.glUseProgram((int)0);
        GlStateManager.disableBlend();
    }

    public static void round(float x, float y, float width, float height, float radius, Color color) {
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL20.glUseProgram((int)rectShader.getProgram());
        GL20.glUniform2f((int)GL20.glGetUniformLocation((int)rectShader.getProgram(), (CharSequence)"size"), (float)width, (float)height);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)rectShader.getProgram(), (CharSequence)"radius"), (float)radius);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)rectShader.getProgram(), (CharSequence)"color"), (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
        RoundedUtils.rect(x, y, width, height);
        GL20.glUseProgram((int)0);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public static void outline(float x, float y, float width, float height, float radius, float thickness, Color color) {
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL20.glUseProgram((int)outlineShader.getProgram());
        GL20.glUniform2f((int)GL20.glGetUniformLocation((int)outlineShader.getProgram(), (CharSequence)"size"), (float)width, (float)height);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)outlineShader.getProgram(), (CharSequence)"radius"), (float)radius);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)outlineShader.getProgram(), (CharSequence)"thickness"), (float)thickness);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)outlineShader.getProgram(), (CharSequence)"color"), (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
        RoundedUtils.rect(x, y, width, height);
        GL20.glUseProgram((int)0);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public static void outline(float x, float y, float width, float height, float radius, float thickness, float opacity, Color[] colours) {
        RoundedUtils.outline(x, y, width, height, radius, thickness, opacity, colours[0], colours[1], colours[2], colours[3]);
    }

    public static void outline(float x, float y, float width, float height, float radius, float thickness, float opacity, Color c1, Color c2, Color c3, Color c4) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL20.glUseProgram((int)outlineGradientShader.getProgram());
        GL20.glUniform2f((int)GL20.glGetUniformLocation((int)outlineGradientShader.getProgram(), (CharSequence)"size"), (float)width, (float)height);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)outlineGradientShader.getProgram(), (CharSequence)"radius"), (float)(radius - 1.5f));
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)outlineGradientShader.getProgram(), (CharSequence)"thickness"), (float)thickness);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)outlineGradientShader.getProgram(), (CharSequence)"alpha"), (float)opacity);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)outlineGradientShader.getProgram(), (CharSequence)"color1"), (float)((float)c1.getRed() / 255.0f), (float)((float)c1.getGreen() / 255.0f), (float)((float)c1.getBlue() / 255.0f), (float)1.0f);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)outlineGradientShader.getProgram(), (CharSequence)"color2"), (float)((float)c2.getRed() / 255.0f), (float)((float)c2.getGreen() / 255.0f), (float)((float)c2.getBlue() / 255.0f), (float)1.0f);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)outlineGradientShader.getProgram(), (CharSequence)"color3"), (float)((float)c3.getRed() / 255.0f), (float)((float)c3.getGreen() / 255.0f), (float)((float)c3.getBlue() / 255.0f), (float)1.0f);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)outlineGradientShader.getProgram(), (CharSequence)"color4"), (float)((float)c4.getRed() / 255.0f), (float)((float)c4.getGreen() / 255.0f), (float)((float)c4.getBlue() / 255.0f), (float)1.0f);
        RoundedUtils.rect(x, y, width, height);
        GL20.glUseProgram((int)0);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void round(HUDModule parent, float radius, Color color) {
        RoundedUtils.round(parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight(), radius, color);
    }

    public static void texture(HUDModule parent, float radius, float opacity) {
        RoundedUtils.texture(parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight(), radius, opacity);
    }

    public static void gradient(HUDModule parent, float radius, float opacity, Color c1, Color c2, Color c3, Color c4) {
        RoundedUtils.gradient(parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight(), radius, opacity, c1, c2, c3, c4);
    }

    public static void shadow(float x, float y, float width, float height, float radius, float softness, Color color) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int sF = sr.getScaleFactor();
        x *= 2.0f;
        y *= 2.0f;
        width *= 2.0f;
        height *= 2.0f;
        radius *= 2.0f;
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glDisable((int)3008);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL20.glUseProgram((int)shadowShader.getProgram());
        GL20.glUniform2f((int)GL20.glGetUniformLocation((int)shadowShader.getProgram(), (CharSequence)"location"), (float)x, (float)((float)sr.getScaledHeight_double() * (float)sF - height - y));
        GL20.glUniform2f((int)GL20.glGetUniformLocation((int)shadowShader.getProgram(), (CharSequence)"size"), (float)width, (float)height);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)shadowShader.getProgram(), (CharSequence)"radius"), (float)radius);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)shadowShader.getProgram(), (CharSequence)"shadowSoftness"), (float)softness);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)shadowShader.getProgram(), (CharSequence)"edgeSoftness"), (float)1.0f);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)shadowShader.getProgram(), (CharSequence)"color"), (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
        float g = width / 5.05f;
        float h = height / 5.05f;
        shadowShader.bind(0.0f, 0.0f, (float)sr.getScaledWidth_double() * (float)sF, (float)sr.getScaledHeight_double() * (float)sF);
        GL20.glUseProgram((int)0);
        GL11.glEnable((int)3008);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public static void shadowGradient(float x, float y, float width, float height, float radius, float softness, float opacity, Color c1, Color c2, Color c3, Color c4, boolean inner) {
        GlStateManager.resetColor();
        GL11.glDisable((int)3008);
        GL11.glShadeModel((int)7425);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL20.glUseProgram((int)shadowGradientShader.getProgram());
        float g = width / 5.05f;
        float h = height / 5.05f;
        GL20.glUniform2f((int)GL20.glGetUniformLocation((int)shadowGradientShader.getProgram(), (CharSequence)"size"), (float)width, (float)height);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)shadowGradientShader.getProgram(), (CharSequence)"radius"), (float)radius);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)shadowGradientShader.getProgram(), (CharSequence)"shadowSoftness"), (float)softness);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)shadowGradientShader.getProgram(), (CharSequence)"edgeSoftness"), (float)1.0f);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)shadowGradientShader.getProgram(), (CharSequence)"alpha"), (float)opacity);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)shadowGradientShader.getProgram(), (CharSequence)"color1"), (float)((float)c1.getRed() / 255.0f), (float)((float)c1.getGreen() / 255.0f), (float)((float)c1.getBlue() / 255.0f), (float)1.0f);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)shadowGradientShader.getProgram(), (CharSequence)"color2"), (float)((float)c2.getRed() / 255.0f), (float)((float)c2.getGreen() / 255.0f), (float)((float)c2.getBlue() / 255.0f), (float)1.0f);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)shadowGradientShader.getProgram(), (CharSequence)"color3"), (float)((float)c3.getRed() / 255.0f), (float)((float)c3.getGreen() / 255.0f), (float)((float)c3.getBlue() / 255.0f), (float)1.0f);
        GL20.glUniform4f((int)GL20.glGetUniformLocation((int)shadowGradientShader.getProgram(), (CharSequence)"color4"), (float)((float)c4.getRed() / 255.0f), (float)((float)c4.getGreen() / 255.0f), (float)((float)c4.getBlue() / 255.0f), (float)1.0f);
        GL20.glUniform1i((int)GL20.glGetUniformLocation((int)shadowGradientShader.getProgram(), (CharSequence)"inner"), (int)(inner ? 1 : 0));
        if (inner) {
            RoundedUtils.rect(x, y, width, height);
        } else {
            GL11.glBegin((int)7);
            GL11.glTexCoord2f((float)-0.2f, (float)-0.2f);
            GL11.glVertex2f((float)(x - g), (float)(y - h));
            GL11.glTexCoord2f((float)-0.2f, (float)1.2f);
            GL11.glVertex2f((float)(x - g), (float)(y + height + h));
            GL11.glTexCoord2f((float)1.2f, (float)1.2f);
            GL11.glVertex2f((float)(x + width + g), (float)(y + height + h));
            GL11.glTexCoord2f((float)1.2f, (float)-0.2f);
            GL11.glVertex2f((float)(x + width + g), (float)(y - h));
            GL11.glEnd();
        }
        GL20.glUseProgram((int)0);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)3008);
        GL11.glShadeModel((int)7424);
        GlStateManager.resetColor();
    }

    public static void rect(float x, float y, float width, float height) {
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex2f((float)x, (float)(y + height));
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex2f((float)(x + width), (float)(y + height));
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex2f((float)(x + width), (float)y);
        GL11.glEnd();
    }

    public static void test(int texture, Color color) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int sF = sr.getScaleFactor();
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL20.glUseProgram((int)testShader.getProgram());
        GL20.glUniform1i((int)GL20.glGetUniformLocation((int)testShader.getProgram(), (CharSequence)"originalTexture"), (int)texture);
        GL20.glUniform1f((int)GL20.glGetUniformLocation((int)testShader.getProgram(), (CharSequence)"width"), (float)((float)(sr.getScaledWidth_double() * (double)sF)));
        GL20.glUniform3f((int)GL20.glGetUniformLocation((int)testShader.getProgram(), (CharSequence)"glowColor"), (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f));
        shadowShader.bind(0.0f, 0.0f, (float)sr.getScaledWidth_double() * (float)sF, (float)sr.getScaledHeight_double() * (float)sF);
        GL20.glUseProgram((int)0);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }
}

