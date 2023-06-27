package rip.athena.client.gui.framework.draw;

import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.entity.*;
import rip.athena.client.font.*;
import java.awt.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.*;

public interface DrawImpl
{
    public static final int SHADOW_SIZE = 3;
    public static final int SHADOW_AMOUNT = 3;
    
    default void drawImage(final ResourceLocation image, final int x, final int y, final int width, final int height) {
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawImageCustomColor(image, x, y, width, height);
    }
    
    default void drawImageCustomColor(final ResourceLocation image, final int x, final int y, final int width, final int height) {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float)width, (float)height);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
    }
    
    default void drawSkinHead(final AbstractClientPlayer player, final int x, final int y, final int dimension, final float opacity) {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationSkin());
        GlStateManager.color(1.0f, 1.0f, 1.0f, opacity);
        final float multiplier = dimension / 18.0f;
        Gui.drawModalRectWithCustomSizedTexture(x, y, 19.0f * multiplier, 19.0f * multiplier, Math.round(18.0f * multiplier), Math.round(18.0f * multiplier), 150.0f * multiplier, 150.0f * multiplier);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 95.0f * multiplier - multiplier, 19.0f * multiplier, Math.round(18.0f * multiplier), Math.round(18.0f * multiplier), 150.0f * multiplier, 150.0f * multiplier);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
    }
    
    default void drawTexturedModalRect(final ResourceLocation resource, final int x, final int y, final int textureX, final int textureY, final int width, final int height, final int textureWidth, final int textureHeight) {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
        Gui.drawModalRectWithCustomSizedTexture(x, y, (float)textureX, (float)textureY, width, height, (float)textureWidth, (float)textureHeight);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
    }
    
    default int getStringHeight(final String string) {
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
    }
    
    default int getStringWidth(final String string) {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(string);
    }
    
    default void drawRect(final int x, final int y, final int width, final int height, final int color) {
        Gui.drawRectangle(x, y, x + width, y + height, color);
    }
    
    default void drawText(final String text, final int x, final int y, final int color) {
        FontManager.baloo16.drawString(text, (float)x, (float)(y + 1), color);
    }
    
    default void drawTextWithShadow(final String text, final int x, final int y, final int color) {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, (float)x, (float)(y + 1), color);
    }
    
    default void drawVerticalLine(final int x, final int y, final int height, final int thickness, final int color) {
        drawRect(x, y, thickness, height, color);
    }
    
    default void drawHorizontalLine(final int x, final int y, final int width, final int thickness, final int color) {
        drawRect(x, y, width, thickness, color);
    }
    
    default void drawPixel(final int x, final int y, final int color) {
        drawRect(x, y, 1, 1, color);
    }
    
    default void drawRainbowBar(final int rainbowX, final int rainbowY, final int rainbowWidth, final int rainbowHeight) {
        this.drawGradientRectSideways(rainbowX, rainbowY, rainbowX + rainbowWidth / 4, rainbowY + rainbowHeight, new Color(45, 135, 166).getRGB(), new Color(103, 93, 161).getRGB());
        this.drawGradientRectSideways(rainbowX + rainbowWidth / 4, rainbowY, rainbowX + rainbowWidth / 4 * 2, rainbowY + rainbowHeight, new Color(103, 93, 161).getRGB(), new Color(156, 58, 154).getRGB());
        this.drawGradientRectSideways(rainbowX + rainbowWidth / 4 * 2, rainbowY, rainbowX + rainbowWidth / 4 * 3, rainbowY + rainbowHeight, new Color(156, 58, 154).getRGB(), new Color(156, 120, 94).getRGB());
        this.drawGradientRectSideways(rainbowX + rainbowWidth / 4 * 3, rainbowY, rainbowX + rainbowWidth / 4 * 4, rainbowY + rainbowHeight, new Color(156, 120, 94).getRGB(), new Color(156, 173, 45).getRGB());
    }
    
    default void drawGradientRectSideways(final int x, final int y, final int width, final int height, final int startColor, final int endColor) {
        final float f = (startColor >> 24 & 0xFF) / 255.0f;
        final float f2 = (startColor >> 16 & 0xFF) / 255.0f;
        final float f3 = (startColor >> 8 & 0xFF) / 255.0f;
        final float f4 = (startColor & 0xFF) / 255.0f;
        final float f5 = (endColor >> 24 & 0xFF) / 255.0f;
        final float f6 = (endColor >> 16 & 0xFF) / 255.0f;
        final float f7 = (endColor >> 8 & 0xFF) / 255.0f;
        final float f8 = (endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(x + (double)width, y + (double)height, 0.0).color(f6, f7, f8, f5).endVertex();
        worldRenderer.pos(x + (double)width, y, 0.0).color(f6, f7, f8, f5).endVertex();
        worldRenderer.pos(x, y, 0.0).color(f2, f3, f4, f).endVertex();
        worldRenderer.pos(x, y + (double)height, 0.0).color(f2, f3, f4, f).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    
    default void drawGradientRectUpwards(final int x, final int y, final int width, final int height, final int startColor, final int endColor) {
        final float f = (startColor >> 24 & 0xFF) / 255.0f;
        final float f2 = (startColor >> 16 & 0xFF) / 255.0f;
        final float f3 = (startColor >> 8 & 0xFF) / 255.0f;
        final float f4 = (startColor & 0xFF) / 255.0f;
        final float f5 = (endColor >> 24 & 0xFF) / 255.0f;
        final float f6 = (endColor >> 16 & 0xFF) / 255.0f;
        final float f7 = (endColor >> 8 & 0xFF) / 255.0f;
        final float f8 = (endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(x + (double)width, y, 0.0).color(f6, f7, f8, f5).endVertex();
        worldRenderer.pos(x, y, 0.0).color(f6, f7, f8, f5).endVertex();
        worldRenderer.pos(x, y + (double)height, 0.0).color(f2, f3, f4, f).endVertex();
        worldRenderer.pos(x + (double)width, y + (double)height, 0.0).color(f2, f3, f4, f).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    
    default void drawShadowUp(final int x, final int y, final int width) {
        final int startColor = 0;
        final int endColor = 1342177280;
        this.drawGradientRectUpwards(x, y - 3, width, 3, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
    }
    
    default void drawShadowDown(final int x, final int y, final int width) {
        final int startColor = 0;
        final int endColor = 1342177280;
        this.drawGradientRectUpwards(x, y, width, 3, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
    }
    
    default void drawShadowLeft(final int x, final int y, final int height) {
        final int startColor = 0;
        final int endColor = 1342177280;
        this.drawGradientRectSideways(x - 3, y, 3, height, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
    }
    
    default void drawShadowRight(final int x, final int y, final int height) {
        final int startColor = 0;
        final int endColor = 1342177280;
        this.drawGradientRectSideways(x, y, 3, height, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
    }
    
    default void drawTriangle(final int x, final int y, final int width, final int height, final int pointing, final int color) {
        final float f = (color >> 24 & 0xFF) / 255.0f;
        final float f2 = (color >> 16 & 0xFF) / 255.0f;
        final float f3 = (color >> 8 & 0xFF) / 255.0f;
        final float f4 = (color & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, 0.0f);
        GlStateManager.rotate((float)pointing, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate((float)(-x), (float)(-y), 0.0f);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.color(f2, f3, f4, f);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(x + width / 3.0, y, 0.0).endVertex();
        worldRenderer.pos(x + (double)width, y + (double)height, 0.0).endVertex();
        worldRenderer.pos(x - width / 3.0, y + (double)height, 0.0).endVertex();
        worldRenderer.pos(x + (double)width, y + (double)height, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
    
    default void drawBottomRect(final int x, final int y, final int width, final int height, final int color) {
        final float f = (color >> 24 & 0xFF) / 255.0f;
        final float f2 = (color >> 16 & 0xFF) / 255.0f;
        final float f3 = (color >> 8 & 0xFF) / 255.0f;
        final float f4 = (color & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.color(f2, f3, f4, f);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(x, y + (double)height, 0.0).endVertex();
        worldRenderer.pos(x + (double)width, y + (double)height, 0.0).endVertex();
        worldRenderer.pos(x + (double)width, y, 0.0).endVertex();
        worldRenderer.pos(x + (double)width, y, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
