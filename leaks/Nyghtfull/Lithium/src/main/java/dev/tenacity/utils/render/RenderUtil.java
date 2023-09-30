package dev.tenacity.utils.render;

import dev.tenacity.utils.Utils;
import dev.tenacity.utils.animations.Animation;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static dev.tenacity.utils.misc.MathUtils.interpolate;
import static org.lwjgl.opengl.GL11.*;

public class RenderUtil implements Utils {

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight;
    }

    public static void drawTracerLine(Entity entity, float width, Color color, float alpha) {
        float ticks = mc.timer.renderPartialTicks;
        glPushMatrix();

        glLoadIdentity();

        mc.entityRenderer.orientCamera(ticks);
        double[] pos = ESPUtil.getInterpolatedPos(entity);

        glDisable(GL_DEPTH_TEST);
        GLUtil.setup2DRendering();

        double yPos = pos[1] + entity.height / 2f;
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(width);

        glBegin(GL_LINE_STRIP);
        color(color.getRGB(), alpha);
        glVertex3d(pos[0], yPos, pos[2]);
        glVertex3d(0, mc.thePlayer.getEyeHeight(), 0);
        glEnd();

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_DEPTH_TEST);

        GLUtil.end2DRendering();

        glPopMatrix();
    }

    public static void drawMicrosoftLogo(float x, float y, float size, float spacing, float alpha) {
        float rectSize = size /2f - spacing;
        int alphaVal = (int) (255 * alpha);
        Gui.drawRect2(x, y, rectSize, rectSize, new Color(244, 83, 38, alphaVal).getRGB());
        Gui.drawRect2(x + rectSize + spacing, y, rectSize, rectSize, new Color(130, 188, 6, alphaVal).getRGB());
        Gui.drawRect2(x, y + spacing + rectSize, rectSize, rectSize, new Color(5, 166, 241, alphaVal).getRGB());
        Gui.drawRect2(x + rectSize + spacing, y + spacing + rectSize, rectSize, rectSize, new Color(254, 186, 7, alphaVal).getRGB());
    }

    public static void drawMicrosoftLogo(float x, float y, float size, float spacing) {
        drawMicrosoftLogo(x, y, size, spacing, 1f);
    }


    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float imgWidth, float imgHeight) {
        GLUtil.startBlend();
        mc.getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture(x,y,0,0, imgWidth, imgHeight, imgWidth, imgHeight);
        GLUtil.endBlend();
    }


    public static void fixBlendIssues() {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void drawUnfilledCircle(double x, double y, float radius, float lineWidth, int color) {
        GLUtil.setup2DRendering();
        color(color);
        glLineWidth(lineWidth);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_POINT_BIT);

        int i = 0;
        while (i <= 360) {
            glVertex2d(x + Math.sin((double) i * 3.141526 / 180.0) * (double) radius, y + Math.cos((double) i * 3.141526 / 180.0) * (double) radius);
            ++i;
        }

        glEnd();
        glDisable(GL_LINE_SMOOTH);
        GLUtil.end2DRendering();
    }


    public static double ticks = 0;
    public static long lastFrame = 0;

    public static void drawCircle(Entity entity, float partialTicks, double rad, int color, float alpha) {
        /*Got this from the people i made the Gui for*/
        ticks += .004 * (System.currentTimeMillis() - lastFrame);

        lastFrame = System.currentTimeMillis();

        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        GlStateManager.color(1, 1, 1, 1);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glShadeModel(GL_SMOOTH);
        GlStateManager.disableCull();

        final double x = interpolate(entity.lastTickPosX, entity.posX, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosX;
        final double y = interpolate(entity.lastTickPosY, entity.posY, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosY + Math.sin(ticks) + 1;
        final double z = interpolate(entity.lastTickPosZ, entity.posZ, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosZ;

        glBegin(GL_TRIANGLE_STRIP);

        for (float i = 0; i < (Math.PI * 2); i += (Math.PI * 2) / 64.F) {

            final double vecX = x + rad * Math.cos(i);
            final double vecZ = z + rad * Math.sin(i);

            color(color, 0);

            glVertex3d(vecX, y - Math.sin(ticks + 1) / 2.7f, vecZ);

            color(color, .52f * alpha);


            glVertex3d(vecX, y, vecZ);
        }

        glEnd();


        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(1.5f);
        glBegin(GL_LINE_STRIP);
        GlStateManager.color(1, 1, 1, 1);
        color(color, .5f * alpha);
        for (int i = 0; i <= 180; i++) {
            glVertex3d(x - Math.sin(i * MathHelper.PI2 / 90) * rad, y, z + Math.cos(i * MathHelper.PI2 / 90) * rad);
        }
        glEnd();

        glShadeModel(GL_FLAT);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        GlStateManager.enableCull();
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
        glColor4f(1f, 1f, 1f, 1f);
    }

    //From rise, alan gave me this
    public static void drawFilledCircleNoGL(int x, int y, double r, int c, int quality) {
        RenderUtil.resetColor();
        RenderUtil.setAlphaLimit(0);
        GLUtil.setup2DRendering();
        color(c);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            glVertex2d(x + x2, y + y2);
        }

        glEnd();
        GLUtil.end2DRendering();
    }

    public static void renderBoundingBox(EntityLivingBase entityLivingBase, Color color, float alpha) {
        AxisAlignedBB bb = ESPUtil.getInterpolatedBoundingBox(entityLivingBase);
        GlStateManager.pushMatrix();
        GLUtil.setup2DRendering();
        GLUtil.enableCaps(GL_BLEND, GL_POINT_SMOOTH, GL_POLYGON_SMOOTH, GL_LINE_SMOOTH);

        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(3);
        float actualAlpha = .3f * alpha;
        glColor4f(color.getRed(), color.getGreen(), color.getBlue(), actualAlpha);
        color(color.getRGB(), actualAlpha);
        RenderGlobal.renderCustomBoundingBox(bb, true, true);
        //or true
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);

        GLUtil.disableCaps();
        GLUtil.end2DRendering();

        GlStateManager.popMatrix();
    }

    public static void glVertex3D2(Vec3 vector3d) {
        GL11.glVertex3d(vector3d.xCoord, vector3d.yCoord, vector3d.zCoord);
    }

    public static void drawBoundingBox(final AxisAlignedBB aa) {

        glBegin(GL_QUADS);
        glVertex3D2(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D2(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        GL11.glEnd();

        glBegin(GL_QUADS);
        glVertex3D2(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D2(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D2(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D2(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        GL11.glEnd();

        glBegin(GL_QUADS);
        glVertex3D2(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D2(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        GL11.glEnd();

        glBegin(GL_QUADS);
        glVertex3D2(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D2(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        GL11.glEnd();

        glBegin(GL_QUADS);
        glVertex3D2(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D2(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D2(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        GL11.glEnd();

        glBegin(GL_QUADS);
        glVertex3D2(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D2(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D2(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        GL11.glEnd();
    }



    public static Vec3 getRenderPos(double x, double y, double z) {

        x -= mc.getRenderManager().renderPosX;
        y -= mc.getRenderManager().renderPosY;
        z -= mc.getRenderManager().renderPosZ;

        return new Vec3(x, y, z);
    }

    public static void circleNoSmoothRGB(double x, double y, double radius, int color) {
        radius /= 2;
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
        color(color);
        glBegin(GL_TRIANGLE_FAN);

        for (double i = 0; i <= 360; i++) {
            double angle = (i * (Math.PI * 2)) / 360;
            glVertex2d(x + (radius * Math.cos(angle)) + radius, y + (radius * Math.sin(angle)) + radius);
        }

        glEnd();
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
    }


    public static void drawBorderedRect(float x, float y, float width, float height, final float outlineThickness, int rectColor, int outlineColor) {
        Gui.drawRect2(x, y, width, height, rectColor);
        glEnable(GL_LINE_SMOOTH);
        color(outlineColor);

        GLUtil.setup2DRendering();

        glLineWidth(outlineThickness);
        float cornerValue = (float) (outlineThickness * .19);

        glBegin(GL_LINES);
        glVertex2d(x, y - cornerValue);
        glVertex2d(x, y + height + cornerValue);
        glVertex2d(x + width, y + height + cornerValue);
        glVertex2d(x + width, y - cornerValue);
        glVertex2d(x, y);
        glVertex2d(x + width, y);
        glVertex2d(x, y + height);
        glVertex2d(x + width, y + height);
        glEnd();

        GLUtil.end2DRendering();

        glDisable(GL_LINE_SMOOTH);
    }

    // Bad rounded rect method but the shader one requires scaling that sucks
    public static void renderRoundedRect(float x, float y, float width, float height, float radius, int color) {
        RenderUtil.drawGoodCircle(x + radius, y + radius, radius, color);
        RenderUtil.drawGoodCircle(x + width - radius, y + radius, radius, color);
        RenderUtil.drawGoodCircle(x + radius, y + height - radius, radius, color);
        RenderUtil.drawGoodCircle(x + width - radius, y + height - radius, radius, color);

        Gui.drawRect2(x + radius, y, width - radius * 2, height, color);
        Gui.drawRect2(x, y + radius, width, height - radius * 2, color);
    }


    // Scales the data that you put in the runnable
    public static void scaleStart(float x, float y, float scale) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        glScalef(scale, scale, 1);
        glTranslatef(-x, -y, 0);
    }

    public static void scaleEnd() {
        glPopMatrix();
    }


    // TODO: Replace this with a shader as GL_POINTS is not consistent with gui scales
    public static void drawGoodCircle(double x, double y, float radius, int color) {
        color(color);
        GLUtil.setup2DRendering();

        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glPointSize(radius * (2 * mc.gameSettings.guiScale));

        glBegin(GL_POINTS);
        glVertex2d(x, y);
        glEnd();

        GLUtil.end2DRendering();
    }

    public static void fakeCircleGlow(float posX, float posY, float radius, Color color, float maxAlpha) {
        setAlphaLimit(0);
        glShadeModel(GL_SMOOTH);
        GLUtil.setup2DRendering();
        color(color.getRGB(), maxAlpha);

        glBegin(GL_TRIANGLE_FAN);
        glVertex2d(posX, posY);
        color(color.getRGB(), 0);
        for (int i = 0; i <= 100; i++) {
            double angle = (i * .06283) + 3.1415;
            double x2 = Math.sin(angle) * radius;
            double y2 = Math.cos(angle) * radius;
            glVertex2d(posX + x2, posY + y2);
        }
        glEnd();

        GLUtil.end2DRendering();
        glShadeModel(GL_FLAT);
        setAlphaLimit(1);
    }

    // animation for sliders and stuff
    public static double animate(double endPoint, double current, double speed) {
        boolean shouldContinueAnimation = endPoint > current;
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }

        double dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        double factor = dif * speed;
        return current + (shouldContinueAnimation ? factor : -factor);
    }

    public static void rotateStart(float x, float y, float width, float height, float rotation) {
        glPushMatrix();
        x += width / 2;
        y += height / 3;
        glTranslatef(x, y, 0);
        glRotatef(rotation, 0, 0, 1);
        glTranslatef(-x, -y, 0);
    }

    public static void rotateStartReal(float x, float y, float width, float height, float rotation) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        glRotatef(rotation, 0, 0, 1);
        glTranslatef(-x, -y, 0);
    }

    public static void rotateEnd() {
        glPopMatrix();
    }

    // Arrow for clickgui
    public static void drawClickGuiArrow(float x, float y, float size, Animation animation, int color) {
        glTranslatef(x, y, 0);
        color(color);

        GLUtil.setup2DRendering();

        glBegin(GL_TRIANGLE_STRIP);
        double interpolation = interpolate(0.0, size / 2.0, animation.getOutput().floatValue());
        if (animation.getOutput().floatValue() >= .48) {
            glVertex2d(size / 2f, interpolate(size / 2.0, 0.0, animation.getOutput().floatValue()));
        }
        glVertex2d(0, interpolation);

        if (animation.getOutput().floatValue() < .48) {
            glVertex2d(size / 2f, interpolate(size / 2.0, 0.0, animation.getOutput().floatValue()));
        }
        glVertex2d(size, interpolation);

        glEnd();

        GLUtil.end2DRendering();

        glTranslatef(-x, -y, 0);
    }

    // Draws a circle using traditional methods of rendering
    public static void drawCircleNotSmooth(double x, double y, double radius, int color) {
        radius /= 2;
        GLUtil.setup2DRendering();
        glDisable(GL_CULL_FACE);
        color(color);
        glBegin(GL_TRIANGLE_FAN);

        for (double i = 0; i <= 360; i++) {
            double angle = i * .01745;
            glVertex2d(x + (radius * Math.cos(angle)) + radius, y + (radius * Math.sin(angle)) + radius);
        }

        glEnd();
        glEnable(GL_CULL_FACE);
        GLUtil.end2DRendering();
    }

    public static void scissor(double x, double y, double width, double height, Runnable data) {
        glEnable(GL_SCISSOR_TEST);
        scissor(x, y, width, height);
        data.run();
        glDisable(GL_SCISSOR_TEST);
    }

    public static void scissor(double x, double y, double width, double height) {
        ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = (sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        glScissor((int) finalX, (int) (finalY - finalHeight), (int) finalWidth, (int) finalHeight);
    }

    public static void scissorStart(double x, double y, double width, double height) {
        glEnable(GL_SCISSOR_TEST);
        ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = (sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        glScissor((int) finalX, (int) (finalY - finalHeight), (int) finalWidth, (int) finalHeight);
    }

    public static void scissorEnd() {
        glDisable(GL_SCISSOR_TEST);
    }


    // This will set the alpha limit to a specified value ranging from 0-1
    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    // This method colors the next avalible texture with a specified alpha value ranging from 0-1
    public static void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }

    // Colors the next texture without a specified alpha value
    public static void color(int color) {
        color(color, (float) (color >> 24 & 255) / 255.0F);
    }

    /**
     * Bind a texture using the specified integer refrence to the texture.
     *
     * @see org.lwjgl.opengl.GL13 for more information about texture bindings
     */
    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    // Sometimes colors get messed up in for loops, so we use this method to reset it to allow new colors to be used
    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    public static boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        GLUtil.setup2DRendering();
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);
        glPushMatrix();
        glBegin(GL_QUADS);
        color(startColor);
        glVertex2d(left, top);
        glVertex2d(left, bottom);
        color(endColor);
        glVertex2d(right, bottom);
        glVertex2d(right, top);
        glEnd();
        glPopMatrix();
        glDisable(GL_LINE_SMOOTH);
        GLUtil.end2DRendering();
        resetColor();
    }

    public static void drawGradientRectBordered(double left, double top, double right, double bottom, double width, int startColor, int endColor, int borderStartColor, int borderEndColor) {
        drawGradientRect(left + width, top + width, right - width, bottom - width, startColor, endColor);
        drawGradientRect(left + width, top, right - width, top + width, borderStartColor, borderEndColor);
        drawGradientRect(left, top, left + width, bottom, borderStartColor, borderEndColor);
        drawGradientRect(right - width, top, right, bottom, borderStartColor, borderEndColor);
        drawGradientRect(left + width, bottom - width, right - width, bottom, borderStartColor, borderEndColor);
    }

}
