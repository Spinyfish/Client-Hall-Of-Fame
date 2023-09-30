package net.minecraft.client.renderer;

import java.nio.FloatBuffer;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderHelper
{
    /** Float buffer used to set OpenGL material colors */
    private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);
    private static final Vec3 LIGHT0_POS = (new Vec3(0.20000000298023224D, 1.0D, -0.699999988079071D)).normalize();
    private static final Vec3 LIGHT1_POS = (new Vec3(-0.20000000298023224D, 1.0D, 0.699999988079071D)).normalize();

    /**
     * Disables the OpenGL lighting properties enabled by enableStandardItemLighting
     */
    public static void disableStandardItemLighting()
    {
        GlStateManager.disableLighting();
        GlStateManager.disableLight(0);
        GlStateManager.disableLight(1);
        GlStateManager.disableColorMaterial();
    }

    /**
     * Sets the OpenGL lighting properties to the values used when rendering blocks as items
     */
    public static void enableStandardItemLighting()
    {
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(1032, 5634);
        float f = 0.4F;
        float f1 = 0.6F;
        float f2 = 0.0F;
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, (FloatBuffer)setColorBuffer(LIGHT0_POS.xCoord, LIGHT0_POS.yCoord, LIGHT0_POS.zCoord, 0.0D));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, (FloatBuffer)setColorBuffer(f1, f1, f1, 1.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, (FloatBuffer)setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, (FloatBuffer)setColorBuffer(f2, f2, f2, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, (FloatBuffer)setColorBuffer(LIGHT1_POS.xCoord, LIGHT1_POS.yCoord, LIGHT1_POS.zCoord, 0.0D));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, (FloatBuffer)setColorBuffer(f1, f1, f1, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, (FloatBuffer)setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, (FloatBuffer)setColorBuffer(f2, f2, f2, 1.0F));
        GlStateManager.shadeModel(7424);
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, (FloatBuffer)setColorBuffer(f, f, f, 1.0F));
    }

    /**
     * Update and return colorBuffer with the RGBA values passed as arguments
     */
    private static FloatBuffer setColorBuffer(double p_74517_0_, double p_74517_2_, double p_74517_4_, double p_74517_6_)
    {
        return setColorBuffer((float)p_74517_0_, (float)p_74517_2_, (float)p_74517_4_, (float)p_74517_6_);
    }

    /**
     * Update and return colorBuffer with the RGBA values passed as arguments
     */
    private static FloatBuffer setColorBuffer(float p_74521_0_, float p_74521_1_, float p_74521_2_, float p_74521_3_)
    {
        colorBuffer.clear();
        colorBuffer.put(p_74521_0_).put(p_74521_1_).put(p_74521_2_).put(p_74521_3_);
        colorBuffer.flip();
        return colorBuffer;
    }

    /**
     * Sets OpenGL lighting for rendering blocks as items inside GUI screens (such as containers).
     */
    public static void enableGUIStandardItemLighting()
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
        enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public static void drawCompleteBox(AxisAlignedBB axisalignedbb, float width, int insideColor, int borderColor) {
        GL11.glLineWidth((float) width);
        GL11.glEnable((int) 2848);
        GL11.glEnable((int) 2881);
        GL11.glHint((int) 3154, (int) 4354);
        GL11.glHint((int) 3155, (int) 4354);
        RenderHelper.glColor(insideColor);
        RenderHelper.drawBox(axisalignedbb);
        RenderHelper.glColor(borderColor);
        RenderHelper.drawOutlinedBox(axisalignedbb);
        RenderHelper.drawCrosses(axisalignedbb);
        GL11.glDisable((int) 2848);
        GL11.glDisable((int) 2881);
    }

    public static void drawCrosses(AxisAlignedBB box) {
        if (box == null) {
            return;
        }
        GL11.glBegin((int) 1);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
        GL11.glEnd();
    }

    public static void drawOutlinedBox(AxisAlignedBB box) {
        if (box == null) {
            return;
        }
        GL11.glBegin((int) 3);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
        GL11.glEnd();
        GL11.glBegin((int) 3);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
        GL11.glEnd();
        GL11.glBegin((int) 1);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
        GL11.glEnd();
    }


    public static void drawBox(AxisAlignedBB axisalignedbb, int color) {
        GL11.glEnable((int) 2848);
        GL11.glEnable((int) 2881);
        GL11.glHint((int) 3154, (int) 4354);
        GL11.glHint((int) 3155, (int) 4354);
        RenderHelper.glColor(color);
        RenderHelper.drawBox(axisalignedbb);
        GL11.glDisable((int) 2848);
        GL11.glDisable((int) 2881);
    }

    public static void drawBox(AxisAlignedBB box) {
        if (box == null) {
            return;
        }
        GL11.glBegin((int) 7);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
        GL11.glEnd();
        GL11.glBegin((int) 7);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
        GL11.glEnd();
        GL11.glBegin((int) 7);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
        GL11.glEnd();
        GL11.glBegin((int) 7);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
        GL11.glEnd();
        GL11.glBegin((int) 7);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
        GL11.glEnd();
        GL11.glBegin((int) 7);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
        GL11.glEnd();
        GL11.glBegin((int) 7);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
        GL11.glEnd();
        GL11.glBegin((int) 7);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
        GL11.glEnd();
        GL11.glBegin((int) 7);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
        GL11.glEnd();
        GL11.glBegin((int) 7);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
        GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
        GL11.glEnd();
        GL11.glBegin((int) 7);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
        GL11.glEnd();
        GL11.glBegin((int) 7);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
        GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
        GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
        GL11.glEnd();
    }

    public static void glColor(int hex) {
        float alpha = (float) (hex >> 24 & 255) / 255.0f;
        float red = (float) (hex >> 16 & 255) / 255.0f;
        float green = (float) (hex >> 8 & 255) / 255.0f;
        float blue = (float) (hex & 255) / 255.0f;
        GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
    }
}
