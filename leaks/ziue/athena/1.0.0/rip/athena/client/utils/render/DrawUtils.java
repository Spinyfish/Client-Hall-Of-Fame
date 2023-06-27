package rip.athena.client.utils.render;

import rip.athena.client.utils.shader.*;
import java.awt.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.renderer.*;

public enum DrawUtils
{
    BLACK(-16711423), 
    BLUE(-12028161), 
    DARKBLUE(-12621684), 
    GREEN(-9830551), 
    DARKGREEN(-9320847), 
    WHITE(-65794), 
    AQUA(-7820064), 
    DARKAQUA(-12621684), 
    GREY(-9868951), 
    DARKGREY(-14342875), 
    RED(-65536), 
    DARKRED(-8388608), 
    ORANGE(-29696), 
    DARKORANGE(-2263808), 
    YELLOW(-256), 
    DARKYELLOW(-2702025), 
    MAGENTA(-18751), 
    DARKMAGENTA(-2252579);
    
    public int c;
    private static final ShaderUtil gradientMaskShader;
    
    private DrawUtils(final int co) {
        this.c = co;
    }
    
    public static void drawMicrosoftLogo(final float x, final float y, final float size, final float spacing) {
        drawMicrosoftLogo(x, y, size, spacing, 1.0f);
    }
    
    public static void drawMicrosoftLogo(final float x, final float y, final float size, final float spacing, final float alpha) {
        final float rectSize = size / 2.0f - spacing;
        final int alphaVal = (int)(255.0f * alpha);
        Gui.drawRect2(x, y, rectSize, rectSize, new Color(244, 83, 38, alphaVal).getRGB());
        Gui.drawRect2(x + rectSize + spacing, y, rectSize, rectSize, new Color(130, 188, 6, alphaVal).getRGB());
        Gui.drawRect2(x, y + spacing + rectSize, rectSize, rectSize, new Color(5, 166, 241, alphaVal).getRGB());
        Gui.drawRect2(x + rectSize + spacing, y + spacing + rectSize, rectSize, rectSize, new Color(254, 186, 7, alphaVal).getRGB());
    }
    
    public static void applyGradientHorizontal(final float x, final float y, final float width, final float height, final float alpha, final Color left, final Color right, final Runnable content) {
        applyGradient(x, y, width, height, alpha, left, left, right, right, content);
    }
    
    public static void applyGradient(final float x, final float y, final float width, final float height, final float alpha, final Color bottomLeft, final Color topLeft, final Color bottomRight, final Color topRight, final Runnable content) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        DrawUtils.gradientMaskShader.init();
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        DrawUtils.gradientMaskShader.setUniformf("location", x * sr.getScaleFactor(), Minecraft.getMinecraft().displayHeight - height * sr.getScaleFactor() - y * sr.getScaleFactor());
        DrawUtils.gradientMaskShader.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        DrawUtils.gradientMaskShader.setUniformf("alpha", alpha);
        DrawUtils.gradientMaskShader.setUniformi("tex", 0);
        DrawUtils.gradientMaskShader.setUniformf("color1", bottomLeft.getRed() / 255.0f, bottomLeft.getGreen() / 255.0f, bottomLeft.getBlue() / 255.0f);
        DrawUtils.gradientMaskShader.setUniformf("color2", topLeft.getRed() / 255.0f, topLeft.getGreen() / 255.0f, topLeft.getBlue() / 255.0f);
        DrawUtils.gradientMaskShader.setUniformf("color3", bottomRight.getRed() / 255.0f, bottomRight.getGreen() / 255.0f, bottomRight.getBlue() / 255.0f);
        DrawUtils.gradientMaskShader.setUniformf("color4", topRight.getRed() / 255.0f, topRight.getGreen() / 255.0f, topRight.getBlue() / 255.0f);
        content.run();
        DrawUtils.gradientMaskShader.unload();
        GlStateManager.disableBlend();
    }
    
    public static void displayFilledRectangle(int x1, int y1, int x2, int y2, final Color color) {
        GL11.glPushMatrix();
        if (x1 < x2) {
            final int i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            final int j = y1;
            y1 = y2;
            y2 = j;
        }
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL11.glBegin(7);
        GL11.glVertex2f((float)x1, (float)y2);
        GL11.glVertex2f((float)x2, (float)y2);
        GL11.glVertex2f((float)x2, (float)y1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    public static void drawCircle(final float x, final float y, final double r, final int c, final float width, final boolean filled) {
        final float red = (c >> 16 & 0xFF) / 255.0f;
        final float green = (c >> 8 & 0xFF) / 255.0f;
        final float blue = (c & 0xFF) / 255.0f;
        final float alpha = (c >> 24 & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(width);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(filled ? 6 : 2);
        for (int i = 0; i <= 360; ++i) {
            final double x2 = Math.sin(i * 3.141526 / 180.0) * r;
            final double y2 = Math.cos(i * 3.141526 / 180.0) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }
    
    public static void color(final int color) {
        final float f = (color >> 24 & 0xFF) / 255.0f;
        final float f2 = (color >> 16 & 0xFF) / 255.0f;
        final float f3 = (color >> 8 & 0xFF) / 255.0f;
        final float f4 = (color & 0xFF) / 255.0f;
        GL11.glColor4f(f2, f3, f4, f);
    }
    
    public static void glColor(final int hex) {
        final float a = (hex >> 24 & 0xFF) / 255.0f;
        final float r = (hex >> 16 & 0xFF) / 255.0f;
        final float g = (hex >> 8 & 0xFF) / 255.0f;
        final float b = (hex & 0xFF) / 255.0f;
        GL11.glColor4f(r, g, b, a);
    }
    
    public static void drawImage(final ResourceLocation image, final int x, final int y, final int width, final int height) {
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        color(-1);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float)width, (float)height);
        GlStateManager.resetColor();
        GlStateManager.disableBlend();
    }
    
    public static int reAlpha(final int color, final float alpha) {
        try {
            final Color c = new Color(color);
            final float r = 0.003921569f * c.getRed();
            final float g = 0.003921569f * c.getGreen();
            final float b = 0.003921569f * c.getBlue();
            return new Color(r, g, b, alpha).getRGB();
        }
        catch (Throwable e) {
            e.printStackTrace();
            return color;
        }
    }
    
    public static void drawChromaString(final String string, final double x, final double y, final boolean static_chroma, final boolean shadow) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (static_chroma) {
            final int i = Color.HSBtoRGB(System.currentTimeMillis() % 5000L / 5000.0f, 0.8f, 0.8f);
            mc.fontRendererObj.drawString(string, (float)x, (float)y, i, shadow);
        }
        else {
            double xTmp = x;
            for (final char textChar : string.toCharArray()) {
                final long l = (long)(System.currentTimeMillis() - (xTmp * 10.0 - y * 10.0));
                final int j = Color.HSBtoRGB(l % 2000L / 2000.0f, 0.8f, 0.8f);
                final String tmp = String.valueOf(textChar);
                mc.fontRendererObj.drawString(tmp, (float)xTmp, (float)y, j, shadow);
                xTmp += mc.fontRendererObj.getCharWidth(textChar);
            }
        }
    }
    
    public static void drawSquareTexture(final ResourceLocation resourceLocation, final float size, final float x, final float y) {
        GL11.glPushMatrix();
        final float width = size * 2.0f;
        final float height = size * 2.0f;
        final float u = 0.0f;
        final float v = 0.0f;
        final float uWidth = size;
        final float vHeight = size;
        final float textureWidth = size;
        final float textureHeight = size;
        GL11.glEnable(3042);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GL11.glBegin(7);
        GL11.glTexCoord2d((double)(u / textureWidth), (double)(v / textureHeight));
        GL11.glVertex2d((double)x, (double)y);
        GL11.glTexCoord2d((double)(u / textureWidth), (double)((v + vHeight) / textureHeight));
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glTexCoord2d((double)((u + uWidth) / textureWidth), (double)((v + vHeight) / textureHeight));
        GL11.glVertex2d((double)(x + width), (double)(y + height));
        GL11.glTexCoord2d((double)((u + uWidth) / textureWidth), (double)(v / textureHeight));
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    public static void drawStrip(final int x, final int y, final float width, final double angle, final float points, final float radius, final int color) {
        GL11.glPushMatrix();
        final float f1 = (color >> 24 & 0xFF) / 255.0f;
        final float f2 = (color >> 16 & 0xFF) / 255.0f;
        final float f3 = (color >> 8 & 0xFF) / 255.0f;
        final float f4 = (color & 0xFF) / 255.0f;
        GL11.glTranslatef((float)x, (float)y, 0.0f);
        GL11.glColor4f(f2, f3, f4, f1);
        GL11.glLineWidth(width);
        GL11.glEnable(3042);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(3008);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glEnable(32925);
        if (angle > 0.0) {
            GL11.glBegin(3);
            for (int i = 0; i < angle; ++i) {
                final float a = (float)(i * (angle * 3.141592653589793 / points));
                final float xc = (float)(Math.cos(a) * radius);
                final float yc = (float)(Math.sin(a) * radius);
                GL11.glVertex2f(xc, yc);
            }
            GL11.glEnd();
        }
        if (angle < 0.0) {
            GL11.glBegin(3);
            for (int i = 0; i > angle; --i) {
                final float a = (float)(i * (angle * 3.141592653589793 / points));
                final float xc = (float)(Math.cos(a) * -radius);
                final float yc = (float)(Math.sin(a) * -radius);
                GL11.glVertex2f(xc, yc);
            }
            GL11.glEnd();
        }
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(3008);
        GL11.glEnable(2929);
        GL11.glDisable(32925);
        GL11.glDisable(3479);
        GL11.glPopMatrix();
    }
    
    public static void drawCustomFontChromaString(final Font font, final String string, final int x, final int y, final boolean static_chroma, final boolean shadow) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (static_chroma) {
            final int i = Color.HSBtoRGB(System.currentTimeMillis() % 5000L / 5000.0f, 0.8f, 0.8f);
            font.drawString(string, x, y, i);
        }
        else {
            int xTmp = x;
            for (final char textChar : string.toCharArray()) {
                final long l = System.currentTimeMillis() - (xTmp * 10 - y * 10);
                final int j = Color.HSBtoRGB(l % 2000L / 2000.0f, 0.8f, 0.8f);
                final String tmp = String.valueOf(textChar);
                font.drawString(tmp, xTmp, y, j);
                xTmp += font.width(tmp);
            }
        }
    }
    
    public static int getCenterX() {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final ScaledResolution scaledresolution = new ScaledResolution(minecraft);
        return minecraft.displayWidth / (2 * scaledresolution.getScaleFactor());
    }
    
    public static int getCenterY() {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final ScaledResolution scaledresolution = new ScaledResolution(minecraft);
        return minecraft.displayHeight / (2 * scaledresolution.getScaleFactor());
    }
    
    public static void drawGradientRect(final double minX, final double minY, final double maxX, final double maxY, final int par5, final int par6) {
        final int zLevel = 0;
        GL11.glPushMatrix();
        final float f = (par5 >> 24 & 0xFF) / 255.0f;
        final float f2 = (par5 >> 16 & 0xFF) / 255.0f;
        final float f3 = (par5 >> 8 & 0xFF) / 255.0f;
        final float f4 = (par5 & 0xFF) / 255.0f;
        final float f5 = (par6 >> 24 & 0xFF) / 255.0f;
        final float f6 = (par6 >> 16 & 0xFF) / 255.0f;
        final float f7 = (par6 >> 8 & 0xFF) / 255.0f;
        final float f8 = (par6 & 0xFF) / 255.0f;
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        final WorldRenderer tessellator = Tessellator.getInstance().getWorldRenderer();
        tessellator.begin(7, DefaultVertexFormats.POSITION_COLOR);
        tessellator.pos(maxX, minY, zLevel).color(f2, f3, f4, f).endVertex();
        tessellator.pos(minX, minY, zLevel).color(f2, f3, f4, f).endVertex();
        tessellator.pos(minX, maxY, zLevel).color(f6, f7, f8, f5).endVertex();
        tessellator.pos(maxX, maxY, zLevel).color(f6, f7, f8, f5).endVertex();
        Tessellator.getInstance().draw();
        GL11.glShadeModel(7424);
        GL11.glEnable(3008);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }
    
    public static void drawRoundedRect(final int x0, final int y0, final int x1, final int y1, final float radius, final int color) {
        final int i = 18;
        final float f = 90.0f / i;
        final float f2 = (color >> 24 & 0xFF) / 255.0f;
        final float f3 = (color >> 16 & 0xFF) / 255.0f;
        final float f4 = (color >> 8 & 0xFF) / 255.0f;
        final float f5 = (color & 0xFF) / 255.0f;
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(f3, f4, f5, f2);
        GL11.glBegin(5);
        GL11.glVertex2f(x0 + radius, (float)y0);
        GL11.glVertex2f(x0 + radius, (float)y1);
        GL11.glVertex2f(x1 - radius, (float)y0);
        GL11.glVertex2f(x1 - radius, (float)y1);
        GL11.glEnd();
        GL11.glBegin(5);
        GL11.glVertex2f((float)x0, y0 + radius);
        GL11.glVertex2f(x0 + radius, y0 + radius);
        GL11.glVertex2f((float)x0, y1 - radius);
        GL11.glVertex2f(x0 + radius, y1 - radius);
        GL11.glEnd();
        GL11.glBegin(5);
        GL11.glVertex2f((float)x1, y0 + radius);
        GL11.glVertex2f(x1 - radius, y0 + radius);
        GL11.glVertex2f((float)x1, y1 - radius);
        GL11.glVertex2f(x1 - radius, y1 - radius);
        GL11.glEnd();
        GL11.glBegin(6);
        float f6 = x1 - radius;
        float f7 = y0 + radius;
        GL11.glVertex2f(f6, f7);
        for (int j = 0; j <= i; ++j) {
            final float f8 = j * f;
            GL11.glVertex2f((float)(f6 + radius * Math.cos(Math.toRadians(f8))), (float)(f7 - radius * Math.sin(Math.toRadians(f8))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f6 = x0 + radius;
        f7 = y0 + radius;
        GL11.glVertex2f(f6, f7);
        for (int k = 0; k <= i; ++k) {
            final float f9 = k * f;
            GL11.glVertex2f((float)(f6 - radius * Math.cos(Math.toRadians(f9))), (float)(f7 - radius * Math.sin(Math.toRadians(f9))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f6 = x0 + radius;
        f7 = y1 - radius;
        GL11.glVertex2f(f6, f7);
        for (int l = 0; l <= i; ++l) {
            final float f10 = l * f;
            GL11.glVertex2f((float)(f6 - radius * Math.cos(Math.toRadians(f10))), (float)(f7 + radius * Math.sin(Math.toRadians(f10))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f6 = x1 - radius;
        f7 = y1 - radius;
        GL11.glVertex2f(f6, f7);
        for (int i2 = 0; i2 <= i; ++i2) {
            final float f11 = i2 * f;
            GL11.glVertex2f((float)(f6 + radius * Math.cos(Math.toRadians(f11))), (float)(f7 + radius * Math.sin(Math.toRadians(f11))));
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(3042);
    }
    
    static {
        gradientMaskShader = new ShaderUtil("gradientMask");
    }
}
