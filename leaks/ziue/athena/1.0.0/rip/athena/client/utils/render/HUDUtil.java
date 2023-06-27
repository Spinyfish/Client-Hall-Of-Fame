package rip.athena.client.utils.render;

import net.minecraft.client.*;
import java.awt.*;
import rip.athena.client.modules.impl.render.*;
import rip.athena.client.*;
import rip.athena.client.utils.font.*;
import net.minecraft.block.*;
import org.lwjgl.opengl.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.renderer.*;

public class HUDUtil
{
    private static Minecraft mc;
    
    public static void drawHUD(final String message, final int x, final int y, final int width, final int height, final Color bColor, final boolean bG, final Color sColor, final boolean customFont) {
        if (HUDUtil.mc.gameSettings.showDebugInfo) {
            return;
        }
        if (bG) {
            if (TPS.backgroundMode.equalsIgnoreCase("Modern")) {
                if (Athena.INSTANCE.getThemeManager().getTheme().isTriColor()) {
                    RoundedUtils.drawGradientRound((float)x, (float)y, (float)width, (float)height, 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getThirdColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor());
                }
                else {
                    RoundedUtils.drawGradientRound((float)x, (float)y, (float)width, (float)height, 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor());
                }
            }
            else if (TPS.backgroundMode.equalsIgnoreCase("Circle")) {
                RoundedUtils.drawGradientRound((float)x, (float)y, (float)width, (float)height, 6.0f, ColorUtil.getClientColor(0, 255), ColorUtil.getClientColor(90, 255), ColorUtil.getClientColor(180, 255), ColorUtil.getClientColor(270, 255));
            }
            else if (TPS.backgroundMode.equalsIgnoreCase("Fade")) {
                RoundedUtils.drawRoundedRect((float)x, (float)y, (float)(x + width), (float)(y + height), 8.0f, Athena.INSTANCE.getThemeManager().getTheme().getAccentColor().getRGB());
            }
            else {
                DrawUtils.displayFilledRectangle(x, y, x + width, y + height, bColor);
            }
        }
        final int stringWidth = getStringWidth(message);
        if (customFont) {
            drawString(message, x + width / 2 - stringWidth / 2, y + height / 2 - getStringHeight() / 2, sColor, true);
        }
        else {
            drawString(message, x + width / 2 - stringWidth / 2, y + height / 2 - getStringHeight() / 2, sColor, false);
        }
    }
    
    public static void drawString(final String message, final int x, final int y, final Color color, final boolean customFont) {
        int c = color.getRGB();
        if (color.getRed() == 5 && color.getGreen() == 5 && color.getBlue() == 5) {
            c = Color.HSBtoRGB(System.currentTimeMillis() % 50000L / 5000.0f, 1.0f, 1.0f);
        }
        else if (color.getRed() == 6 && color.getGreen() == 6 && color.getBlue() == 6) {
            double xTmp = x;
            for (final char letter : message.toCharArray()) {
                final long l = (long)(System.currentTimeMillis() - (xTmp * 10.0 - y * 10));
                final int i = Color.HSBtoRGB(l % 10000L / 10000.0f, 1.0f, 1.0f);
                final String tmp = String.valueOf(letter);
                if (customFont) {
                    FontManager.getProductSansRegular(25).drawString(tmp, (int)xTmp, y, i);
                    xTmp += rip.athena.client.font.FontManager.baloo17.getCharWidth(letter);
                }
                else {
                    HUDUtil.mc.fontRendererObj.drawString(tmp, (int)xTmp, y, i);
                    xTmp += HUDUtil.mc.fontRendererObj.getCharWidth(letter);
                }
            }
            return;
        }
        if (customFont) {
            FontManager.getProductSansRegular(25).drawString(message, x, y, c);
        }
        else {
            HUDUtil.mc.fontRendererObj.drawString(message, (float)x, (float)y, c, false);
        }
    }
    
    public static void drawString(final String message, final float x, final float y, final Color color, final boolean shadow) {
        drawString(message, (int)x, (int)y, color, shadow);
    }
    
    public static void drawString(final String message, final double x, final double y, final Color color, final boolean shadow) {
        drawString(message, (int)x, (int)y, color, shadow);
    }
    
    public static void drawHUD(final String message, final int x, final int y, final int width, final int height, final Color bColor, final Color sColor) {
        drawHUD(message, x, y, width, height, bColor, true, sColor, true);
    }
    
    public static void drawHUD(final String message, final float x, final float y, final int width, final int height, final Color bColor, final boolean bG, final Color sColor, final boolean shadow) {
        drawHUD(message, (int)x, (int)y, width, height, bColor, bG, sColor, shadow);
    }
    
    public static void drawHUD(final String message, final float x, final float y, final int width, final int height, final Color bColor, final boolean bG, final Color sColor, final boolean shadow, final boolean simple) {
        if (simple) {
            drawString(message, (int)x, (int)y, sColor, shadow);
        }
        else {
            drawHUD(message, (int)x, (int)y, width, height, bColor, bG, sColor, shadow);
        }
    }
    
    public static void highlight(final Block block, final BlockPos pos, final Color hColor) {
        final float red = hColor.getRed() / 255.0f;
        final float green = hColor.getGreen() / 255.0f;
        final float blue = hColor.getBlue() / 255.0f;
        final double renderPosX = HUDUtil.mc.getRenderManager().viewerPosX;
        final double renderPosY = HUDUtil.mc.getRenderManager().viewerPosY;
        final double renderPosZ = HUDUtil.mc.getRenderManager().viewerPosZ;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(red, green, blue, 0.5f);
        final AxisAlignedBB bb = block.getSelectedBoundingBox(HUDUtil.mc.theWorld, pos).expand(0.0020000000949949026, -9.49949026E-10, 0.0020000000949949026).offset(-renderPosX, -renderPosY, -renderPosZ);
        drawFilledBoundingBox(bb);
        GL11.glLineWidth(1.0f);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    public static void drawFilledBoundingBox(final AxisAlignedBB box) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        tessellator.draw();
    }
    
    public static void drawTexturedModalRect(final int x, final int y, final int u, final int v, final int width, final int height, final float zLevel) {
        final float var7 = 0.00390625f;
        final float var8 = 0.00390625f;
        final WorldRenderer tessellator = Tessellator.getInstance().getWorldRenderer();
        tessellator.begin(7, DefaultVertexFormats.POSITION_TEX);
        tessellator.pos(x + 0, y + height, zLevel).tex((u + 0) * var7, (v + height) * var8).endVertex();
        tessellator.pos(x + width, y + height, zLevel).tex((u + width) * var7, (v + height) * var8).endVertex();
        tessellator.pos(x + width, y + 0, zLevel).tex((u + width) * var7, (v + 0) * var8).endVertex();
        tessellator.pos(x + 0, y + 0, zLevel).tex((u + 0) * var7, (v + 0) * var8).endVertex();
        Tessellator.getInstance().draw();
    }
    
    public static int getStringWidth(final String string) {
        if (TPS.customFont) {
            return FontManager.getProductSansRegular(25).width(string);
        }
        return HUDUtil.mc.fontRendererObj.getStringWidth(string);
    }
    
    public static int getStringHeight() {
        if (TPS.customFont) {
            return (int)FontManager.getProductSansRegular(25).height();
        }
        return HUDUtil.mc.fontRendererObj.FONT_HEIGHT;
    }
    
    static {
        HUDUtil.mc = Minecraft.getMinecraft();
    }
}
