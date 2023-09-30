package dev.elysium.client.utils.render;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class BlurUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private final ResourceLocation resourceLocation;
    private ShaderGroup shaderGroup;
    private Framebuffer framebuffer;

    private int lastFactor;
    private int lastWidth;
    private int lastHeight;

    public BlurUtil() {
        this.resourceLocation = new ResourceLocation("Elysium/blur.json");
    }

    public void init() {
        try {
            this.shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), resourceLocation);
            this.shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            this.framebuffer = shaderGroup.mainFramebuffer;
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private void setValues(int strength) {
        for(int i = 0; i <= 4; i++) {
            try {
                this.shaderGroup.getShaders().get(i).getShaderManager().getShaderUniform("Radius").set(strength);
            } catch (Exception e) {

            }
        }
    }

    public final void blur(int blurStrength) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);

        final int scaleFactor = scaledResolution.getScaleFactor();
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();

        this.lastFactor = scaleFactor;
        this.lastWidth = width;
        this.lastHeight = height;

        setValues(blurStrength);
        if (sizeHasChanged(scaleFactor, width, height) || framebuffer == null || shaderGroup == null) {
            init();
        }
        framebuffer.bindFramebuffer(true);
        shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.enableAlpha();
    }

    public final void blurArea(int x, int y, int x2, int y2, int rounding, int blurStrength) {
        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.scissor(x, y, x2 - x, y2 - y);
        blur(blurStrength);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GlStateManager.popMatrix();
    }

    private boolean sizeHasChanged(int scaleFactor, int width, int height) {
        return (lastFactor != scaleFactor || lastWidth != width || lastHeight != height);
    }

}
