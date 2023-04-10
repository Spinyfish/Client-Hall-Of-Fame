/*
 * Decompiled with CFR 0_122.
 *
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package arithmo.gui.altmanager;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class RenderUtil {
    public static void drawOutlinedString(String str, float x, float y, int color) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.fontRendererObj.drawStringWithShadow(str, x - 0.3f, y, Colors.getColor(0));
        mc.fontRendererObj.drawStringWithShadow(str, x + 0.3f, y, Colors.getColor(0));
        mc.fontRendererObj.drawStringWithShadow(str, x, y + 0.3f, Colors.getColor(0));
        mc.fontRendererObj.drawStringWithShadow(str, x, y - 0.3f, Colors.getColor(0));
        mc.fontRendererObj.drawStringWithShadow(str, x, y, color);
    }
    public static void blinkToPosFromPos(Vec3 src, Vec3 dest, double maxTP) {
        double range = 0;
        Minecraft mc = Minecraft.getMinecraft();
        double xDist = src.xCoord - dest.xCoord;
        double yDist = src.yCoord - dest.yCoord;
        double zDist = src.zCoord - dest.zCoord;
        double x1 = src.xCoord;
        double y1 = src.yCoord;
        double z1 = src.zCoord;
        double x2 = dest.xCoord;
        double y2 = dest.yCoord;
        double z2 = dest.zCoord;
        range = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
        double step = maxTP / range;
        int steps = 0;
        for (int i = 0; i < range; i++) {
            steps++;
            if (maxTP * steps > range) {
                break;
            }
        }
        for (int i = 0; i < steps; i++) {
            double difX = x1 - x2;
            double difY = y1 - y2;
            double difZ = z1 - z2;
            double divider = step * i;
            double x = x1 - difX * divider;
            double y = y1 - difY * divider;
            double z = z1 - difZ * divider;

            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2, z2, true));
    }


    public static void glColor(final int red, final int green, final int blue, final int alpha) {
        GL11.glColor4f(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }

    public static void glColor3(final Color color) {
        glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static void glColor2(final int hex) {
        glColor(hex >> 16 & 0xFF, hex >> 8 & 0xFF, hex & 0xFF, hex >> 24 & 0xFF);
    }


    public static void drawFancy(double d, double e, double f2, double f3, int paramColor) {
        float alpha = (float)(paramColor >> 24 & 255) / 255.0f;
        float red = (float)(paramColor >> 16 & 255) / 255.0f;
        float green = (float)(paramColor >> 8 & 255) / 255.0f;
        float blue = (float)(paramColor & 255) / 255.0f;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glPushMatrix();
        GL11.glEnable((int)2848);
        GL11.glEnable((int)2881);
        GL11.glEnable((int)2832);
        GL11.glEnable((int)3042);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)(f2 + 1.300000011920929), (double)e);
        GL11.glVertex2d((double)(d + 1.0), (double)e);
        GL11.glVertex2d((double)(d - 1.300000011920929), (double)f3);
        GL11.glVertex2d((double)(f2 - 1.0), (double)f3);
        GL11.glEnd();
        GL11.glDisable((int)2848);
        GL11.glDisable((int)2881);
        GL11.glDisable((int)2832);
        GL11.glDisable((int)3042);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static void drawGradient(double x, double y, double x2, double y2, int col1, int col2) {
        float f = (float)(col1 >> 24 & 255) / 255.0f;
        float f1 = (float)(col1 >> 16 & 255) / 255.0f;
        float f2 = (float)(col1 >> 8 & 255) / 255.0f;
        float f3 = (float)(col1 & 255) / 255.0f;
        float f4 = (float)(col2 >> 24 & 255) / 255.0f;
        float f5 = (float)(col2 >> 16 & 255) / 255.0f;
        float f6 = (float)(col2 >> 8 & 255) / 255.0f;
        float f7 = (float)(col2 & 255) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glShadeModel((int)7425);
        GL11.glPushMatrix();
        GL11.glBegin((int)7);
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
        GL11.glVertex2d((double)x2, (double)y);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glColor4f((float)f5, (float)f6, (float)f7, (float)f4);
        GL11.glVertex2d((double)x, (double)y2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
        GL11.glShadeModel((int)7424);
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (float)(col1 >> 24 & 255) / 255.0f;
        float f1 = (float)(col1 >> 16 & 255) / 255.0f;
        float f2 = (float)(col1 >> 8 & 255) / 255.0f;
        float f3 = (float)(col1 & 255) / 255.0f;
        float f4 = (float)(col2 >> 24 & 255) / 255.0f;
        float f5 = (float)(col2 >> 16 & 255) / 255.0f;
        float f6 = (float)(col2 >> 8 & 255) / 255.0f;
        float f7 = (float)(col2 & 255) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glShadeModel((int)7425);
        GL11.glPushMatrix();
        GL11.glBegin((int)7);
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
        GL11.glVertex2d((double)left, (double)top);
        GL11.glVertex2d((double)left, (double)bottom);
        GL11.glColor4f((float)f5, (float)f6, (float)f7, (float)f4);
        GL11.glVertex2d((double)right, (double)bottom);
        GL11.glVertex2d((double)right, (double)top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
        GL11.glShadeModel((int)7424);
    }

    public static void rectangle(double left, double top, double right, double bottom, int color) {
        double var5;
        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }
        float var11 = (float)(color >> 24 & 255) / 255.0f;
        float var6 = (float)(color >> 16 & 255) / 255.0f;
        float var7 = (float)(color >> 8 & 255) / 255.0f;
        float var8 = (float)(color & 255) / 255.0f;
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);

        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

    }





    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glShadeModel((int)7425);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDisable((int)2896);
        GL11.glDepthMask((boolean)false);
        GL11.glHint((int)3154, (int)4354);
    }

    public static void post3D() {
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static void glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
        float red = 0.003921569f * (float)redRGB;
        float green = 0.003921569f * (float)greenRGB;
        float blue = 0.003921569f * (float)blueRGB;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
    }

    public static void drawRect(float x, float y, float x1, float y1) {
        GL11.glBegin((int)7);
        GL11.glVertex2f((float)x, (float)y1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glVertex2f((float)x1, (float)y);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glEnd();
    }

    public static void glColor(int hex) {
        float alpha = (float)(hex >> 24 & 255) / 255.0f;
        float red = (float)(hex >> 16 & 255) / 255.0f;
        float green = (float)(hex >> 8 & 255) / 255.0f;
        float blue = (float)(hex & 255) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
    }

    public static void drawRect(float x, float y, float x1, float y1, int color) {
        RenderUtil.enableGL2D();
        RenderUtil.glColor(color);
        RenderUtil.drawRect(x, y, x1, y1);
        RenderUtil.disableGL2D();
    }

    public static void drawRoundedRect(float x, float y, float x1, float y1, int borderC, int insideC) {
        RenderUtil.drawRect(x + 0.5f, y, x1 - 0.5f, y + 0.5f, insideC);
        RenderUtil.drawRect(x + 0.5f, y1 - 0.5f, x1 - 0.5f, y1, insideC);
        RenderUtil.drawRect(x, y + 0.5f, x1, y1 - 0.5f, insideC);
    }

    public static void drawHLine(float x, float y, float x1, int y1) {
        if (y < x) {
            float var5 = x;
            x = y;
            y = var5;
        }
        RenderUtil.drawRect(x, x1, y + 1.0f, x1 + 1.0f, y1);
    }

    public static void drawVLine(float x, float y, float x1, float y1, float width, int color) {
        if (width <= 0.0f) {
            return;
        }
        GL11.glPushMatrix();
        RenderUtil.pre3D();
        float var11 = (float)(color >> 24 & 255) / 255.0f;
        float var6 = (float)(color >> 16 & 255) / 255.0f;
        float var7 = (float)(color >> 8 & 255) / 255.0f;
        float var8 = (float)(color & 255) / 255.0f;
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        int shade = GL11.glGetInteger((int)2900);
        GlStateManager.shadeModel(7425);
        GL11.glColor4f((float)var6, (float)var7, (float)var8, (float)var11);
        float line = GL11.glGetFloat((int)2849);
        GL11.glLineWidth((float)width);
        GL11.glBegin((int)3);
        GL11.glVertex3d((double)x, (double)y, (double)0.0);
        GL11.glVertex3d((double)x1, (double)y1, (double)0.0);
        GL11.glEnd();
        GlStateManager.shadeModel(shade);
        GL11.glLineWidth((float)line);
        RenderUtil.post3D();
        GL11.glPopMatrix();
    }

    public static void enableGL2D() {
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
    }

    public static void disableGL2D() {
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glHint((int)3155, (int)4352);
    }

    public static void drawCircle(float cx, float cy, float r, int num_segments, int c) {
        GL11.glPushMatrix();
        cx *= 2.0f;
        cy *= 2.0f;
        float f = (float)(c >> 24 & 255) / 255.0f;
        float f1 = (float)(c >> 16 & 255) / 255.0f;
        float f2 = (float)(c >> 8 & 255) / 255.0f;
        float f3 = (float)(c & 255) / 255.0f;
        float theta = (float)(6.2831852 / (double)num_segments);
        float p = (float)Math.cos(theta);
        float s = (float)Math.sin(theta);
        float x = r *= 2.0f;
        float y = 0.0f;
        RenderUtil.enableGL2D();
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
        GL11.glBegin((int)2);
        for (int ii = 0; ii < num_segments; ++ii) {
            GL11.glVertex2f((float)(x + cx), (float)(y + cy));
            float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
        }
        GL11.glEnd();
        GL11.glScalef((float)2.0f, (float)2.0f, (float)2.0f);
        RenderUtil.disableGL2D();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }

    public static void drawBorderedCircle(float circleX, float circleY, double radius, double width, int borderColor, int innerColor) {
        RenderUtil.enableGL2D();
        GlStateManager.enableBlend();
        GL11.glEnable((int)2881);
        RenderUtil.drawCircle(circleX, circleY, (float)(radius - 0.5 + width), 72, borderColor);
        RenderUtil.drawFullCircle(circleX, circleY, (float)radius, innerColor);
        GlStateManager.disableBlend();
        GL11.glDisable((int)2881);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.disableGL2D();
    }

    public static void drawCircleNew(float x, float y, float radius, int numberOfSides) {
        float z = 0.0f;
        int numberOfVertices = numberOfSides + 2;
        float doublePi = 6.2831855f;
    }

    public static void drawFullCircle(float cx, float cy, float r, int c) {
        cx *= 2.0f;
        cy *= 2.0f;
        float theta = 0.19634953f;
        float p = (float)Math.cos(theta);
        float s = (float)Math.sin(theta);
        float x = r *= 2.0f;
        float y = 0.0f;
        RenderUtil.enableGL2D();
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glEnable((int)3024);
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
        RenderUtil.glColor(c);
        GL11.glBegin((int)9);
        for (int ii = 0; ii < 32; ++ii) {
            GL11.glVertex2f((float)(x + cx), (float)(y + cy));
            float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
        }
        GL11.glEnd();
        GL11.glScalef((float)2.0f, (float)2.0f, (float)2.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.disableGL2D();
    }
    public static void glColor(Color color)
    {
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
    }

    public static void drawCircle(int x, int y, double r, int c)
    {
        float f = (c >> 24 & 0xFF) / 255.0F;
        float f1 = (c >> 16 & 0xFF) / 255.0F;
        float f2 = (c >> 8 & 0xFF) / 255.0F;
        float f3 = (c & 0xFF) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(2);
        for (int i = 0; i <= 360; i++)
        {
            double x2 = Math.sin(i * 3.141592653589793D / 180.0D) * r;
            double y2 = Math.cos(i * 3.141592653589793D / 180.0D) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
    public static void drawTracerLine(double x, double y, double z, Color color, float alpha, float lineWdith)
    {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glDisable(2929);

        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(lineWdith);
        glColor(color);
        GL11.glBegin(2);
        Minecraft.getMinecraft();GL11.glVertex3d(0.0D, 0.0D + Minecraft.getMinecraft().thePlayer.getEyeHeight(), 0.0D);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glDisable(3042);

        GL11.glPopMatrix();
    }
}


