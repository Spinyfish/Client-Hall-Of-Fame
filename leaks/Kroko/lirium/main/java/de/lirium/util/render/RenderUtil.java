package de.lirium.util.render;

import de.lirium.util.interfaces.IMinecraft;
import de.lirium.util.math.Vec;
import de.lirium.util.render.shader.shaders.AcrylBlurShader;
import de.lirium.util.render.shader.shaders.BackgroundShader;
import de.lirium.util.render.shader.shaders.RoundedRectOutlineShader;
import de.lirium.util.render.shader.shaders.RoundedRectShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityEndPortalRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.Random;

public class RenderUtil implements IMinecraft {

    public static float delta;

    public static RoundedRectShader roundedRectShader = new RoundedRectShader();

    public static RoundedRectOutlineShader roundedRectOutlineShader = new RoundedRectOutlineShader();

    public static AcrylBlurShader acrylBlurShader = new AcrylBlurShader();

    public static BackgroundShader backgroundShader = new BackgroundShader();

    public static void drawAcrylicBlur() {
        acrylBlurShader.draw();
    }

    public static void drawAcrylicBlurStencil() {
        StencilUtil.init();
    }

    public static void stopAcrylicBlurStencil() {
        StencilUtil.readBuffer(1);
        acrylBlurShader.draw();
        StencilUtil.uninit();
        GlStateManager.enableBlend();
        mc.entityRenderer.setupOverlayRendering();
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float radius, Color color) {
        roundedRectShader.drawRound(x, y, width, height, radius, color);
    }

    public static void drawRoundedRectOutline(float x, float y, float width, float height, float radius, float outlineThickness, Color color) {
        roundedRectOutlineShader.drawRound(x, y, width, height, radius, outlineThickness, color);
    }

    public static void drawBox(double x, double y, double z, double width, double height) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBegin(GL11.GL_QUADS);

        /* Front */
        GL11.glVertex3d(x, y + height, z);
        GL11.glVertex3d(x + width, y + height, z);
        GL11.glVertex3d(x + width, y, z);
        GL11.glVertex3d(x, y, z);

        /* Left */
        GL11.glVertex3d(x + width, y + height, z);
        GL11.glVertex3d(x + width, y + height, z + width);
        GL11.glVertex3d(x + width, y, z + width);
        GL11.glVertex3d(x + width, y, z);

        /* Behind */
        GL11.glVertex3d(x, y + height, z + width);
        GL11.glVertex3d(x + width, y + height, z + width);
        GL11.glVertex3d(x + width, y, z + width);
        GL11.glVertex3d(x, y, z + width);

        /* Right */
        GL11.glVertex3d(x, y + height, z);
        GL11.glVertex3d(x, y + height, z + width);
        GL11.glVertex3d(x, y, z + width);
        GL11.glVertex3d(x, y, z);

        /* Up */
        GL11.glVertex3d(x, y + height, z);
        GL11.glVertex3d(x + width, y + height, z);
        GL11.glVertex3d(x + width, y + height, z + width);
        GL11.glVertex3d(x, y + height, z + width);

        /* Down */
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x + width, y, z);
        GL11.glVertex3d(x + width, y, z + width);
        GL11.glVertex3d(x, y, z + width);

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
    private static final ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");

    private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);
    private static FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
    private static final Random RANDOM = new Random(31100L);

    private static FloatBuffer getBuffer(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_) {
        buffer.clear();
        buffer.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
        buffer.flip();
        return buffer;
    }

    private static int func_191286_a(double p_191286_1_) {
        int i;

        if (p_191286_1_ > 36864.0D) {
            i = 1;
        } else if (p_191286_1_ > 25600.0D) {
            i = 3;
        } else if (p_191286_1_ > 16384.0D) {
            i = 5;
        } else if (p_191286_1_ > 9216.0D) {
            i = 7;
        } else if (p_191286_1_ > 4096.0D) {
            i = 9;
        } else if (p_191286_1_ > 1024.0D) {
            i = 11;
        } else if (p_191286_1_ > 576.0D) {
            i = 13;
        } else if (p_191286_1_ > 256.0D) {
            i = 14;
        } else {
            i = 15;
        }

        return i;
    }

    public static void drawEndPortal(ScaledResolution resolution) {
        if (!Shaders.isShadowPass && Shaders.programsID[Shaders.activeProgram] == 0) {

            GlStateManager.disableLighting();
            RANDOM.setSeed(31100L);
            GlStateManager.getFloat(2982, MODELVIEW);
            GlStateManager.getFloat(2983, PROJECTION);
            double d0 = 0;
            int i = func_191286_a(d0);
            float f = 0.75F;
            boolean flag = false;

            for (int j = 0; j < i; ++j) {
                GlStateManager.pushMatrix();
                float f1 = 2.0F / (float) (18 - j);

                if (j == 0) {
                    mc.getTextureManager().bindTexture(END_SKY_TEXTURE);
                    f1 = 0.15F;
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                }

                if (j >= 1) {
                    mc.getTextureManager().bindTexture(END_PORTAL_TEXTURE);
                    flag = true;
                    Minecraft.getMinecraft().entityRenderer.func_191514_d(true);
                }

                if (j == 1) {
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                }

                GlStateManager.texGen(GlStateManager.TexGen.S, 9216);
                GlStateManager.texGen(GlStateManager.TexGen.T, 9216);
                GlStateManager.texGen(GlStateManager.TexGen.R, 9216);
                GlStateManager.texGen(GlStateManager.TexGen.S, 9474, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.texGen(GlStateManager.TexGen.T, 9474, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                GlStateManager.texGen(GlStateManager.TexGen.R, 9474, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
                GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
                GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5890);
                GlStateManager.pushMatrix();
                GlStateManager.loadIdentity();
                GlStateManager.translate(0.5F, 0.5F, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 1.0F);
                float f2 = (float) (j + 1);
                GlStateManager.translate(17.0F / f2, (2.0F + f2 / 1.5F) * ((float) Minecraft.getSystemTime() % 800000.0F / 800000.0F), 0.0F);
                GlStateManager.rotate((f2 * f2 * 4321.0F + f2 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.scale(4.5F - f2 / 4.0F, 4.5F - f2 / 4.0F, 1.0F);
                GlStateManager.multMatrix(PROJECTION);
                GlStateManager.multMatrix(MODELVIEW);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                float f3 = (RANDOM.nextFloat() * 0.5F + 0.1F) * f1;
                float f4 = (RANDOM.nextFloat() * 0.5F + 0.4F) * f1;
                float f5 = (RANDOM.nextFloat() * 0.5F + 0.5F) * f1;

                final int x = 0, y = 0, width = resolution.getScaledWidth(), height = resolution.getScaledHeight();

                bufferbuilder.pos((double) x, (double) (y + height), 0.0D).color(f3, f4, f5, 1.0F).endVertex();
                bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0D).color(f3, f4, f5, 1.0F).endVertex();
                bufferbuilder.pos((double) (x + width), (double) y, 0.0D).color(f3, f4, f5, 1.0F).endVertex();
                bufferbuilder.pos((double) x, (double) y, 0.0D).color(f3, f4, f5, 1.0F).endVertex();

                tessellator.draw();
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
                mc.getTextureManager().bindTexture(END_SKY_TEXTURE);
            }

            GlStateManager.disableBlend();
            GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
            GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
            GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
            GlStateManager.enableLighting();

        } else {
            float f = 0.5F;
            float f1 = f * 0.15F;
            float f2 = f * 0.3F;
            float f3 = f * 0.4F;
            float f4 = 0.0F;
            float f5 = 0.2F;
            float f6 = (float) (System.currentTimeMillis() % 100000L) / 100000.0F;
            int i = 240;

            int x = 0, y = 0, width = resolution.getScaledWidth(), height = resolution.getScaledHeight();

            GlStateManager.disableLighting();
            mc.getTextureManager().bindTexture(END_PORTAL_TEXTURE);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
            bufferbuilder.pos((double) x, (double) (y + height), 0.0D).color(f1, f2, f3, 1.0F).tex((double) (f4 + f6), (double) (f4 + f6)).lightmap(i, i).endVertex();
            bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0D).color(f1, f2, f3, 1.0F).tex((double) (f4 + f6), (double) (f5 + f6)).lightmap(i, i).endVertex();
            bufferbuilder.pos((double) (x + width), (double) y, 0.0D).color(f1, f2, f3, 1.0F).tex((double) (f5 + f6), (double) (f5 + f6)).lightmap(i, i).endVertex();
            bufferbuilder.pos((double) x, (double) y, 0.0D).color(f1, f2, f3, 1.0F).tex((double) (f5 + f6), (double) (f4 + f6)).lightmap(i, i).endVertex();
            tessellator.draw();
        }
    }


    public static float[] colorToRGBA(final int color) {
        return new float[]{(float) (color >> 16 & 255) / 255.0F,
                (float) (color >> 8 & 255) / 255.0F,
                (float) (color & 255) / 255.0F,
                (float) (color >> 24 & 255) / 255.0F};
    }

    public static void drawCheckMark(float x, float y, int width, int color) {
        float f = (float) (color >> 24 & 255) / 255.0F;
        float f1 = (float) (color >> 16 & 255) / 255.0F;
        float f2 = (float) (color >> 8 & 255) / 255.0F;
        float f3 = (float) (color & 255) / 255.0F;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(1.5F);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d((double) (x + (float) width) - 6.5D, (y + 4.0F));
        GL11.glVertex2d((double) (x + (float) width) - 11.5D, (y + 10.0F));
        GL11.glVertex2d((double) (x + (float) width) - 13.5D, (y + 8.0F));
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawPicture(ResourceLocation location, int x, int y, float width, float height) {
        GlStateManager.resetColor();
        GlStateManager.enableAlpha();
        mc.getTextureManager().bindTexture(location);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0F, 0F, (int) width, (int) height, width, height);
        GlStateManager.resetColor();
    }

    public static void drawCenteredString(String text, float x, float y, Color color, boolean dropShadow) {
        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        if (dropShadow)
            fr.drawStringWithShadow(text, x - fr.getStringWidth(text) / 2f, y - fr.FONT_HEIGHT / 2f, color.getRGB());
        else
            fr.drawString(text, x - fr.getStringWidth(text) / 2f, y - fr.FONT_HEIGHT / 2f, color.getRGB());
    }


    public static void renderEnchantment() {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/misc/enchanted_item_glint.png"));

        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        final Color color = new Color(-8372020);
        GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getRed() / 255F, 90 / 255F);
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);

        final ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());

        Gui.drawScaledCustomSizeModalRect(0, 0, 8, 8, 8, 8, resolution.getScaledWidth(), resolution.getScaledHeight(), 64, 64);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float) (Minecraft.getSystemTime() % 9000L) / 9000L / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        Gui.drawScaledCustomSizeModalRect(0, 0, 8, 8, 8, 8, resolution.getScaledWidth(), resolution.getScaledHeight(), 64, 64);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
    }

    public static double getAnimationState(double current, double finalState, double speed) {
        float add = (float) ((double) delta * speed);
        current = current < finalState ? (Math.min(current + (double) add, finalState)) : (Math.max(current - (double) add, finalState));
        return current;
    }

    public static void drawLine(double x, double y, double x1, double y1, float width, int color) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        glLineWidth(width);
        float[] rgba = colorToRGBA(color);
        glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);
        glBegin(GL_LINES);
        glVertex2d(x, y);
        glVertex2d(x1, y1);
        glEnd();
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    public static void line(Vec firstPoint, Vec secondPoint, int color) {
        line(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY(), color);
    }

    public static void line(double x, double y, double x1, double y1, int color) {
        GL11.glPushMatrix();
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean texture2D = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean lineSmooth = GL11.glIsEnabled(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(1.5F);
        GlStateManager.color((float) (color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, (float) (color >> 24 & 255) / 255.0F);
        GL11.glBegin(1);
        GL11.glVertex2f((float) x1, (float) y1);
        GL11.glVertex2f((float) x, (float) y);
        GL11.glEnd();
        if (!lineSmooth)
            GL11.glDisable(GL11.GL_LINE_SMOOTH);


        if (texture2D)
            GL11.glEnable(GL11.GL_TEXTURE_2D);


        if (!blend)
            GL11.glDisable(GL11.GL_BLEND);


        GL11.glPopMatrix();
    }

    public static void drawGradient(double x, double y, double x2, double y2, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;

        GL11.glEnable(GL_BLEND);
        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glShadeModel(GL_SMOOTH);

        GL11.glPushMatrix();
        GL11.glBegin(GL_QUADS);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);

        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glDisable(GL_BLEND);
        GL11.glDisable(GL_LINE_SMOOTH);
        GL11.glShadeModel(GL_FLAT);
    }

}
