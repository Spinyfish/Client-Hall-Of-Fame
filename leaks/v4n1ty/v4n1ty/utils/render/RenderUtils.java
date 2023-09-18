package v4n1ty.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;
import static v4n1ty.utils.math.MathUtils.*;

public class RenderUtils {

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount),
                interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static void startDrawing() {
        GL11.glEnable(3042);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        Minecraft.getMinecraft().entityRenderer.setupCameraTransform(Minecraft.getMinecraft().timer.renderPartialTicks, 0);
    }

    public static void stopDrawing() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static Vec3 getRenderPos(double x, double y, double z) {

        x -= Minecraft.getMinecraft().getRenderManager().renderPosX;
        y -= Minecraft.getMinecraft().getRenderManager().renderPosY;
        z -= Minecraft.getMinecraft().getRenderManager().renderPosZ;

        return new Vec3(x, y, z);
    }

    public static int interpolateColor(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return interpolateColorC(color1, color2, amount).getRGB();
    }

    public static int interpolateColor(int color1, int color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        Color cColor1 = new Color(color1);
        Color cColor2 = new Color(color2);
        return interpolateColorC(cColor1, cColor2, amount).getRGB();
    }

    public static void glVertex3D(Vec3 vector3d) {
        GL11.glVertex3d(vector3d.xCoord, vector3d.yCoord, vector3d.zCoord);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void drawBoundingBox(final AxisAlignedBB aa) {

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

    public static void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }

    public static void color2(int color) {
        color(color, (float) (color >> 24 & 255) / 255.0F);
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void glColor(int hex) {
        float alpha = (float) (hex >> 24 & 255) / 255F;
        float red = (float) (hex >> 16 & 255) / 255F;
        float green = (float) (hex >> 8 & 255) / 255F;
        float blue = (float) (hex & 255) / 255F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void color(float red, float green, float blue, float alpha) {
        glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255f);
    }

    public static void color(Color color) {
        color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }


    public static void color(int argb) {

        float alpha = (argb >> 24 & 255) / 255f;
        float red = (argb >> 16 & 255) / 255f;
        float green = (argb >> 8 & 255) / 255f;
        float blue = (argb & 255) / 255f;

        glColor4f(red, green, blue, alpha);
    }

    public static void draw3dLine(double x, double y, double z, double x1, double y1, double z1, Color color) {
        GlStateManager.pushMatrix();
        if (true) {
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
        }
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0F);
        glColor(color.getRGB());
        GL11.glBegin(1);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glEnd();
        if (true) {
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
        }
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
