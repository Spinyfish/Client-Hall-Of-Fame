/**
 * @project hakarware
 * @author CodeMan
 * @at 26.07.23, 18:26
 */

package cc.swift.util.render;

import cc.swift.Swift;
import cc.swift.util.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3f;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;
import static org.lwjgl.opengl.GL11.*;

@UtilityClass
public class RenderUtil implements IMinecraft {

    public FloatBuffer windowPosition = BufferUtils.createFloatBuffer(4);
    public IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    public FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    public FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);

    public Frustum frustrum = new Frustum();

    public Vector3f project(float x, float y, float z) {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);


        if (GLU.gluProject(x, y, z, modelview, projection, viewport, windowPosition))
            return new Vector3f(windowPosition.get(0) / Swift.INSTANCE.sr.getScaleFactor(), (mc.displayHeight - windowPosition.get(1)) / Swift.INSTANCE.sr.getScaleFactor(), windowPosition.get(2));

        return null;
    }

    public boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public void drawImg(ResourceLocation loc, double posX, double posY, double width, double height, Color c) {
        float alpha = (float)(c.getRGB() >> 24 & 255) / 255.0F;
        float red = (float)(c.getRGB() >> 16 & 255) / 255.0F;
        float green = (float)(c.getRGB() >> 8 & 255) / 255.0F;
        float blue = (float)(c.getRGB() & 255) / 255.0F;

        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(alpha, red, green, blue);

        mc.getTextureManager().bindTexture(loc);

        float textureWidthRatio = 1.0F / (float)width;
        float textureHeightRatio = 1.0F / (float)height;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(posX, posY + height, 0.0D).tex(0.0, height * textureHeightRatio).endVertex();
        worldrenderer.pos(posX + width, posY + height, 0.0D).tex(width * textureWidthRatio, height * textureHeightRatio).endVertex();
        worldrenderer.pos(posX + width, posY, 0.0D).tex(width * textureWidthRatio, 0.0).endVertex();
        worldrenderer.pos(posX, posY, 0.0D).tex(0.0, 0.0).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    public void drawRoundedRect(double x, double y, double width, double height, double radius, int color, int outlineColor, float outlineWidth) {
        double x1 = x + width, y1 = y + height;

        glPushMatrix();

        radius = MathHelper.clamp_double(radius, 1, Math.min(width / 2, height / 2));

        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);

        if (color != 0) {
            glColor(color);
            glBegin(GL_POLYGON);
            drawPie(x, y, x1, y1, radius);
            glEnd();
        }

        if (outlineWidth > 0 && outlineColor != 0) {
            glColor(outlineColor);
            glLineWidth(outlineWidth);
            glBegin(GL_LINE_LOOP);
            drawPie(x, y, x1, y1, radius);
            glEnd();
        }

        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);

        glPopMatrix();
        glColor4f(1, 1, 1, 1);
    }

    public void drawPie(double x, double y, double x1, double y1, double radius) {
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

    public void glColor(int color) {
        GL11.glColor4ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF), (byte) (color >> 24 & 0xFF));
    }
}
