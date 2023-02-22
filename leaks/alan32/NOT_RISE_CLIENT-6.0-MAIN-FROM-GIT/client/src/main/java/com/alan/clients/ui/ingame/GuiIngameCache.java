package com.alan.clients.ui.ingame;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import static com.alan.clients.util.interfaces.InstanceAccess.mc;

public class GuiIngameCache {

    private static final Minecraft MC = Minecraft.getMinecraft();
    private static Framebuffer framebuffer;

    // TODO: SET DIRTY TO TRUE IN END OF RUN TICK
    public static boolean dirty;

    public static void renderGameOverlay(float partialTicks) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);

        // Don't optimize if framebuffers are not enabled or optimization should not be used
        if (!OpenGlHelper.isFramebufferEnabled()) {
//            MC.ingameGUI.renderGameOverlay(partialTicks);
        }

        // Screen size
        int width = MC.displayWidth;
        int height = MC.displayHeight;

        // TODO: Do stuff here before cached hud is used (e.g. cross-hair because buggy)
        GlStateManager.enableBlend();

        // Cross-hair
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(Gui.icons);
        GlStateManager.enableBlend();

        if (mc.ingameGUI.showCrosshair()) {
            GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
            GlStateManager.enableAlpha();
            mc.ingameGUI.drawTexturedModalRect(scaledResolution.getScaledWidth() / 2f - 7,
                    scaledResolution.getScaledHeight() / 2 - 7, 0, 0, 16, 16);
        }

        // Render framebuffer content
        if (framebuffer != null) {
            renderCrosshair(width / 2 - 7, height / 2 - 7);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1, 1, 1, 1);
            framebuffer.bindFramebufferTexture();
            drawTexturedRect(0, 0, (float) scaledResolution.getScaledWidth(), (float) scaledResolution.getScaledHeight(), 0, 1, 1, 0, GL11.GL_NEAREST);
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        }


        // If dirty, update framebuffer content
        if (dirty) {

            // Re-create/clear framebuffer
            framebuffer = refreshFramebuffer(framebuffer, width, height);

            // Bind cache framebuffer
            framebuffer.framebufferClear();
            framebuffer.bindFramebuffer(false);

            // Calling correct gl functions before rendering to avoid errors
            GlStateManager.disableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableLighting();
            GlStateManager.disableFog();

            // Enabling caching in GlStateManager, render overlay and disable it again
            GlStateManager.caching = true;
            MC.ingameGUI.renderGameOverlay(partialTicks);
            GlStateManager.caching = false;

            // Re-bind minecraft framebuffer and un-dirty
            MC.getFramebuffer().bindFramebuffer(false);
            GlStateManager.enableBlend();
            dirty = false;
        }
    }

    public static void renderCrosshair(int x, int y) {
        MC.getTextureManager().bindTexture(Gui.icons);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
        GlStateManager.enableAlpha();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + 16, 100).tex(0, 16 * 0.00390625F).endVertex();
        worldrenderer.pos(x + 16, y + 16, 100).tex(16 * 0.00390625F, 16 * 0.00390625F).endVertex();
        worldrenderer.pos(x + 16, y, 100).tex(16 * 0.00390625F, 0).endVertex();
        worldrenderer.pos(x, y, 100).tex(0, 0).endVertex();
        tessellator.draw();
    }

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableTexture2D();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(uMin, vMax).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex(uMax, vMax).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex(uMax, vMin).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(uMin, vMin).endVertex();
        tessellator.draw();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }

    public static Framebuffer refreshFramebuffer(Framebuffer framebuffer, int width, int height) {
        if (framebuffer == null) {
            framebuffer = new Framebuffer(width, height, true);
            framebuffer.setFramebufferFilter(GL11.GL_NEAREST);
            framebuffer.framebufferColor[0] = 0;
            framebuffer.framebufferColor[1] = 0;
            framebuffer.framebufferColor[2] = 0;
        } else if (framebuffer.framebufferWidth != width || framebuffer.framebufferHeight != height) {
            framebuffer.createBindFramebuffer(width, height);
            framebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        }

        GuiIngameCache.framebuffer = framebuffer;
        return framebuffer;
    }
}