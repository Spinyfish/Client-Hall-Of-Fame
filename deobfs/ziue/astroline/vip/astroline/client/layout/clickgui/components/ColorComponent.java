/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.WorldRenderer
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.service.module.value.ColorValue
 */
package vip.astroline.client.layout.clickgui.components;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.service.module.value.ColorValue;

public class ColorComponent {
    public ColorValue colorValue;
    private float hue;
    private float saturation;
    private float brightness;
    private float alpha;
    private boolean colorSelectorDragging;
    private boolean hueSelectorDragging;
    private boolean alphaSelectorDragging;
    public Color ccc;

    public ColorComponent(ColorValue colorValue) {
        this.colorValue = colorValue;
        this.ccc = colorValue.getValue();
    }

    public void draw(float mouseX, float mouseY, float x, float y, float w, float h) {
        float hueSelectorY;
        float hueSliderYDif;
        float alphaSliderBottom;
        float hueSliderRight;
        float x2 = this.getExpandedX(x, w);
        float y2 = this.getExpandedY(y, h);
        float width = this.getExpandedWidth(x, w);
        float height = this.getExpandedHeight(x, w);
        int black = -16777216;
        Gui.drawRect((double)(x2 - 0.5f), (double)(y2 - 0.5f), (double)(x2 + width + 0.5f), (double)(y2 + height + 0.5f), (int)black);
        int guiAlpha = 40;
        int color = this.colorValue.getValue().getRGB();
        int colorAlpha = color >> 24 & 0xFF;
        int minAlpha = Math.min(guiAlpha, colorAlpha);
        if (colorAlpha < 255) {
            ColorComponent.drawCheckeredBackground(x2, y2, x2 + width, y2 + height);
        }
        int newColor = new Color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, minAlpha).getRGB();
        this.drawGradientRect(x2, y2, x2 + width, y2 + height, newColor, ColorComponent.darker(newColor, 0.6f));
        GL11.glTranslated((double)0.0, (double)0.0, (double)3.0);
        float expandedX = this.getExpandedX(x, w);
        float expandedY = this.getExpandedY(y, h);
        float expandedWidth = this.getExpandedWidth(x, w);
        float expandedHeight = this.getExpandedHeight(x, w);
        Gui.drawRect((double)expandedX, (double)expandedY, (double)(expandedX + expandedWidth), (double)(expandedY + expandedHeight), (int)black);
        Gui.drawRect((double)(expandedX + 0.5f), (double)(expandedY + 0.5f), (double)(expandedX + expandedWidth - 0.5f), (double)(expandedY + expandedHeight - 0.5f), (int)new Color(0x39393B).getRGB());
        Gui.drawRect((double)(expandedX + 1.0f), (double)(expandedY + 1.0f), (double)(expandedX + expandedWidth - 1.0f), (double)(expandedY + expandedHeight - 1.0f), (int)new Color(0x232323).getRGB());
        float colorPickerSize = expandedWidth - 9.0f - 8.0f;
        float colorPickerLeft = expandedX + 3.0f;
        float colorPickerTop = expandedY + 3.0f;
        float colorPickerRight = colorPickerLeft + colorPickerSize;
        float colorPickerBottom = colorPickerTop + colorPickerSize;
        int selectorWhiteOverlayColor = new Color(255, 255, 255, Math.min(40, 180)).getRGB();
        if (mouseX <= colorPickerLeft || mouseY <= colorPickerTop || mouseX >= colorPickerRight || mouseY >= colorPickerBottom) {
            this.colorSelectorDragging = false;
        }
        Gui.drawRect((double)(colorPickerLeft - 0.5f), (double)(colorPickerTop - 0.5f), (double)(colorPickerRight + 0.5f), (double)(colorPickerBottom + 0.5f), (int)black);
        this.drawColorPickerRect(colorPickerLeft, colorPickerTop, colorPickerRight, colorPickerBottom);
        float hueSliderLeft = this.saturation * (colorPickerRight - colorPickerLeft);
        float alphaSliderTop = (1.0f - this.brightness) * (colorPickerBottom - colorPickerTop);
        if (this.colorSelectorDragging) {
            hueSliderRight = colorPickerRight - colorPickerLeft;
            alphaSliderBottom = mouseX - colorPickerLeft;
            this.saturation = alphaSliderBottom / hueSliderRight;
            hueSliderLeft = alphaSliderBottom;
            hueSliderYDif = colorPickerBottom - colorPickerTop;
            hueSelectorY = mouseY - colorPickerTop;
            this.brightness = 1.0f - hueSelectorY / hueSliderYDif;
            alphaSliderTop = hueSelectorY;
            this.updateColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness), false);
        }
        hueSliderRight = colorPickerLeft + hueSliderLeft - 0.5f;
        alphaSliderBottom = colorPickerTop + alphaSliderTop - 0.5f;
        hueSliderYDif = colorPickerLeft + hueSliderLeft + 0.5f;
        hueSelectorY = colorPickerTop + alphaSliderTop + 0.5f;
        Gui.drawRect((double)(hueSliderRight - 0.5f), (double)(alphaSliderBottom - 0.5f), (double)hueSliderRight, (double)(hueSelectorY + 0.5f), (int)black);
        Gui.drawRect((double)hueSliderYDif, (double)(alphaSliderBottom - 0.5f), (double)(hueSliderYDif + 0.5f), (double)(hueSelectorY + 0.5f), (int)black);
        Gui.drawRect((double)hueSliderRight, (double)(alphaSliderBottom - 0.5f), (double)hueSliderYDif, (double)alphaSliderBottom, (int)black);
        Gui.drawRect((double)hueSliderRight, (double)hueSelectorY, (double)hueSliderYDif, (double)(hueSelectorY + 0.5f), (int)black);
        Gui.drawRect((double)hueSliderRight, (double)alphaSliderBottom, (double)hueSliderYDif, (double)hueSelectorY, (int)selectorWhiteOverlayColor);
        hueSliderLeft = colorPickerRight + 3.0f;
        hueSliderRight = hueSliderLeft + 8.0f;
        if (mouseX <= hueSliderLeft || mouseY <= colorPickerTop || mouseX >= hueSliderRight || mouseY >= colorPickerBottom) {
            this.hueSelectorDragging = false;
        }
        hueSliderYDif = colorPickerBottom - colorPickerTop;
        hueSelectorY = (1.0f - this.hue) * hueSliderYDif;
        if (this.hueSelectorDragging) {
            float inc = mouseY - colorPickerTop;
            this.hue = 1.0f - inc / hueSliderYDif;
            hueSelectorY = inc;
            this.updateColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness), false);
        }
        Gui.drawRect((double)(hueSliderLeft - 0.5f), (double)(colorPickerTop - 0.5f), (double)(hueSliderRight + 0.5f), (double)(colorPickerBottom + 0.5f), (int)black);
        float hsHeight = colorPickerBottom - colorPickerTop;
        float alphaSelectorX = hsHeight / 5.0f;
        float asLeft = colorPickerTop;
        int i2 = 0;
        while ((float)i2 < 5.0f) {
            boolean last = (float)i2 == 4.0f;
            this.drawGradientRect(hueSliderLeft, asLeft, hueSliderRight, asLeft + alphaSelectorX, new Color(Color.HSBtoRGB(1.0f - 0.2f * (float)i2, 1.0f, 1.0f)).getRGB(), new Color(Color.HSBtoRGB(1.0f - 0.2f * (float)(i2 + 1), 1.0f, 1.0f)).getRGB());
            if (!last) {
                asLeft += alphaSelectorX;
            }
            ++i2;
        }
        float hsTop = colorPickerTop + hueSelectorY - 0.5f;
        float asRight = colorPickerTop + hueSelectorY + 0.5f;
        Gui.drawRect((double)(hueSliderLeft - 0.5f), (double)(hsTop - 0.5f), (double)hueSliderLeft, (double)(asRight + 0.5f), (int)black);
        Gui.drawRect((double)hueSliderRight, (double)(hsTop - 0.5f), (double)(hueSliderRight + 0.5f), (double)(asRight + 0.5f), (int)black);
        Gui.drawRect((double)hueSliderLeft, (double)(hsTop - 0.5f), (double)hueSliderRight, (double)hsTop, (int)black);
        Gui.drawRect((double)hueSliderLeft, (double)asRight, (double)hueSliderRight, (double)(asRight + 0.5f), (int)black);
        Gui.drawRect((double)hueSliderLeft, (double)hsTop, (double)hueSliderRight, (double)asRight, (int)selectorWhiteOverlayColor);
        alphaSliderTop = colorPickerBottom + 3.0f;
        alphaSliderBottom = alphaSliderTop + 8.0f;
        if (mouseX <= colorPickerLeft || mouseY <= alphaSliderTop || mouseX >= colorPickerRight || mouseY >= alphaSliderBottom) {
            this.alphaSelectorDragging = false;
        }
        int z2 = Color.HSBtoRGB(this.hue, this.saturation, this.brightness);
        int r2 = z2 >> 16 & 0xFF;
        int g2 = z2 >> 8 & 0xFF;
        int b2 = z2 & 0xFF;
        hsHeight = colorPickerRight - colorPickerLeft;
        alphaSelectorX = this.alpha * hsHeight;
        if (this.alphaSelectorDragging) {
            asLeft = mouseX - colorPickerLeft;
            this.alpha = asLeft / hsHeight;
            alphaSelectorX = asLeft;
            this.updateColor(new Color(r2, g2, b2, (int)(this.alpha * 255.0f)).getRGB(), true);
        }
        Gui.drawRect((double)(colorPickerLeft - 0.5f), (double)(alphaSliderTop - 0.5f), (double)(colorPickerRight + 0.5f), (double)(alphaSliderBottom + 0.5f), (int)black);
        ColorComponent.drawCheckeredBackground(colorPickerLeft, alphaSliderTop, colorPickerRight, alphaSliderBottom);
        this.drawGradientRect(colorPickerLeft, alphaSliderTop, colorPickerRight, alphaSliderBottom, true, new Color(r2, g2, b2, 0).getRGB(), new Color(r2, g2, b2, Math.min(guiAlpha, 255)).getRGB());
        asLeft = colorPickerLeft + alphaSelectorX - 0.5f;
        asRight = colorPickerLeft + alphaSelectorX + 0.5f;
        Gui.drawRect((double)(asLeft - 0.5f), (double)alphaSliderTop, (double)(asRight + 0.5f), (double)alphaSliderBottom, (int)black);
        Gui.drawRect((double)asLeft, (double)alphaSliderTop, (double)asRight, (double)alphaSliderBottom, (int)selectorWhiteOverlayColor);
        GL11.glTranslated((double)0.0, (double)0.0, (double)-3.0);
    }

    public void release() {
        if (this.hueSelectorDragging) {
            this.hueSelectorDragging = false;
        } else if (this.colorSelectorDragging) {
            this.colorSelectorDragging = false;
        } else {
            if (!this.alphaSelectorDragging) return;
            this.alphaSelectorDragging = false;
        }
    }

    public void onClick(float mouseX, float mouseY, float x, float y, float w, float h) {
        float expandedX = this.getExpandedX(x, w);
        float expandedY = this.getExpandedY(y, h);
        float expandedWidth = this.getExpandedWidth(x, w);
        float expandedHeight = this.getExpandedHeight(x, w);
        float colorPickerSize = expandedWidth - 9.0f - 8.0f;
        float colorPickerLeft = expandedX + 3.0f;
        float colorPickerTop = expandedY + 3.0f;
        float colorPickerRight = colorPickerLeft + colorPickerSize;
        float colorPickerBottom = colorPickerTop + colorPickerSize;
        float alphaSliderTop = colorPickerBottom + 3.0f;
        float alphaSliderBottom = alphaSliderTop + 8.0f;
        float hueSliderLeft = colorPickerRight + 3.0f;
        float hueSliderRight = hueSliderLeft + 8.0f;
        this.colorSelectorDragging = !this.colorSelectorDragging && mouseX > colorPickerLeft && mouseY > colorPickerTop && mouseX < colorPickerRight && mouseY < colorPickerBottom;
        this.alphaSelectorDragging = !this.alphaSelectorDragging && mouseX > colorPickerLeft && mouseY > alphaSliderTop && mouseX < colorPickerRight && mouseY < alphaSliderBottom;
        this.hueSelectorDragging = !this.hueSelectorDragging && mouseX > hueSliderLeft && mouseY > colorPickerTop && mouseX < hueSliderRight && mouseY < colorPickerBottom;
    }

    private static void drawCheckeredBackground(float x, float y, float x2, float y2) {
        Gui.drawRect((double)x, (double)y, (double)x2, (double)y2, (int)new Color(0xFFFFFF).getRGB());
        boolean offset = false;
        while (y < y2) {
            offset = !offset;
            for (float x1 = x + (float)(offset ? true : false); x1 < x2; x1 += 2.0f) {
                if (!(x1 <= x2 - 1.0f)) continue;
                Gui.drawRect((double)x1, (double)y, (double)(x1 + 1.0f), (double)(y + 1.0f), (int)new Color(0x808080).getRGB());
            }
            y += 1.0f;
        }
    }

    public void updateColor(int hex, boolean hasAlpha) {
        if (hasAlpha) {
            this.setColor(hex);
        } else {
            this.setColor(new Color(hex >> 16 & 0xFF, hex >> 8 & 0xFF, hex & 0xFF, (int)(this.alpha * 255.0f)).getRGB());
        }
    }

    public void setColor(int color) {
        this.ccc = new Color(color);
    }

    public static int darker(int color, float factor) {
        int r = (int)((float)(color >> 16 & 0xFF) * factor);
        int g = (int)((float)(color >> 8 & 0xFF) * factor);
        int b = (int)((float)(color & 0xFF) * factor);
        int a = color >> 24 & 0xFF;
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF | (a & 0xFF) << 24;
    }

    public void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
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
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.shadeModel((int)7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)right, (double)top, 0.0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void drawGradientRect(double left, double top, double right, double bottom, boolean sideways, int startColor, int endColor) {
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glShadeModel((int)7425);
        GL11.glBegin((int)7);
        ColorComponent.color(startColor);
        if (sideways) {
            GL11.glVertex2d((double)left, (double)top);
            GL11.glVertex2d((double)left, (double)bottom);
            ColorComponent.color(endColor);
            GL11.glVertex2d((double)right, (double)bottom);
            GL11.glVertex2d((double)right, (double)top);
        } else {
            GL11.glVertex2d((double)left, (double)top);
            ColorComponent.color(endColor);
            GL11.glVertex2d((double)left, (double)bottom);
            GL11.glVertex2d((double)right, (double)bottom);
            ColorComponent.color(startColor);
            GL11.glVertex2d((double)right, (double)top);
        }
        GL11.glEnd();
        GL11.glDisable((int)3042);
        GL11.glShadeModel((int)7424);
        GL11.glEnable((int)3553);
    }

    public static void color(int color) {
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
    }

    private void drawColorPickerRect(float left, float top, float right, float bottom) {
        int hueBasedColor = Color.HSBtoRGB(this.hue, 1.0f, 1.0f);
        this.drawGradientRect(left, top, right, bottom, true, new Color(0xFFFFFF).getRGB(), hueBasedColor);
        this.drawGradientRect(left, top, right, bottom, 0, Color.black.getRGB());
    }

    public float getExpandedX(float x, float w) {
        return x + w - 80.333336f;
    }

    public float getExpandedY(float y, float h) {
        return y + h;
    }

    public float getExpandedWidth(float x, float w) {
        float right = x + w;
        return right - this.getExpandedX(x, w);
    }

    public float getExpandedHeight(float x, float w) {
        return this.getExpandedWidth(x, w);
    }
}
