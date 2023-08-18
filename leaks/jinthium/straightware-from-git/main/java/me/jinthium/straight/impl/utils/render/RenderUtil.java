package me.jinthium.straight.impl.utils.render;

import me.jinthium.straight.api.shader.ShaderUtil;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.modules.visual.CustomModel;
import me.jinthium.straight.impl.modules.visual.Hud;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.render.particle.Particle;
import me.jinthium.straight.impl.utils.vector.Vector3d;
import me.jinthium.straight.impl.utils.vector.Vector4d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjglx.BufferUtils;
import org.lwjglx.util.glu.GLU;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public class RenderUtil implements MinecraftInstance {

    private static final Frustum frustum = new Frustum();
    private static final FloatBuffer windPos = BufferUtils.createFloatBuffer(4);
    private static final IntBuffer intBuffer = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer floatBuffer1 = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer floatBuffer2 = GLAllocation.createDirectFloatBuffer(16);
    private static final ShaderUtil circleShader = new ShaderUtil("circleArcShader");

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static void outlinedRect(final double x,
                                          final double y,
                                          final double width,
                                          final double height,
                                          final float thickness,
                                          final int color) {
        // Enable blending
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set color
        color(color);

        glLineWidth(thickness);

        // Begin rect
        glBegin(GL_LINE_LOOP);
        {
            glVertex2d(x, y);
            glVertex2d(x, y + height);
            glVertex2d(x + width, y + height);
            glVertex2d(x + width, y);
        }
        // Draw the rect
        glEnd();

        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
        // Disable blend
        glRestoreBlend(restore);
    }

    public static void drawCircle(float x, float y, float radius, float progress, int change, Color color, float smoothness) {
        GLUtil.startBlend();
        float borderThickness = 1;
        circleShader.init();
        circleShader.setUniformf("radialSmoothness", smoothness);
        circleShader.setUniformf("radius", radius);
        circleShader.setUniformf("borderThickness", borderThickness);
        circleShader.setUniformf("progress", progress);
        circleShader.setUniformi("change", change);
        circleShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        float wh = radius + 10;
        ScaledResolution sr = ScaledResolution.fetchResolution(mc);
        float width = (wh / 2f) - ((radius + borderThickness) / 2f);
        circleShader.setUniformf("pos", (x + width) * sr.getScaleFactor(),
                (Minecraft.getMinecraft().displayHeight - ((radius + borderThickness) * sr.getScaleFactor())) - ((y + width) * sr.getScaleFactor()));
        ShaderUtil.drawQuads(x, y, wh, wh);
        circleShader.unload();
        GLUtil.endBlend();
    }

    public static float interpolate(final float old, final float now, final float progress) {
        return old + (now - old) * progress;
    }

    public static Vec3 getRenderPos(double x, double y, double z) {

        x -= mc.getRenderManager().renderPosX;
        y -= mc.getRenderManager().renderPosY;
        z -= mc.getRenderManager().renderPosZ;

        return new Vec3(x, y, z);
    }

    public static void glVertex3D(Vec3 vector3d) {
        GL11.glVertex3d(vector3d.xCoord, vector3d.yCoord, vector3d.zCoord);
    }

    public static void  drawBoundingBox(final AxisAlignedBB aa) {

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        end();
    }

    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float imgWidth, float imgHeight) {
        GLUtil.startBlend();
        mc.getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
        GLUtil.endBlend();
    }

    public static void rotateStart(float x, float y, float width, float height, float rotation) {
        glPushMatrix();
        x += width / 2;
        y += height / 3;
        glTranslatef(x, y, 0);
        glRotatef(rotation, 0, 0, 1);
        glTranslatef(-x, -y, 0);
    }

    public static void rotateEnd() {
        glPopMatrix();
    }

    public static void polygon(final double x, final double y, double sideLength, final double amountOfSides, final boolean filled, final Color color) {
        sideLength /= 2;
        start();
        if (color != null)
            color(color.getRGB());
        if (!filled) GL11.glLineWidth(2);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_STRIP);
        {
            for (double i = 0; i <= amountOfSides / 4; i++) {
                final double angle = i * 4 * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public static void vertex(final double x, final double y) {
        GL11.glVertex2d(x, y);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void begin(final int glMode) {
        GL11.glBegin(glMode);
    }

    public static void start() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void stop() {
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }


    public static void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final boolean filled) {
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public static void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final Color color) {
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public static void polygon(final double x, final double y, final double sideLength, final int amountOfSides) {
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public static void renderParticle(final Particle particle) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        try {
            final Vec3 v = particle.getPosition();
            boolean draw = true;

            final double x = v.xCoord - mc.getRenderManager().renderPosX;
            final double y = v.yCoord - mc.getRenderManager().renderPosY;
            final double z = v.zCoord - mc.getRenderManager().renderPosZ;

            final double distanceFromPlayer = mc.thePlayer.getDistanceSq(v.xCoord, v.yCoord - 1, v.zCoord);
            int quality = (int) (distanceFromPlayer * 4 * 4 + 10);

            if (quality > 350)
                quality = 350;

            if (!RenderUtil.isInView(new EntityEgg(mc.theWorld, v.xCoord, v.yCoord, v.zCoord)))
                draw = false;

            if (draw) {
                glPushMatrix();
                glTranslated(x, y, z);

                final float scale = 0.04F;
                glScalef(-scale, -scale, -scale);

                glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                glRotated(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0D : 1.0D, 0.0D, 0.0D);

                Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
                final Color c = hud.getHudColor((float) System.currentTimeMillis() / 600);

                drawFilledCircleNoGL(0, -3, 0.7, c.hashCode(), quality);

                if (distanceFromPlayer < 4 * 4)
                    drawFilledCircleNoGL(0, -3, 1.4, new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).hashCode(), quality);

                if (distanceFromPlayer < 20 * 20)
                    drawFilledCircleNoGL(0, -3, 2.3, new Color(c.getRed(), c.getGreen(), c.getBlue(), 30).hashCode(), quality);

                glScalef(0.8F, 0.8F, 0.8F);
                glPopMatrix();
            }
        } catch (final ConcurrentModificationException ignored) {
            ignored.printStackTrace();
        }

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        glColor3d(255, 255, 255);
    }

    public static void renderBreadCrumbs(final List<Vec3> vec3s) {
        GlStateManager.disableDepth();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        int i = 0;
        try {
            for (final Vec3 v : vec3s) {

                i++;

                boolean draw = true;

                final double x = v.xCoord - mc.getRenderManager().renderPosX;
                final double y = v.yCoord - mc.getRenderManager().renderPosY;
                final double z = v.zCoord - mc.getRenderManager().renderPosZ;

                final double distanceFromPlayer = mc.thePlayer.getDistance(v.xCoord, v.yCoord - 1, v.zCoord);
                int quality = (int) (distanceFromPlayer * 4 + 10);

                if (quality > 350)
                    quality = 350;

                if (i % 10 != 0 && distanceFromPlayer > 25) {
                    draw = false;
                }

                if (i % 3 == 0 && distanceFromPlayer > 15) {
                    draw = false;
                }

                if (draw) {

                    glPushMatrix();
                    glTranslated(x, y, z);

                    final float scale = 0.04f;
                    glScalef(-scale, -scale, -scale);

                    glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);
                    glRotated((mc.getRenderManager()).playerViewX, 1.0D, 0.0D, 0.0D);

                    Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
                    final Color c = hud.getHudColor((float) System.currentTimeMillis() / 600);

                    RenderUtil.drawFilledCircleNoGL(0, 0, 0.7, c.hashCode(), quality);

                    if (distanceFromPlayer < 4)
                        RenderUtil.drawFilledCircleNoGL(0, 0, 1.4, new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).hashCode(), quality);

                    if (distanceFromPlayer < 20)
                        RenderUtil.drawFilledCircleNoGL(0, 0, 2.3, new Color(c.getRed(), c.getGreen(), c.getBlue(), 30).hashCode(), quality);

                    glScalef(0.8f, 0.8f, 0.8f);

                    glPopMatrix();

                }

            }
        } catch (final ConcurrentModificationException ignored) {
        }

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        GlStateManager.enableDepth();

        glColor3d(255, 255, 255);
    }

    public static void renderParticles(final List<Particle> particles) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        int i = 0;
        try {
            for (final Particle particle : particles) {
                i++;
                final Vec3 v = particle.getPosition();
                boolean draw = true;

                final double x = v.xCoord;
                final double y = v.yCoord;
                final double z = v.zCoord;

                final double distanceFromPlayer = mc.thePlayer.getDistanceSq(v.xCoord, v.yCoord - 1, v.zCoord);
                int quality = (int) (distanceFromPlayer * 4 * 4 + 10);

                if (quality > 350)
                    quality = 350;

                if (!RenderUtil.isInView(new EntityEgg(mc.theWorld, v.xCoord, v.yCoord, v.zCoord)))
                    draw = false;

                if (i % 10 != 0 && distanceFromPlayer > 25 * 25)
                    draw = false;

                if (i % 3 == 0 && distanceFromPlayer > 15 * 15)
                    draw = false;

                if (draw) {
                    glPushMatrix();
                    glTranslated(x, y, z);

                    final float scale = 0.04F;
                    glScalef(-scale, -scale, -scale);

                    glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                    glRotated(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0D : 1.0D, 0.0D, 0.0D);

                    Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
                    final Color c = hud.getHudColor((float) System.currentTimeMillis() / 600);

                    drawFilledCircleNoGL(0, -3, 0.7, c.hashCode(), quality);

                    if (distanceFromPlayer < 4 * 4)
                        drawFilledCircleNoGL(0, -3, 1.4, new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).hashCode(), quality);

                    if (distanceFromPlayer < 20 * 20)
                        drawFilledCircleNoGL(0, -3, 2.3, new Color(c.getRed(), c.getGreen(), c.getBlue(), 30).hashCode(), quality);

                    glScalef(0.8F, 0.8F, 0.8F);
                    glPopMatrix();
                }
            }
        } catch (final ConcurrentModificationException ignored) {
            ignored.printStackTrace();
        }

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        glColor3d(255, 255, 255);
    }

    public static void drawFilledCircleNoGL(final int x, final int y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;

        glColor4f(f1, f2, f3, f);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            glVertex2d(x + x2, y + y2);
        }

        glEnd();
    }

    public static Vector4f getEntityPositionsOn2D(Entity entity) {
        final AxisAlignedBB bb = getInterpolatedBoundingBox(entity);

        float yOffset = CustomModel.enabled ? (float) CustomModel.getYOffset() : 0;


        final List<Vector3f> vectors = Arrays.asList(
                new Vector3f((float) bb.minX, (float) bb.minY, (float) bb.minZ),
                new Vector3f((float) bb.minX, (float) bb.maxY - yOffset, (float) bb.minZ),
                new Vector3f((float) bb.maxX, (float) bb.minY, (float) bb.minZ),
                new Vector3f((float) bb.maxX, (float) bb.maxY - yOffset, (float) bb.minZ),
                new Vector3f((float) bb.minX, (float) bb.minY, (float) bb.maxZ),
                new Vector3f((float) bb.minX, (float) bb.maxY - yOffset, (float) bb.maxZ),
                new Vector3f((float) bb.maxX, (float) bb.minY, (float) bb.maxZ),
                new Vector3f((float) bb.maxX, (float) bb.maxY - yOffset, (float) bb.maxZ));

        Vector4f entityPos = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0f, -1.0f);
        ScaledResolution sr = ScaledResolution.fetchResolution(mc);
        for (Vector3f vector3f : vectors) {
            vector3f = projectOn2D(vector3f.x, vector3f.y, vector3f.z, sr.getScaleFactor());
            if (vector3f != null && vector3f.z >= 0.0 && vector3f.z < 1.0) {
                entityPos.x = Math.min(vector3f.x, entityPos.x);
                entityPos.y = Math.min(vector3f.y, entityPos.y);
                entityPos.z = Math.max(vector3f.x, entityPos.z);
                entityPos.w = Math.max(vector3f.y, entityPos.w);
            }
        }
        return entityPos;
    }

    public static Vector4d getEntityPositionsOn2Dd(Entity entity) {
        final AxisAlignedBB bb = getInterpolatedBoundingBox(entity);

        float yOffset = CustomModel.enabled ? (float) CustomModel.getYOffset() : 0;


        final List<Vector3f> vectors = Arrays.asList(
                new Vector3f((float) bb.minX, (float) bb.minY, (float) bb.minZ),
                new Vector3f((float) bb.minX, (float) bb.maxY - yOffset, (float) bb.minZ),
                new Vector3f((float) bb.maxX, (float) bb.minY, (float) bb.minZ),
                new Vector3f((float) bb.maxX, (float) bb.maxY - yOffset, (float) bb.minZ),
                new Vector3f((float) bb.minX, (float) bb.minY, (float) bb.maxZ),
                new Vector3f((float) bb.minX, (float) bb.maxY - yOffset, (float) bb.maxZ),
                new Vector3f((float) bb.maxX, (float) bb.minY, (float) bb.maxZ),
                new Vector3f((float) bb.maxX, (float) bb.maxY - yOffset, (float) bb.maxZ));

        Vector4d entityPos = new Vector4d(Float.MAX_VALUE, Float.MAX_VALUE, -1.0f, -1.0f);
        ScaledResolution sr = ScaledResolution.fetchResolution(mc);
        for (Vector3f vector3f : vectors) {
            vector3f = projectOn2D(vector3f.x, vector3f.y, vector3f.z, sr.getScaleFactor());
            if (vector3f != null && vector3f.z >= 0.0 && vector3f.z < 1.0) {
                entityPos.x = Math.min(vector3f.x, entityPos.x);
                entityPos.y = Math.min(vector3f.y, entityPos.y);
                entityPos.z = Math.max(vector3f.x, entityPos.z);
                entityPos.w = Math.max(vector3f.y, entityPos.w);
            }
        }
        return entityPos;
    }

    public static double[] getInterpolatedPos(Entity entity) {
        float ticks = mc.timer.renderPartialTicks;
        return new double[]{
                MathUtils.interpolate(entity.lastTickPosX, entity.posX, ticks) - mc.getRenderManager().viewerPosX,
                MathUtils.interpolate(entity.lastTickPosY, entity.posY, ticks) - mc.getRenderManager().viewerPosY,
                MathUtils.interpolate(entity.lastTickPosZ, entity.posZ, ticks) - mc.getRenderManager().viewerPosZ
        };
    }

    public static Vector3f projectOn2D(float x, float y, float z, int scaleFactor) {
        glGetFloatv(GL_MODELVIEW_MATRIX, floatBuffer1);
        glGetFloatv(GL_PROJECTION_MATRIX, floatBuffer2);
        glGetIntegerv(GL_VIEWPORT, intBuffer);
        if (GLU.gluProject(x, y, z, floatBuffer1, floatBuffer2, intBuffer, windPos)) {
            return new Vector3f(windPos.get(0) / scaleFactor, (mc.displayHeight - windPos.get(1)) / scaleFactor, windPos.get(2));
        }
        return null;
    }

    public static Vector3d projectOn2D(double x, double y, double z, int scaleFactor) {
        glGetFloatv(GL_MODELVIEW_MATRIX, floatBuffer1);
        glGetFloatv(GL_PROJECTION_MATRIX, floatBuffer2);
        glGetIntegerv(GL_VIEWPORT, intBuffer);
        if (GLU.gluProject((float) x, (float) y, (float) z, floatBuffer1, floatBuffer2, intBuffer, windPos)) {
            return new Vector3d(windPos.get(0) / scaleFactor, (mc.displayHeight - windPos.get(1)) / scaleFactor, windPos.get(2));
        }
        return null;
    }

    public static AxisAlignedBB getInterpolatedBoundingBox(Entity entity) {
        final double[] renderingEntityPos = getInterpolatedPos(entity);
        final double entityRenderWidth = entity.width / 1.5;
        return new AxisAlignedBB(renderingEntityPos[0] - entityRenderWidth,
                renderingEntityPos[1], renderingEntityPos[2] - entityRenderWidth, renderingEntityPos[0] + entityRenderWidth,
                renderingEntityPos[1] + entity.height + (entity.isSneaking() ? -0.3 : 0.18), renderingEntityPos[2] + entityRenderWidth).expand(0.15, 0.15, 0.15);
    }

    public static boolean isInView(Entity ent) {
        frustum.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        return frustum.isBoundingBoxInFrustum(ent.getEntityBoundingBox()) || ent.ignoreFrustumCheck;
    }

    public static void scissor(double x, double y, double width, double height, Runnable data) {
        glEnable(GL_SCISSOR_TEST);
        scissor(x, y, width, height);
        data.run();
        glDisable(GL_SCISSOR_TEST);
    }

    public static void scissorStart(double x, double y, double width, double height) {
        glEnable(GL_SCISSOR_TEST);
        ScaledResolution sr = ScaledResolution.fetchResolution(mc);
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

    public static float animate(float target, float current, float speed) {
        boolean larger = (target > current);
        if (speed < 0.0f) speed = 0.0f;
        else if (speed > 1.0f) speed = 1.0f;
        float dif = Math.abs(current - target);
        float factor = dif * speed;
        if (larger) current += factor;
        else current -= factor;
        return current;
    }

    public static void scissor(double x, double y, double width, double height) {
        ScaledResolution sr = ScaledResolution.fetchResolution(mc);
        final double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = (sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        glScissor((int) finalX, (int) (finalY - finalHeight), (int) finalWidth, (int) finalHeight);
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

    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    public static boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public static void scaleStart(float x, float y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(-x, -y, 0);
    }

    public static void scaleEnd() {
        GlStateManager.popMatrix();
    }

    public static void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }

    public static void color(int color) {
        color(color, (float) (color >> 24 & 255) / 255.0F);
    }

    public static boolean glEnableBlend() {
        final boolean wasEnabled = glIsEnabled(GL_BLEND);

        if (!wasEnabled) {
            glEnable(GL_BLEND);
            glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        }

        return wasEnabled;
    }

    public static void glRestoreBlend(final boolean wasEnabled) {
        if (!wasEnabled) {
            glDisable(GL_BLEND);
        }
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight;
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }
}
