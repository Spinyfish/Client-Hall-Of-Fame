/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.util.glu.GLU
 */
package Reality.Realii.utils.render.gl;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import Reality.Realii.utils.math.Vec3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public final class GLUtils {
    public static final FloatBuffer MODELVIEW = BufferUtils.createFloatBuffer((int)16);
    public static final FloatBuffer PROJECTION = BufferUtils.createFloatBuffer((int)16);
    public static final IntBuffer VIEWPORT = BufferUtils.createIntBuffer((int)16);
    public static final FloatBuffer TO_SCREEN_BUFFER = BufferUtils.createFloatBuffer((int)3);
    public static final FloatBuffer TO_WORLD_BUFFER = BufferUtils.createFloatBuffer((int)3);
    private static FloatBuffer colorBuffer;
    private static final Vec3 LIGHT0_POS;
    private static final Vec3 LIGHT1_POS;

    static {
        GLUtils.colorBuffer = GLAllocation.createDirectFloatBuffer(16);
        LIGHT0_POS = new Vec3(0.20000000298023224, 1.0, -0.699999988079071).normalize();
        LIGHT1_POS = new Vec3(-0.20000000298023224, 1.0, 0.699999988079071).normalize();
    }


    private GLUtils() {
    }

    public static void init() {
    }
    public static void enableGUIStandardItemLighting() {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(-30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(165.0f, 1.0f, 0.0f, 0.0f);
        enableStandardItemLighting();
        GlStateManager.popMatrix();
    }
    public static void disableStandardItemLighting() {
        GlStateManager.disableLighting();
        GlStateManager.disableLight(0);
        GlStateManager.disableLight(1);
        GlStateManager.disableColorMaterial();
    }

    public static void enableStandardItemLighting() {
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(1032, 5634);
        final float n = 0.4f;
        final float n2 = 0.6f;
        final float n3 = 0.0f;
        GL11.glLight(16384, 4611, setColorBuffer(GLUtils.LIGHT0_POS.xCoord, GLUtils.LIGHT0_POS.yCoord, GLUtils.LIGHT0_POS.zCoord, 0.0));
        GL11.glLight(16384, 4609, setColorBuffer(n2, n2, n2, 1.0f));
        GL11.glLight(16384, 4608, setColorBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GL11.glLight(16384, 4610, setColorBuffer(n3, n3, n3, 1.0f));
        GL11.glLight(16385, 4611, setColorBuffer(GLUtils.LIGHT1_POS.xCoord, GLUtils.LIGHT1_POS.yCoord, GLUtils.LIGHT1_POS.zCoord, 0.0));
        GL11.glLight(16385, 4609, setColorBuffer(n2, n2, n2, 1.0f));
        GL11.glLight(16385, 4608, setColorBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GL11.glLight(16385, 4610, setColorBuffer(n3, n3, n3, 1.0f));
        GlStateManager.shadeModel(7424);
        GL11.glLightModel(2899, setColorBuffer(n, n, n, 1.0f));
    }

    private static FloatBuffer setColorBuffer(final double p_setColorBuffer_0_, final double p_setColorBuffer_2_, final double p_setColorBuffer_4_, final double p_setColorBuffer_6_) {
        return setColorBuffer((float)p_setColorBuffer_0_, (float)p_setColorBuffer_2_, (float)p_setColorBuffer_4_, (float)p_setColorBuffer_6_);
    }

    private static FloatBuffer setColorBuffer(final float p_setColorBuffer_0_, final float p_setColorBuffer_1_, final float p_setColorBuffer_2_, final float p_setColorBuffer_3_) {
        GLUtils.colorBuffer.clear();
        GLUtils.colorBuffer.put(p_setColorBuffer_0_).put(p_setColorBuffer_1_).put(p_setColorBuffer_2_).put(p_setColorBuffer_3_);
        GLUtils.colorBuffer.flip();
        return GLUtils.colorBuffer;
    }


    public static float[] getColor(int hex) {
        return new float[]{(float)(hex >> 16 & 255) / 255.0f, (float)(hex >> 8 & 255) / 255.0f, (float)(hex & 255) / 255.0f, (float)(hex >> 24 & 255) / 255.0f};
    }

    public static void glColor(int hex) {
        float[] color = GLUtils.getColor(hex);
        GlStateManager.color(color[0], color[1], color[2], color[3]);
    }

    public static void rotateX(float angle, double x, double y, double z) {
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(angle, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(- x, - y, - z);
    }

    public static void rotateY(float angle, double x, double y, double z) {
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(angle, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(- x, - y, - z);
    }

    public static void rotateZ(float angle, double x, double y, double z) {
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(angle, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(- x, - y, - z);
    }

    public static Vec3f toScreen(Vec3f pos) {
        return GLUtils.toScreen(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3f toScreen(double x, double y, double z) {
        boolean result = GLU.gluProject((float)((float)x), (float)((float)y), (float)((float)z), (FloatBuffer)MODELVIEW, (FloatBuffer)PROJECTION, (IntBuffer)VIEWPORT, (FloatBuffer)((FloatBuffer)TO_SCREEN_BUFFER.clear()));
        if (result) {
            return new Vec3f(TO_SCREEN_BUFFER.get(0), (float)Display.getHeight() - TO_SCREEN_BUFFER.get(1), TO_SCREEN_BUFFER.get(2));
        }
        return null;
    }

    public static Vec3f toWorld(Vec3f pos) {
        return GLUtils.toWorld(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3f toWorld(double x, double y, double z) {
        boolean result = GLU.gluUnProject((float)((float)x), (float)((float)y), (float)((float)z), (FloatBuffer)MODELVIEW, (FloatBuffer)PROJECTION, (IntBuffer)VIEWPORT, (FloatBuffer)((FloatBuffer)TO_WORLD_BUFFER.clear()));
        if (result) {
            return new Vec3f(TO_WORLD_BUFFER.get(0), TO_WORLD_BUFFER.get(1), TO_WORLD_BUFFER.get(2));
        }
        return null;
    }

    public static FloatBuffer getModelview() {
        return MODELVIEW;
    }

    public static FloatBuffer getProjection() {
        return PROJECTION;
    }

    public static IntBuffer getViewport() {
        return VIEWPORT;
    }
}

