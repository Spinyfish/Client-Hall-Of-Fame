/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 19:36
 */
package dev.myth.api.utils.render;

import dev.myth.api.interfaces.IMethods;
import dev.myth.api.logger.Logger;
import dev.myth.features.display.ClickGuiFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.ui.clickgui.skeetgui.SkeetGui;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_QUADS;

@UtilityClass
public class RenderUtil implements IMethods {

    private static final Frustum frustrum = new Frustum();
    private ResourceLocation skeetChainMail = new ResourceLocation("myth/skeetchainmail.png");

    public static void drawRect(double x, double y, double width, double height, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
    }

    public static void drawRect(double x, double y, double width, double height, Color color) {
        drawRect(x, y, width, height, color.getRGB());
    }

    public static void drawCircle(double cx, double cy, double r, final int c) {
        drawRoundedRect(cx - r, cy - r, r * 2, r * 2, r, c, 0, 0);
    }

    public static void drawCircle(double cx, double cy, double r, final int c, final int outlineColor, final float outlineWidth) {
        drawRoundedRect(cx - r, cy - r, r * 2, r * 2, r, c, outlineColor, outlineWidth);
    }

    public static void drawRoundedRect(double x, double y, double width, double height, double radius, int color, int outlineColor, float outlineWidth) {
        double x1 = x + width, y1 = y + height;
        GL11.glPushAttrib(0);

        if (radius < 1) radius = 1;
        if (radius > height / 2) radius = height / 2;
        if (radius > width / 2) radius = width / 2;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        if (color != 0) {
            glColor(color);
            GL11.glBegin(GL11.GL_POLYGON);
            drawPie(x, y, x1, y1, radius);
            GL11.glEnd();
        }

        if (outlineWidth > 0 && outlineColor != 0) {
            glColor(outlineColor);
            GL11.glLineWidth(outlineWidth);
            GL11.glBegin(GL11.GL_LINE_LOOP);
            drawPie(x, y, x1, y1, radius);
            GL11.glEnd();
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopAttrib();
        GL11.glColor4f(1, 1, 1, 1);
    }

    public static void drawRoundedRectWithXY(double x, double y, double x1, double y1, double radius, int color, int outlineColor, float outlineWidth) {
        GL11.glPushAttrib(0);

        if (radius < 1) radius = 1;
        if (radius > (y1 - y) / 2) radius = (y1 - y) / 2;
        if (radius > (x1 - x) / 2) radius = (x1 - x) / 2;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        if (color != 0) {
            glColor(color);
            GL11.glBegin(GL11.GL_POLYGON);
            drawPie(x, y, x1, y1, radius);
            GL11.glEnd();
        }

        if (outlineWidth > 0 && outlineColor != 0) {
            glColor(outlineColor);
            GL11.glLineWidth(outlineWidth);
            GL11.glBegin(GL11.GL_LINE_LOOP);
            drawPie(x, y, x1, y1, radius);
            GL11.glEnd();
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopAttrib();
        GL11.glColor4f(1, 1, 1, 1);
    }

    public static void drawPie(double x, double y, double x1, double y1, double radius) {
        final double v = Math.PI / 180;
        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin(i * v) * (radius * -1), y + radius + Math.cos(i * v) * (radius * -1));
        }
        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin(i * v) * (radius * -1), y1 - radius + Math.cos(i * v) * (radius * -1));
        }
        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x1 - radius + Math.sin(i * v) * radius, y1 - radius + Math.cos(i * v) * radius);
        }
        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x1 - radius + Math.sin(i * v) * radius, y + radius + Math.cos(i * v) * radius);
        }
    }

    public static void glColor(int color) {
        GL11.glColor4ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF), (byte) (color >> 24 & 0xFF));
    }

    public static double[] getInterpolatedPosition(Entity entity) {
        double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX)
                * MC.timer.renderPartialTicks - MC.getRenderManager().viewerPosX;
        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY)
                * MC.timer.renderPartialTicks - MC.getRenderManager().viewerPosY;
        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ)
                * MC.timer.renderPartialTicks - MC.getRenderManager().viewerPosZ;

        return new double[]{posX, posY, posZ};
    }

    public static boolean isInViewFrustrum(final Entity entity) {
        return (isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck);
    }

    private static boolean isInViewFrustrum(final AxisAlignedBB bb) {
        final Entity current = MC.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static void scissor(double x, double y, double width, double height) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = (sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        GL11.glScissor((int) finalX, (int) (finalY - finalHeight), (int) finalWidth, (int) finalHeight);
    }

    public static void drawHead(final AbstractClientPlayer target, final int x, final int y, final int width, final int height) {
        final ResourceLocation skin = target.getLocationSkin();
        MC.getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
    }

    public static void drawImage(final double x, final double y, final double width, final double height, final ResourceLocation image, Color color) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1.0f);
        MC.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawImage(final double x, final double y, final double width, final double height, final ResourceLocation image) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        MC.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public void drawSkeetRect(double x, double y, double width, double height, boolean chainmail, boolean gradient) {
        Gui.drawRect(x, y, x + width, y + height, getColor(0x080809, 255));
        Gui.drawRect(x + 0.5, y + 0.5, x + width - 0.5, y + height - 0.5, getColor(0x3C3C3D, 255));
        Gui.drawRect(x + 1, y + 1, x + width - 1, y + height - 1, getColor(0x282829, 255));
        Gui.drawRect(x + 2.5, y + 2.5, x + width - 2.5, y + height - 2.5, getColor(0x3C3C3D, 255));
        Gui.drawRect(x + 3, y + 3, x + width - 3, y + height - 3, getColor(0x151515, 255));

        if (chainmail) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(x + 3, y + 3, width - 6, height - 6);
            RenderUtil.drawImage(x + 3, y + 3, 325, 275, skeetChainMail);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        if (gradient) {
            Gui.drawGradientRectSideways(x + 3 + 0.5, y + 3 + 0.5, x + width / 2, y + 3 + 1.5, getColor(0x319ABD, 255), getColor(0xC34FB8, 255));
            Gui.drawGradientRectSideways(x + width / 2, y + 3 + 0.5, x + width - 3 - 0.5, y + 3 + 1.5, getColor(0xC34FB8, 255), getColor(0xC3D833, 255));
        }
    }

    public static float getDeltaTime() {
        int targetedFps = 120;
        int fps = Minecraft.getDebugFPS();
        if (fps == 0) {
            return 0;
        }
        return targetedFps / (float) fps;
    }

    public static int darker(int color, float factor) {
        int r = (int) ((color >> 16 & 0xFF) * factor);
        int g = (int) ((color >> 8 & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        int a = color >> 24 & 0xFF;

        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF) | ((a & 0xFF) << 24);
    }

    public static void drawBoundingBox(AxisAlignedBB boundingBox, Color color, boolean outlined, boolean filled, float rotateAngle) {
        GL11.glPushMatrix();
        double red = color.getRed() / 255f;
        double green = color.getGreen() / 255f;
        double blue = color.getBlue() / 255f;
        double alpha = color.getAlpha() / 255f;
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4d(red, green, blue, alpha);

        boundingBox = boundingBox.offset(-MC.getRenderManager().getRenderPosX(), -MC.getRenderManager().getRenderPosY(), -MC.getRenderManager().getRenderPosZ());

        if (outlined) {
            GL11.glBegin(GL_LINE_STRIP);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(GL_LINE_STRIP);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(GL_LINES);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glEnd();
        }
        if (filled) {
            GL11.glBegin(GL_QUADS);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glEnd();
            GL11.glBegin(GL_QUADS);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glEnd();
            GL11.glBegin(GL_QUADS);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(GL_QUADS);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(GL_QUADS);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(GL_QUADS);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glEnd();
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    /**
     * Pls just ignore this
     */
    public static void drawAnime() {
        ClickGuiFeature clickGui = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ClickGuiFeature.class);
        /* https://a.mg-renders.net/2017/01/high-school-dxd-rias-gremory-render-196.html for anime character*/
        if (clickGui.backgroundSettings.isEnabled("Anime")) {
            switch (clickGui.anime.getValue()) {
                case REM:
                    RenderUtil.drawImage(clickGui.animeX.getValue(), 130, 366, 489, new ResourceLocation("myth/waifu/rem.png"));
                    break;
                case REM2:
                    RenderUtil.drawImage(clickGui.animeX.getValue(), 100, 369, 676, new ResourceLocation("myth/waifu/rem2.png"));
                    break;
                case ASNA:
                    RenderUtil.drawImage(clickGui.animeX.getValue(), 130, 435, 574, new ResourceLocation("myth/waifu/Asna.png"));
                    break;
                case SCHOOLGIRL:
                    RenderUtil.drawImage(clickGui.animeX.getValue(), 130, 422, 591, new ResourceLocation("myth/waifu/SchoolGirl.png"));
                    break;
                case KIRIGAYA:
                    RenderUtil.drawImage(clickGui.animeX.getValue(), 130, 422, 591, new ResourceLocation("myth/waifu/Kirigaya.png"));
                    break;
                case MIKU:
                    RenderUtil.drawImage(clickGui.animeX.getValue(), 130, 369, 676, new ResourceLocation("myth/waifu/Miku.png"));
                    break;
                case SHIINAMASHIRO:
                    RenderUtil.drawImage(clickGui.animeX.getValue(), 130, 369, 676, new ResourceLocation("myth/waifu/Shiina Mashiro.png"));
                    break;
                case AKENO:
                    RenderUtil.drawImage(clickGui.animeX.getValue(), 130, 422, 591, new ResourceLocation("myth/waifu/Akeno.png"));
                    break;
                case MISAKA:
                    RenderUtil.drawImage(clickGui.animeX.getValue(), 130, 422, 591, new ResourceLocation("myth/waifu/Misaka.png"));
                    break;
            }
        }
    }

    public int getColor(int color, int alpha) {
        return color & 0x00FFFFFF | alpha << 24;
    }
}
