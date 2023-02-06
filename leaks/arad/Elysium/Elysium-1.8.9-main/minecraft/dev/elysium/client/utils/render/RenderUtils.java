package dev.elysium.client.utils.render;

import dev.elysium.client.Elysium;
import dev.elysium.client.mods.impl.settings.Optimization;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class RenderUtils {

    public static ResourceLocation white = new ResourceLocation("Elysium/white.png");
    private static final Frustum frustrum = new Frustum();

    private final static IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final static FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private final static FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);

    public static boolean isInViewFrustrum(Entity entity) {
        return isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    public static void drawSkeetoRect(double x,double y, double x2, double y2) {
        Gui.drawRect(x, y, x2, y2, 0xFF0a0a0a);
        double increment = .5;
        Gui.drawRect(x + increment, y + increment, x2- increment, y2 - increment, 0xFF3c3c3c);
        increment = 1;
        Gui.drawRect(x + increment, y + increment, x2 - increment, y2 - increment, 0xFF222222);
        increment = 2.5;
        Gui.drawRect(x + increment, y + increment, x2 - increment, y2 - increment, 0xFF3c3c3c);
        increment = 3;
        Gui.drawRect(x + increment, y + increment, x2 - increment, y2 - increment, 0xFF161616);
    }

    public static void drawRoundedRectNoTopRounding(double x, double y, double x2, double y2, double roundFactor, int color) {
        GlStateManager.pushMatrix();
        double width = x2 - x;
        double height = y2 - y;
        Gui.drawRect(x, y + height / roundFactor,x + height / roundFactor,y + height - height / roundFactor, color);
        Gui.drawRect(x + width - (height / roundFactor), y + height / roundFactor,x + width,y + height - height / roundFactor, color);
        Gui.drawRect(x + height / roundFactor, y + (height / roundFactor), x + width - height / roundFactor, y + height, color);
        //drawCircle(x + height / roundFactor, y + height / roundFactor, height / roundFactor, 180,270, color);
        drawCircle(x + height / roundFactor, y + height - height / roundFactor, 270, 360, height / roundFactor, color);
        //drawCircle(x + width - height / roundFactor, y + height / roundFactor, height / roundFactor, 90, 180, color);
        drawCircle(x + width - height / roundFactor, y + height - height / roundFactor, 0, 90, height / roundFactor, color);
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1);
    }

    public static void drawRoundedRectNoBottomRounding(double x, double y, double x2, double y2, double d, int color) {
        GlStateManager.pushMatrix();
        double width = x2 - x;
        double height = y2 - y;
        Gui.drawRect(x, y + height / d,x + height / d,y + height - height / d, color);
        Gui.drawRect(x + width - (height / d), y + height / d,x + width,y + height - height / d, color);
        Gui.drawRect(x + height / d, y, x + width - height / d, y + height - (height / d), color);
        drawCircle(x + height / d, y + height / d, 180,270, height / d, color);
        //drawCircle(x + height / roundFactor, y + height - height / roundFactor, height / roundFactor, 270, 360, color);
        drawCircle(x + width - height / d, y + height / d, 90, 180, height / d, color);
        //drawCircle(x + width - height / roundFactor, y + height - height / roundFactor, height / roundFactor, 0, 90, color);
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1);
    }

    private static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = Minecraft.getMinecraft().getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor)
    {
        float start1 = (float)(startColor >> 24 & 255) / 255.0F;
        float start2 = (float)(startColor >> 16 & 255) / 255.0F;
        float start3 = (float)(startColor >> 8 & 255) / 255.0F;
        float start4 = (float)(startColor & 255) / 255.0F;
        float end1 = (float)(endColor >> 24 & 255) / 255.0F;
        float end2 = (float)(endColor >> 16 & 255) / 255.0F;
        float end3 = (float)(endColor >> 8 & 255) / 255.0F;
        float end4 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)right, (double)top, (double)0).color(start2, start3, start4, start1).endVertex();
        worldrenderer.pos((double)left, (double)top, (double)0).color(start2, start3, start4, start1).endVertex();
        worldrenderer.pos((double)left, (double)bottom, (double)0).color(end2, end3, end4, end1).endVertex();
        worldrenderer.pos((double)right, (double)bottom, (double)0).color(end2, end3, end4, end1).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawGradientRectHorizontal(double left, double top, double right, double bottom, int startColor, int endColor)
    {
        float start1 = (float)(startColor >> 24 & 255) / 255.0F;
        float start2 = (float)(startColor >> 16 & 255) / 255.0F;
        float start3 = (float)(startColor >> 8 & 255) / 255.0F;
        float start4 = (float)(startColor & 255) / 255.0F;
        float end1 = (float)(endColor >> 24 & 255) / 255.0F;
        float end2 = (float)(endColor >> 16 & 255) / 255.0F;
        float end3 = (float)(endColor >> 8 & 255) / 255.0F;
        float end4 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)right, (double)top, (double)0).color(start2, start3, start4, start1).endVertex();
        worldrenderer.pos((double)left, (double)top, (double)0).color(end2, end3, end4, end1).endVertex();
        worldrenderer.pos((double)left, (double)bottom, (double)0).color(end2, end3, end4, end1).endVertex();
        worldrenderer.pos((double)right, (double)bottom, (double)0).color(start2, start3, start4, start1).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    // bad code scrap
    /*public static void drawDiagnonalGradientRect(double left, double top, double right, double bottom, int startColor, int endColor, int cornerColor)
    {
        int rad = 10;

        GlStateManager.pushMatrix();
        Stencil.INSTANCE.start();
        Stencil.INSTANCE.setBuffer(true);

        Gui.drawRect(left, top, left + rad, top + rad, -1);
        Gui.drawRect(left, bottom, left + rad, bottom - rad, -1);
        Gui.drawRect(right - rad, bottom - rad, right, bottom, -1);
        Gui.drawRect(right - rad, top, right, top + rad, -1);

        Stencil.INSTANCE.cropOutside();

        float start1 = (float)(startColor >> 24 & 255) / 255.0F;
        float start2 = (float)(startColor >> 16 & 255) / 255.0F;
        float start3 = (float)(startColor >> 8 & 255) / 255.0F;
        float start4 = (float)(startColor & 255) / 255.0F;
        float end1 = (float)(endColor >> 24 & 255) / 255.0F;
        float end2 = (float)(endColor >> 16 & 255) / 255.0F;
        float end3 = (float)(endColor >> 8 & 255) / 255.0F;
        float end4 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)right, (double)top, (double)0).color(start2, start3, start4, start1).endVertex();
        worldrenderer.pos((double)left, (double)top, (double)0).color(end2, end3, end4, end1).endVertex();
        worldrenderer.pos((double)left, (double)bottom, (double)0).color(start2, start3, start4, start1).endVertex();
        worldrenderer.pos((double)right, (double)bottom, (double)0).color(end2, end3, end4, end1).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();

        Stencil.getInstance().stopLayer();
        GlStateManager.popMatrix();

        drawACircle(left + rad, top + rad, 180, 270, rad, cornerColor);
        drawACircle(left + rad, bottom - rad, 270, 360, rad, startColor);
        drawACircle(right - rad, top + rad, 90, 180, rad, startColor);
        drawACircle(bottom - rad, right - rad, 0, 90, rad, cornerColor);
    }*/

    public static void drawDiagnonalGradientRoundedRectOutline(double left, double top, double right, double bottom, int startColor, int endColor, int rounding) {
        int rad = rounding;
        double width = 1.6;

        GlStateManager.pushMatrix();
        Stencil.INSTANCE.start();
        Stencil.INSTANCE.setBuffer(true);

        drawARoundedRect(left + width, top + width, right - width, bottom - width, rad, -1);

        Stencil.getInstance().cropOutside();

        drawGradientRectHorizontal(left + rad, top, right - rad, top + width, endColor, startColor);
        drawGradientRectHorizontal(left + rad, bottom - width, right - rad, bottom, startColor, endColor);

        drawGradientRect(left, top + rad, left + width, bottom - rad, startColor, endColor);
        drawGradientRect(right - width, top + rad, right, bottom - rad, endColor, startColor);

        drawCircle(left + rad, top + rad, 180, 270, rad, startColor);
        drawCircle(right - rad, top + rad, 90, 180, rad, endColor);
        drawCircle(left + rad, bottom - rad, 270, 360, rad, endColor);
        drawCircle(right - rad, bottom - rad, 0, 90, rad, startColor);

        Stencil.getInstance().stopLayer();
        GlStateManager.popMatrix();
    }

    public static void drawGradientRoundedRectOutline(double left, double top, double right, double bottom, int startColor, int endColor, int rounding) {
        int rad = rounding;
        double width = 1.6;

        GlStateManager.pushMatrix();
        Stencil.INSTANCE.start();
        Stencil.INSTANCE.setBuffer(true);

        drawARoundedRect(left + width, top + width, right - width, bottom - width, rad, -1);

        Stencil.getInstance().cropOutside();

        drawGradientRect(left + rad, top, right - rad, top + width, endColor, startColor);
        drawGradientRect(left + rad, bottom - width, right - rad, bottom, startColor, endColor);

        drawGradientRect(left, top + rad, left + width, bottom - rad, startColor, endColor);
        drawGradientRect(right - width, top + rad, right, bottom - rad, startColor, endColor);

        drawCircle(left + rad, top + rad, 180, 270, rad, startColor);
        drawCircle(right - rad, top + rad, 90, 180, rad, startColor);
        drawCircle(left + rad, bottom - rad, 270, 360, rad, endColor);
        drawCircle(right - rad, bottom - rad, 0, 90, rad, endColor);

        Stencil.getInstance().stopLayer();
        GlStateManager.popMatrix();
    }

    public static void drawHorizontalGradientRoundedRectOutline(double left, double top, double right, double bottom, int startColor, int endColor, int rounding) {
        int rad = rounding;
        double width = 1;

        GlStateManager.pushMatrix();
        Stencil.INSTANCE.start();
        Stencil.INSTANCE.setBuffer(true);

        drawARoundedRect(left + width, top + width, right - width, bottom - width, rad, -1);

        Stencil.getInstance().cropOutside();

        drawGradientRectHorizontal(left + rad, top, right - rad, top + width, startColor, endColor);
        drawGradientRectHorizontal(left + rad, bottom - width, right - rad, bottom, startColor, endColor);

        drawGradientRect(left, top + rad, left + width, bottom - rad, endColor, endColor);
        drawGradientRect(right - width, top + rad, right, bottom - rad, startColor, startColor);

        drawCircle(left + rad, top + rad, 180, 270, rad, endColor);
        drawCircle(right - rad, top + rad, 90, 180, rad, startColor);
        drawCircle(left + rad, bottom - rad, 270, 360, rad, endColor);
        drawCircle(right - rad, bottom - rad, 0, 90, rad, startColor);

        Stencil.getInstance().stopLayer();
        GlStateManager.popMatrix();
    }

    public static void drawGradientRoundedRect(double x, double y, double x2, double y2, double radius, int color1, int color2) {
        GlStateManager.pushMatrix();
        double width = (float) Math.abs(x2 - x); double height = (float) Math.abs(y2 - y);

        radius = Math.min(radius, width/2);
        radius = Math.min(radius, height/2);

        Gui.drawRect(x+radius, y, x+width-radius, y+radius, color1);
        drawGradientRect(x, y+radius, x+width, y+height-radius, color1, color2);
        Gui.drawRect(x+radius, y+height-radius, x+width-radius, y+height, color2);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();

        if((color1 >> 24 & 255) < 254 && (color2 >> 24 & 255) < 254) {
            drawCircleO(x+radius, y+radius, 180, 270, radius, color1);
            drawCircleO(x+radius, y+height-radius, 270, 361, radius, color2);
            drawCircleO(x+width-radius, y+radius, 90, 181, radius, color1);
            drawCircleO(x+width-radius, y+height-radius, 0, 91, radius, color2);
        } else {
            drawCircle(x+radius, y+radius, 180, 270, radius, color1);
            drawCircle(x+radius, y+height-radius, 270, 361, radius, color2);
            drawCircle(x+width-radius, y+radius, 90, 181, radius, color1);
            drawCircle(x+width-radius, y+height-radius, 0, 91, radius, color2);
        }

        GlStateManager.popMatrix();
    }

    public static void drawTracerLine(double x, double y, double z,float width, int colour) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(width);
        glColor(colour);
        GL11.glBegin(2);
        GL11.glVertex3d(0, Minecraft.getMinecraft().thePlayer.getEyeHeight(), 0);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void scissor(double x, double y, double width, double height) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public static void drawCircleSmall(double x, double y, int from, int too, float radius, int color) {
        GlStateManager.pushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBegin(GL11.GL_POLYGON);
        glColor(color);
        for (int i = from; i <= too; i++) {
            double x2 = Math.sin(((i * Math.PI) / 180)) * radius;
            double y2 = Math.cos(((i * Math.PI) / 180)) * radius;
            GL11.glVertex2d(x + x2, y + y2);
        }
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.popMatrix();
    }

    public static void drawChromaString(String text, float x,float y) {
        float xTmp = x;
        for (char textChar : text.toCharArray())
        {
            long index = (long) (xTmp * 80 + y * 80);

            int arraycolor = ColorUtil.getRainbow(8, 0.5F, 1, index);
            String tmp = String.valueOf(textChar);
            Minecraft.getMinecraft().fontRendererObj.drawStringBorder(tmp, xTmp, y, arraycolor, 0.5F);
            xTmp += Minecraft.getMinecraft().fontRendererObj.getStringWidth(tmp);
        }
    }

    public static void drawFilledBox(AxisAlignedBB mask) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.startDrawingQuads();
        worldRenderer.pos(mask.minX, mask.minY, mask.minZ);
        worldRenderer.pos(mask.minX, mask.maxY, mask.minZ);
        worldRenderer.pos(mask.maxX, mask.minY, mask.minZ);
        worldRenderer.pos(mask.maxX, mask.maxY, mask.minZ);
        worldRenderer.pos(mask.maxX, mask.minY, mask.maxZ);
        worldRenderer.pos(mask.maxX, mask.maxY, mask.maxZ);
        worldRenderer.pos(mask.minX, mask.minY, mask.maxZ);
        worldRenderer.pos(mask.minX, mask.maxY, mask.maxZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.pos(mask.maxX, mask.maxY, mask.minZ);
        worldRenderer.pos(mask.maxX, mask.minY, mask.minZ);
        worldRenderer.pos(mask.minX, mask.maxY, mask.minZ);
        worldRenderer.pos(mask.minX, mask.minY, mask.minZ);
        worldRenderer.pos(mask.minX, mask.maxY, mask.maxZ);
        worldRenderer.pos(mask.minX, mask.minY, mask.maxZ);
        worldRenderer.pos(mask.maxX, mask.maxY, mask.maxZ);
        worldRenderer.pos(mask.maxX, mask.minY, mask.maxZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.pos(mask.minX, mask.maxY, mask.minZ);
        worldRenderer.pos(mask.maxX, mask.maxY, mask.minZ);
        worldRenderer.pos(mask.maxX, mask.maxY, mask.maxZ);
        worldRenderer.pos(mask.minX, mask.maxY, mask.maxZ);
        worldRenderer.pos(mask.minX, mask.maxY, mask.minZ);
        worldRenderer.pos(mask.minX, mask.maxY, mask.maxZ);
        worldRenderer.pos(mask.maxX, mask.maxY, mask.maxZ);
        worldRenderer.pos(mask.maxX, mask.maxY, mask.minZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.pos(mask.minX, mask.minY, mask.minZ);
        worldRenderer.pos(mask.maxX, mask.minY, mask.minZ);
        worldRenderer.pos(mask.maxX, mask.minY, mask.maxZ);
        worldRenderer.pos(mask.minX, mask.minY, mask.maxZ);
        worldRenderer.pos(mask.minX, mask.minY, mask.minZ);
        worldRenderer.pos(mask.minX, mask.minY, mask.maxZ);
        worldRenderer.pos(mask.maxX, mask.minY, mask.maxZ);
        worldRenderer.pos(mask.maxX, mask.minY, mask.minZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.pos(mask.minX, mask.minY, mask.minZ);
        worldRenderer.pos(mask.minX, mask.maxY, mask.minZ);
        worldRenderer.pos(mask.minX, mask.minY, mask.maxZ);
        worldRenderer.pos(mask.minX, mask.maxY, mask.maxZ);
        worldRenderer.pos(mask.maxX, mask.minY, mask.maxZ);
        worldRenderer.pos(mask.maxX, mask.maxY, mask.maxZ);
        worldRenderer.pos(mask.maxX, mask.minY, mask.minZ);
        worldRenderer.pos(mask.maxX, mask.maxY, mask.minZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.pos(mask.minX, mask.maxY, mask.maxZ);
        worldRenderer.pos(mask.minX, mask.minY, mask.maxZ);
        worldRenderer.pos(mask.minX, mask.maxY, mask.minZ);
        worldRenderer.pos(mask.minX, mask.minY, mask.minZ);
        worldRenderer.pos(mask.maxX, mask.maxY, mask.minZ);
        worldRenderer.pos(mask.maxX, mask.minY, mask.minZ);
        worldRenderer.pos(mask.maxX, mask.maxY, mask.maxZ);
        worldRenderer.pos(mask.maxX, mask.minY, mask.maxZ);
        tessellator.draw();
    }

    public static void drawItemStack(ItemStack stack, int x, int y, float scale)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - scale*1.5, y - scale*1.5, 1);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(-x - scale*1.5, -y - scale*1.5, 1);

        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, x, y);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public static void drawRoundedRect(double x, double y, double x2, double y2, int roundFactor, int color) {
        double width = x2 - x;
        double height = y2 - y;
        Gui.drawRect(x, y + height / roundFactor,x + height / roundFactor,y + height - height / roundFactor, color);
        Gui.drawRect(x + width - (height / roundFactor), y + height / roundFactor,x + width,y + height - height / roundFactor, color);
        Gui.drawRect(x + height / roundFactor, y, x + width - height / roundFactor, y + height, color);
        drawCircleO(x + height / roundFactor, y + height / roundFactor, 180, 270, (height / roundFactor), color);
        drawCircleO(x + height / roundFactor, y + height - height / roundFactor, 270, 360,height / roundFactor, color);
        drawCircleO(x + width - height / roundFactor, y + height / roundFactor,  90, 180, height / roundFactor,color);
        drawCircleO(x + width - height / roundFactor, y + height - height / roundFactor,  0, 90, height / roundFactor, color);
    }

    public static void drawOutLinedRoundedRect(float x, float y, float x2, float y2, int roundFactor, int color) {
        float width = x2 - x;
        float height = y2 - y;
        Gui.drawRect(x, y + height / roundFactor,x + 2,y + height - height / roundFactor, color);
        Gui.drawRect(x + width - (height / roundFactor)+3, y + height / roundFactor,x + width - (height / roundFactor)+5,y + height - height / roundFactor, color);
        Gui.drawRect(x + height / roundFactor, y, x + width - height / roundFactor, y + 2, color);
        Gui.drawRect(x + height / roundFactor, y + height - 2, x + width - height / roundFactor, y + height, color);
        drawOutLineCircle(x + height / roundFactor, y + height / roundFactor, 180,270, height / roundFactor, color);
        drawOutLineCircle(x + height / roundFactor, y + height - height / roundFactor, 270, 360, height / roundFactor, color);
        drawOutLineCircle(x + width - height / roundFactor, y + height / roundFactor, 90, 180, height / roundFactor, color);
        drawOutLineCircle(x + width - height / roundFactor, y + height - height / roundFactor, 0, 90, height / roundFactor, color);
    }

    public static void drawARoundedRect(double x, double y, double x2, double y2, double radius, int color) {
        float width = (float) Math.abs(x2 - x); float height = (float) Math.abs(y2 - y);

        radius = Math.min(radius, width/2);
        radius = Math.min(radius, height/2);

        Gui.drawRect(x, y+radius, x+width, y+height-radius, color);
        Gui.drawRect(x+radius, y, x+width-radius, y+radius, color);
        Gui.drawRect(x+radius, y+height-radius, x+width-radius, y+height, color);

        drawCircle((float) (x+radius), y+radius, 180,271, radius, color);
        drawCircle(x+radius, y+height-radius, 270, 361, radius, color);
        drawCircle(x+width-radius, y+radius, 90, 181, radius, color);
        drawCircle(x+width-radius, y+height-radius, 0, 91, radius, color);
    }

    public static void drawACircleSmall(double x, double y, int start, int end, double radius, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(0.5, 0.5, 1);
        GlStateManager.translate(-x, -y, 0);
        if((color | 127 << 24) == color) {
            RenderUtils.drawCircleWithTexture(x, y, start, end, radius, white, color);
            RenderUtils.drawCircleWithTexture(x, y, start, end, radius, white, color);
            RenderUtils.drawCircleWithTexture(x, y, start, end, radius, white, color);
        }
        RenderUtils.drawCircleWithTexture(x, y, start, end, radius, white, color);
        GlStateManager.popMatrix();
    }

    public static void drawARoundedRectWithBorder(double x, double y, double x2, double y2, double radius, int color, float borderThickness, int borderColor) {
        drawARoundedRect(x-borderThickness, y-borderThickness, x2+borderThickness, y2+borderThickness, radius, borderColor);
        drawARoundedRect(x, y, x2, y2, radius, color);
    }

    public static void drawACircle(double x, double y, int start, int end, double radius, int color) {
        if((color | 127 << 24) == color) {
            RenderUtils.drawCircleWithTexture(x, y, start, end, radius, white, color);
            RenderUtils.drawCircleWithTexture(x, y, start, end, radius, white, color);
            RenderUtils.drawCircleWithTexture(x, y, start, end, radius, white, color);
        }
        RenderUtils.drawCircleWithTexture(x, y, start, end, radius, white, color);
    }

    public static void drawCircleWithTexture(double cX, double cY, int start, int end, double radius, ResourceLocation res, int color) {
        double radian, x, y, tx, ty, xsin, ycos;
        GL11.glPushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(res);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glColor(color);
        GlStateManager.enableAlpha();//----
        GL11.glBegin(GL11.GL_POLYGON);
        for (int i = start; i < end; ++i) {
            radian = i * (Math.PI / 180.0f);
            xsin = Math.sin(radian);
            ycos = Math.cos(radian);

            x = xsin * radius;
            y = ycos * radius;

            tx = xsin * 0.5 + 0.5;
            ty = ycos * 0.5 + 0.5;

            GL11.glTexCoord2d(cX + tx, cY + ty);
            GL11.glVertex2d(cX + x, cY + y);
        }
        GL11.glTexCoord2d(cX, cY);
        GL11.glVertex2d(cX, cY);
        GL11.glEnd();
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        GL11.glPopMatrix();
        GlStateManager.popMatrix();
        GlStateManager.resetColor();
    }

    public static Vector3d project(double x, double y, double z) {
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(2982, modelview);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        if (GLU.gluProject((float) x, (float) y, (float) z, modelview, projection, viewport, vector)) {
            return new Vector3d(vector.get(0) / getResolution().getScaleFactor(), (Display.getHeight() - vector.get(1)) / getResolution().getScaleFactor(), vector.get(2));
        }
        return null;
    }

    public static ScaledResolution getResolution() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static void drawCircle(double x, double y, double radius, int color) {
        drawCircle(x, y, 0, 360, radius, color);
    }

    public static void drawCircle(double x, double y, int from, int too, double radius, int color) {
        if(((Optimization) Elysium.getInstance().getModManager().getModByName("Optimization")).smoothCircles.isEnabled()) {
            drawACircle(x, y, from, too, radius, color); return;
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBegin(GL11.GL_POLYGON);
        glColor(color);
        for (int i = from; i <= too; i++) {
            double x2 = Math.sin(((i * Math.PI) / 180)) * radius;
            double y2 = Math.cos(((i * Math.PI) / 180)) * radius;
            GL11.glVertex2d(x + x2, y + y2);
        }
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void drawCircleO(double x, double y, int from, int too, double radius, int color) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBegin(GL11.GL_POLYGON);
        glColor(color);
        for (int i = from; i <= too; i++) {
            double x2 = Math.sin(((i * Math.PI) / 180)) * radius;
            double y2 = Math.cos(((i * Math.PI) / 180)) * radius;
            GL11.glVertex2d(x + x2, y + y2);
        }
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /*
     *
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
     */

    public static void drawOutLineCircle(double x, double y, int from, int too, double radius, int color) {
        double radian, cx, cy, tx, ty, xsin, ycos;
        glColor(color);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        radius -= 1;
        for (int i = from; i < too; ++i) {
            cx = Math.sin(((i * Math.PI) / 180)) * radius;
            cy = Math.cos(((i * Math.PI) / 180)) * radius;
            GL11.glVertex2d(x + cx, y + cy);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void glColor(int hex) {
        float alpha = (float) (hex >> 24 & 255) / 255F;
        float red = (float) (hex >> 16 & 255) / 255F;
        float green = (float) (hex >> 8 & 255) / 255F;
        float blue = (float) (hex & 255) / 255F;
        GL11.glColor4f(red, green, blue, alpha);
    }
}





