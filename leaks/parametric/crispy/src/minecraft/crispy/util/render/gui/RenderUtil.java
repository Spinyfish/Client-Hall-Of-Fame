package crispy.util.render.gui;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.minecraft.client.renderer.GlStateManager.enableBlend;
import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {


    private final static Frustum frustrum = new Frustum();
    public static Minecraft mc = Minecraft.getMinecraft();

    public final static void enable(int glTarget) {
        glEnable(glTarget);
    }

    public final static void disable(int glTarget) {
        glDisable(glTarget);
    }

    public final static void vertex(double x, double y) {
        glVertex2d(x, y);
    }

    public final static void start() {
        enable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        disable(GL_TEXTURE_2D);
        disable(GL_CULL_FACE);
        GlStateManager.disableAlpha();
    }

    public final static void stop() {
        GlStateManager.enableAlpha();
        enable(GL_CULL_FACE);
        enable(GL_TEXTURE_2D);
        disable(GL_BLEND);
        color(Color.white);
    }

    public final static void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }

    public final static void color(double red, double green, double blue, double alpha) {
        glColor4d(red, green, blue, alpha);
    }

    public static void drawLine(double x, double y, double endX, double endY, float lineWidth, int color) {
        GlStateManager.disableTexture2D();
        glColor(color);
        glBegin(3);
        glVertex2d(x, y);
        glVertex2d(endX, endY);
        glEnd();
        glColor4d(1, 1, 1, 1);
        GlStateManager.enableTexture2D();
    }

    public final static void gradientSideways(double x, double y, double width, double height, boolean filled, Color color1, Color color2) {
        start();
        glShadeModel(GL_SMOOTH);
        GlStateManager.disableAlpha();
        if (color1 != null)
            color(color1);
        glBegin(filled ? GL_TRIANGLE_FAN : GL_LINES);
        {
            vertex(x, y);
            vertex(x, height);
            if (color2 != null)
                color(color2);
            vertex(width, height);
            vertex(width, y);
        }
        glEnd();
        GlStateManager.enableAlpha();
        glShadeModel(GL_FLAT);
        stop();
    }

    public static final void gradientSideways(double x, double y, double width, double height, Color color1, Color color2) {
        gradientSideways(x, y, width, height, true, color1, color2);
    }

    public static void glColor(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255.0f;
        final float red = (hex >> 16 & 0xFF) / 255.0f;
        final float green = (hex >> 8 & 0xFF) / 255.0f;
        final float blue = (hex & 0xFF) / 255.0f;
        glColor4f(red, green, blue, alpha);
    }

    public static void drawFullCircle(float cx, float cy, float r, final int c) {
        r *= 2.0f;
        cx *= 2.0f;
        cy *= 2.0f;
        final float theta = 0.19634953f;
        final float p = (float) Math.cos(theta);
        final float s = (float) Math.sin(theta);
        float x = r;
        float y = 0.0f;
        enableGL2D();
        glEnable(2848);
        glHint(3154, 4354);
        glEnable(3024);
        glScalef(0.5f, 0.5f, 0.5f);
        glColor(c);
        glBegin(9);
        for (int ii = 0; ii < 32; ++ii) {
            glVertex2f(x + cx, y + cy);
            float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
        }
        glEnd();
        glScalef(2.0f, 2.0f, 2.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        disableGL2D();
    }

    static void enableGL2D() {
        glDisable(2929);
        glEnable(3042);
        glDisable(3553);
        glBlendFunc(770, 771);
        glDepthMask(true);
        glEnable(2848);
        glHint(3154, 4354);
        glHint(3155, 4354);
    }

    public static void disableGL2D() {
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        glHint(3154, 4352);
        glHint(3155, 4352);
    }


    /*public static void drawCircle(float cx, float cy, float r, final int num_segments, final int c) {
        glPushMatrix();
        cx *= 2.0f;
        cy *= 2.0f;
        final float f = (c >> 24 & 0xFF) / 255.0f;
        final float f2 = (c >> 16 & 0xFF) / 255.0f;
        final float f3 = (c >> 8 & 0xFF) / 255.0f;
        final float f4 = (c & 0xFF) / 255.0f;
        final float theta = (float)(6.2831852 / num_segments);
        final float p = (float)Math.cos(theta);
        final float s = (float)Math.sin(theta);
        float x;
        r = (x = r * 2.0f);
        float y = 0.0f;
        enableGL2D();
        glScalef(0.5f, 0.5f, 0.5f);
        glColor4f(f2, f3, f4, f);
        glBegin(2);
        for (int ii = 0; ii < num_segments; ++ii) {
            glVertex2f(x + cx, y + cy);
            final float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
        }
        glEnd();
        glScalef(2.0f, 2.0f, 2.0f);
        disableGL2D();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        glPopMatrix();
    }

    public static void drawBorderedCircle(final float circleX, final float circleY, final double radius, final double width, final int borderColor, final int innerColor) {
        enableGL2D();
        GlStateManager.enableBlend();
        glEnable(2881);
        drawCircle(circleX, circleY, (float)(radius - 0.5 + width), 72, borderColor);
        drawFullCircle(circleX, circleY, (float)radius, innerColor);
        GlStateManager.disableBlend();
        glDisable(2881);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        disableGL2D();
    }*/
    public static void drawCircle(double x, double y, float radius, int color) {
        GlStateManager.disableTexture2D();

        glEnable(GL_BLEND);
        glColor(color);
        glEnable(GL_LINE_SMOOTH);
        glBegin(3);
        for (double i = 0; i <= 360; i += 1) {
            if (i > 360) {
                break;
            }
            double theta = i * Math.PI / 180;
            glVertex2d(radius * Math.cos(theta) + x, radius * Math.sin(theta) + y);
        }
        glEnd();
        glDisable(GL_LINE_SMOOTH);

        glColor4d(1, 1, 1, 1);
        GlStateManager.enableTexture2D();
    }

    public static void drawBorderedCircle(double x, double y, float radius, int outsideC, int insideC) {
        //  glEnable((int)3042);
        glDisable(3553);
        glBlendFunc(770, 771);
        glEnable(2848);
        glPushMatrix();
        float scale = 0.1f;
        glScalef(0.1f, 0.1f, 0.1f);
        drawCircle(x *= 10, y *= 10, radius *= 10.0f, insideC);
        // drawUnfilledCircle(x, y, radius, 1.0f, outsideC);
        glScalef(10.0f, 10.0f, 10.0f);
        glPopMatrix();
        glEnable(3553);
        //  glDisable((int)3042);
        glDisable(2848);
    }

    public static void drawAxisAlignedBBFilled(AxisAlignedBB axisAlignedBB, int color, boolean depth) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        if (depth) glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        Draw.color(color);
        drawBoxFilled(axisAlignedBB);
        GlStateManager.resetColor();
        glEnable(GL_TEXTURE_2D);
        if (depth) glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);
    }

    public static void drawBox(BlockPos pos, int color, boolean depth) {
        final RenderManager renderManager = mc.getRenderManager();
        final Timer timer = mc.timer;

        final double x = pos.getX() - RenderManager.renderPosX;
        final double y = pos.getY() - RenderManager.renderPosY;
        final double z = pos.getZ() - RenderManager.renderPosZ;

        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        final Block block = Minecraft.theWorld.getBlockState(pos).getBlock();

        if (block != null) {
            final EntityPlayer player = mc.thePlayer;

            final double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) timer.renderPartialTicks;
            final double posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) timer.renderPartialTicks;
            final double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) timer.renderPartialTicks;
            axisAlignedBB = block.getSelectedBoundingBox(Minecraft.theWorld, pos).expand(.002, .002, .002).offset(-posX, -posY, -posZ);

            drawAxisAlignedBBFilled(axisAlignedBB, color, depth);
        }
    }

    public static void drawBoxFilled(AxisAlignedBB axisAlignedBB) {
        glBegin(GL_QUADS);
        {
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);

            glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);

            glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);

            glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);

            glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);

            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);

            glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);

            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);

            glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);

            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);

            glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);

            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
            glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
            glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        }
        glEnd();
    }


    public static double[] interpolate(Entity entity) {
        double partialTicks = mc.timer.renderPartialTicks;
        return new double[]{entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks};
    }


    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    private static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }


    public static void drawHsvScale(double left, double top, double right, double bottom) {
        float width = (float) (right - left);
        for (float i = 0; i < width; i++) {
            double posX = left + i;
            int color = Color.getHSBColor(i / width, 1, 1).getRGB();
            Gui.drawRect(posX, top, posX + 1, bottom, color);
        }
    }

    public static void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft(), mc.displayWidth, mc.displayHeight);
        int factor = scale.getScaleFactor();
        glScissor((int) (x * (float) factor), (int) (((float) ScaledResolution.getScaledHeight() - y2) * (float) factor), (int) ((x2 - x) * (float) factor), (int) ((y2 - y) * (float) factor));
    }

    public static void drawBorderedRect(double left, double top, double right, double bottom, double borderWidth, int insideColor, int borderColor) {
        Gui.drawRect(left - borderWidth, top - borderWidth, right + borderWidth, bottom + borderWidth, borderColor);
        Gui.drawRect(left, top, right, bottom, insideColor);
    }


    public static void drawRoundedRect(double left, double top, double right, double bottom, double radius, int color) {
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        left *= 2.0D;
        top *= 2.0D;
        right *= 2.0D;
        bottom *= 2.0D;
        GL11.glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        enableBlend();
        glColor(color);
        //GlStateManager.enableAlpha();
        //GlStateManager.enableBlend();
        GL11.glBegin(9);
        int i;
        for (i = 0; i <= 90; i += 1)
            GL11.glVertex2d(left + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, top + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 1)
            GL11.glVertex2d(left + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, bottom - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 1)
            GL11.glVertex2d(right - radius + Math.sin(i * Math.PI / 180.0D) * radius, bottom - radius + Math.cos(i * Math.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 1)
            GL11.glVertex2d(right - radius + Math.sin(i * Math.PI / 180.0D) * radius, top + radius + Math.cos(i * Math.PI / 180.0D) * radius);
        GL11.glEnd();
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        glColor4d(1, 1, 1, 1);
//        GlStateManager.enableAlpha();
//        GlStateManager.enableBlend();
//        GL11.glPopAttrib();

    }

    public static void drawLeftRounded(double left, double top, double right, double bottom, double radius, int color) {
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        left *= 2.0D;
        top *= 2.0D;
        right *= 2.0D;
        bottom *= 2.0D;
        GL11.glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        enableBlend();
        glColor(color);
//        GlStateManager.enableAlpha();
//        GlStateManager.enableBlend();
        GL11.glBegin(9);
        int i;
        for (i = 0; i <= 90; i += 1)
            GL11.glVertex2d(left + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, top + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 1)
            GL11.glVertex2d(left + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, bottom - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right,top);
        GL11.glEnd();
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        glColor4d(1, 1, 1, 1);
//        GlStateManager.enableAlpha();
//        GlStateManager.enableBlend();
//        GL11.glPopAttrib();
    }


    public static void drawBorder(double left, double top, double width, double height, double lineWidth, int color) {
        Gui.drawRect(left, top, left + width, top + lineWidth, color);
        Gui.drawRect(left, top, left + lineWidth, top + height, color);
        Gui.drawRect(left, top + height - lineWidth, left + width, top + height, color);
        Gui.drawRect(left + width - lineWidth, top, left + width, top + height, color);
    }

    public static void startSmooth() {
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POLYGON_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
    }

    public static void endSmooth() {
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POLYGON_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
    }

    public static Color fade(final Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);

        float brightness = Math.abs((((System.currentTimeMillis() % 2000) / 1000F + (index / (float) count) * 2F) % 2F) - 1);
        brightness = 0.5f + (0.5f * brightness);

        hsb[2] = brightness % 2F;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static void scissor(double x, double y, double width, double height) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), mc.displayWidth, mc.displayHeight);
        final double scale = sr.getScaleFactor();

        y = ScaledResolution.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public static int getOppositeColor(int color) {
        int R = color & 255;
        int G = (color >> 8) & 255;
        int B = (color >> 16) & 255;
        int A = (color >> 24) & 255;
        R = 255 - R;
        G = 255 - G;
        B = 255 - B;
        return R + (G << 8) + (B << 16) + (A << 24);
    }

}