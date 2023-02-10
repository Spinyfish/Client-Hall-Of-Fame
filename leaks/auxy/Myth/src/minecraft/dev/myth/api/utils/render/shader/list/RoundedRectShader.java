package dev.myth.api.utils.render.shader.list;

import dev.myth.api.utils.render.shader.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ShaderAnnoation(fragName = "rounded.glsl", renderType = ShaderRenderType.RENDER2D)
public class RoundedRectShader extends ShaderProgram {

    @Override
    public void doRender() {
        this.doRender();
    }


    public void drawRound(float x, float y, float width, float height, float radius, Color color) {
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        ShaderExtension.useShader(getShaderProgramID());
        setupRoundedRectUniforms(x, y, width, height, radius);
        setShaderUniform("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        KoksFramebuffer.drawQuads(x - 1, y - 1, width + 2, height + 2);
        ShaderExtension.deleteProgram();
        GlStateManager.disableBlend();
    }

    private void setupRoundedRectUniforms(float x, float y, float width, float height, float radius) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        setShaderUniform("location", x * sr.getScaleFactor(), (Minecraft.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        setShaderUniform("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        setShaderUniform("radius", radius * sr.getScaleFactor());
    }
}