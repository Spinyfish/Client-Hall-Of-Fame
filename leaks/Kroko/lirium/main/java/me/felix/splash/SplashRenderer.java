package me.felix.splash;

import de.lirium.Client;
import de.lirium.base.animation.Animation;
import de.lirium.base.animation.Easings;
import de.lirium.util.interfaces.IMinecraft;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.shader.FramebufferHelper;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class SplashRenderer {

    public Framebuffer framebuffer;

    public SplashRenderer(Framebuffer framebuffer) {
        this.framebuffer = framebuffer;
    }

    @Getter
    private SplashProcess splashProcess = new SplashProcess();

    private final Animation animation = new Animation();

    private FontRenderer descriptionFont = null;

    public void changeSplash(String text, int value) {
        splashProcess = new SplashProcess();
        splashProcess.updateStage(text, value);
        render();
    }

    public void render() {
        useFramebuffer();

        final ScaledResolution scaledResolution = new ScaledResolution(IMinecraft.mc);

        enableTextureSupport(scaledResolution);

        color();

        animation.update();
        animation.animate((double)splashProcess.currentStage.process * 2.3, .4, Easings.BACK_IN);

        Gui.drawRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), new Color(9, 9, 9).getRGB());

        final int barWidth = 100;

        RenderUtil.drawRoundedRect(scaledResolution.getScaledWidth() / 2f - barWidth,
                (float) (scaledResolution.getScaledHeight() / 2 - 10), (float) (barWidth * 2), 30F, 3F, new Color(20, 20, 20));

        RenderUtil.drawRoundedRect(scaledResolution.getScaledWidth() / 2f - animation.getValue(),
                (float) (scaledResolution.getScaledHeight() / 2 - 10), (float) (animation.getValue() * 2), 30F, 3F, new Color(73, 37, 245));

        if (Client.INSTANCE.fontLoader == null) return;
        if (descriptionFont == null)
            descriptionFont = Client.INSTANCE.fontLoader.get("Arial Bold", 30);
        descriptionFont.drawRainbowString(splashProcess.currentStage.text, 10, 10);

        final String percent = splashProcess.currentStage.process + "%";
        descriptionFont.drawString(percent, scaledResolution.getScaledWidth() / 2f - descriptionFont.getStringWidth(percent) / 2F, scaledResolution.getScaledHeight() / 2F - 3, -1);

        deleteFramebuffer(scaledResolution);
    }

    private void deleteFramebuffer(ScaledResolution scaledResolution) {
        final int scaleFactor = scaledResolution.getScaleFactor();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(
                scaledResolution.getScaledWidth() * scaleFactor,
                scaledResolution.getScaledHeight() * scaleFactor
        );
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        IMinecraft.mc.updateDisplay();
    }

    private void color() {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.resetColor();
    }

    private void enableTextureSupport(ScaledResolution scaledResolution) {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(
                0.0,
                scaledResolution.getScaledWidth_double(),
                scaledResolution.getScaledHeight_double(),
                0.0,
                1000.0,
                3000.0
        );
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
    }

    private void useFramebuffer() {
        framebuffer = FramebufferHelper.doFrameBuffer(framebuffer);

        framebuffer.framebufferClear();

        framebuffer.bindFramebuffer(true);
    }

}
