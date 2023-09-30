package dev.echo.ui;

import dev.echo.Echo;
import dev.echo.utils.Utils;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.animations.Direction;
import dev.echo.utils.animations.impl.DecelerateAnimation;
import dev.echo.utils.animations.impl.EaseBackIn;
import dev.echo.utils.font.CustomFont;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.RenderUtil;
import dev.echo.utils.render.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class SplashScreen implements Utils {

    private static int currentProgress;

    // Background texture
    private static ResourceLocation splash;
    private static Framebuffer framebuffer;


    static ResourceLocation image = new ResourceLocation("Echo/splashscreen.png");

    private static int count;


    public static void continueCount() {
        continueCount(true);
    }

    public static void continueCount(boolean continueCount) {
        drawSplash();
        if(continueCount){
            count++;
        }
    }

    /**
     * Render the splash screen background
     */
    private static void drawSplash() {

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        // Create the scale factor
        int scaleFactor = sr.getScaleFactor();
        // Bind the width and height to the framebuffer
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);

        // Create the projected image to be rendered
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();


        GlStateManager.color(0, 0, 0, 0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawSplashBackground(sr.getScaledWidth(), sr.getScaledHeight(), 1);

        RenderUtil.resetColor();
        GL11.glEnable(GL11.GL_BLEND);
        RenderUtil.setAlphaLimit(0);

        CustomFont fr = echoBoldFont80;

        if(count > 3){
            count = 0;
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < count; i++){
            sb.append(".");
        }


        fr.drawCenteredString("Loading" + sb, sr.getScaledWidth() / 2f, fr.getMiddleOfBox(sr.getScaledHeight()), -1);
        // Unbind the width and height as it's no longer needed
        framebuffer.unbindFramebuffer();

        // Render the previously used frame buffer
        framebuffer.framebufferRender(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor);

        // Update the texture to enable alpha drawing
        RenderUtil.setAlphaLimit(1);

        // Update the users screen
        Minecraft.getMinecraft().updateDisplay();
    }


    private static Animation fadeAnim;
    private static Animation moveAnim;
    private static Animation versionAnim;
    private static Animation progressAnim;
    private static Animation progress2Anim;

    private static void drawScreen(float width, float height) {
        Gui.drawRect2(0, 0, width, height, Color.BLACK.getRGB());
        if (fadeAnim == null) {
            fadeAnim = new DecelerateAnimation(600, 1);
            moveAnim = new DecelerateAnimation(600, 1);
        }

        drawSplashBackground(width, height, 1);

        CustomFont fr = echoBoldFont80;

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < count; i++){
            sb.append(".");
        }

        fr.drawCenteredString("Loading" + sb, width / 2f, fr.getMiddleOfBox(height),
                ColorUtil.applyOpacity(-1, 1 - fadeAnim.getOutput().floatValue()));


        float yMovement = progressAnim != null && progressAnim.getDirection().backwards() ? 1 - progressAnim.getOutput().floatValue() : 0;
        float actualY = (fr.getMiddleOfBox(height) - 40) - (48 * yMovement);
        fr.drawCenteredString("Echo", width / 2f, actualY,
                ColorUtil.applyOpacity(-1, moveAnim.getOutput().floatValue()));

        if (moveAnim.isDone() && versionAnim == null) {
            versionAnim = new EaseBackIn(500, 1, 2);
        }


        if (versionAnim != null) {

            float versionWidth = echoFont32.getStringWidth(Echo.VERSION) / 2f;
            float versionX = width / 2f + fr.getStringWidth("Echo") / 2f - (versionWidth);
            float versionY = (echoFont32.getMiddleOfBox(height) - 57) - (48 * yMovement);

            RenderUtil.scaleStart(versionX + versionWidth, versionY + echoFont32.getHeight() / 2f, versionAnim.getOutput().floatValue());

            echoFont32.drawSmoothString(Echo.VERSION, versionX, versionY, ColorUtil.applyOpacity(-1, versionAnim.getOutput().floatValue()));
            RenderUtil.scaleEnd();
        }


        if (progressAnim == null) {
            if (moveAnim.isDone()) {
                progressAnim = new DecelerateAnimation(250, 1);
                progress2Anim = new DecelerateAnimation(1800, 1);
            }
        } else {
            float rectWidth = fr.getStringWidth("Echo") + 10;
            float rectHeight = 5;
            float roundX = (width / 2f - rectWidth / 2f);
            float roundY = height / 2f - rectHeight / 2f;
            float roundAlpha = progressAnim.getOutput().floatValue();
            RoundedUtil.drawRound(roundX, height / 2f - rectHeight / 2f, rectWidth, rectHeight,
                    (rectHeight / 2f) - .25f, ColorUtil.tripleColor(50, roundAlpha));

            float progress = progress2Anim.getOutput().floatValue();
            Color color1 = ColorUtil.interpolateColorC(Echo.INSTANCE.getClientColor(), Echo.INSTANCE.getAlternateClientColor(), progress);
            Color color2 = ColorUtil.interpolateColorC(Echo.INSTANCE.getAlternateClientColor(), Echo.INSTANCE.getClientColor(), progress);
            RoundedUtil.drawGradientHorizontal(roundX, roundY, rectWidth * progress, rectHeight,
                    (rectHeight / 2f) - .25f, ColorUtil.applyOpacity(color1, roundAlpha), ColorUtil.applyOpacity(color2, roundAlpha));


            float textAlpha = progress2Anim.getDirection().forwards() ? progress2Anim.getOutput().floatValue() : 1;
            echoFont18.drawCenteredString("by liticane, Nyghtfull and stephen", width / 2f, actualY + 42, ColorUtil.applyOpacity(-1, textAlpha));


            if (progressAnim.finished(Direction.FORWARDS) && progress2Anim.finished(Direction.FORWARDS)) {
                progressAnim.changeDirection();
            }

        }


    }

    public static void drawScreen() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        // Create the scale factor
        int scaleFactor = sr.getScaleFactor();
        // Bind the width and height to the framebuffer
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);


        while (progressAnim == null || !progressAnim.finished(Direction.BACKWARDS)) {
            framebuffer.framebufferClear();
            framebuffer.bindFramebuffer(true);
            // Create the projected image to be rendered
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();


            GlStateManager.color(0, 0, 0, 0);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            drawScreen(sr.getScaledWidth(), sr.getScaledHeight());

            // Unbind the width and height as it's no longer needed
            framebuffer.unbindFramebuffer();

            // Render the previously used frame buffer
            framebuffer.framebufferRender(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor);

            // Update the texture to enable alpha drawing
            RenderUtil.setAlphaLimit(1);

            // Update the users screen
            Minecraft.getMinecraft().updateDisplay();
        }
    }


    public static void drawSplashBackground(float width, float height, float alpha) {
        RenderUtil.resetColor();
        GlStateManager.color(1, 1, 1, alpha);
        mc.getTextureManager().bindTexture(new ResourceLocation("Echo/splashscreen.png"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);
    }


}
