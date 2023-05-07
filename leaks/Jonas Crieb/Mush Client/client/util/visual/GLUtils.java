/** THIS ONLY FOR GL **/
package client.util.visual;

import client.util.gl.GaussianBlur;
import client.util.gl.ShaderUtility;
import client.util.gl.StencilUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
public class GLUtils {
    protected static Minecraft mc = Minecraft.getMinecraft();
    static final ShaderUtility roundedShader = new ShaderUtility("roundedRect");
    private static final IntBuffer viewport = BufferUtils.createIntBuffer(16);
    private final static FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private final static FloatBuffer projections = GLAllocation.createDirectFloatBuffer(16);
    public static Framebuffer glCreateFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }
    public static void glBindTexture(int texture) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
    }
    public static javax.vecmath.Vector3d glProject(double x, double y, double z) {
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(2982, modelview);
        GL11.glGetFloat(2983, projections);
        GL11.glGetInteger(2978, viewport);
        if (GLU.gluProject((float) x, (float) y, (float) z, modelview, projections, viewport, vector)) {
            return new Vector3d(vector.get(0) / new ScaledResolution(mc).getScaleFactor(), (Display.getHeight() - vector.get(1)) / new ScaledResolution(mc).getScaleFactor(), vector.get(2));
        }
        return null;
    }
    public static void glStart2D() {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
    }
    public static void glStop2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glColor4f(1, 1, 1, 1);
    }
    public static void glBlur(Runnable data, float radius) {
        StencilUtility.initStencilToWrite();
        data.run();
        StencilUtility.readStencilBuffer(1);
        GaussianBlur.renderBlur(radius);
        StencilUtility.uninitStencilBuffer();
    }
    public static void glArrow(double x, double y, int lineWidth, int color, double length) {
        glStart2D();
        GL11.glPushMatrix();
        GL11.glLineWidth(lineWidth);
        ColorUtils.color(new Color(color));
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + 3, y + length);
        GL11.glVertex2d(x + 3 * 2, y);
        GL11.glEnd();
        GL11.glPopMatrix();
        glStop2D();
    }
}
