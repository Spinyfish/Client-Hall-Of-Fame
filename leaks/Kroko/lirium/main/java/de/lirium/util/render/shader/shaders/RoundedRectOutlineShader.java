package de.lirium.util.render.shader.shaders;

import de.lirium.util.interfaces.IMinecraft;
import de.lirium.util.render.shader.ShaderProgram;
import de.lirium.util.render.shader.ShaderType;
import de.lirium.util.render.shader.TextureRenderer;
import god.buddy.aot.BCompiler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RoundedRectOutlineShader implements IMinecraft {

    public ShaderProgram shaderProgram = new ShaderProgram("vertex/vertex.vsh", "/fragment/roundedoutline.glsl", ShaderType.GLSL);

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void drawRound(float x, float y, float width, float height, float radius, float outlineThickness, Color color) {
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        shaderProgram.initShader();
        setupRoundedRectUniforms(x, y, width, height, radius, outlineThickness);
        shaderProgram.setUniformf("outlineColor", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        TextureRenderer.drawTexture(x - 1, y - 1, width + 2, height + 2);
        shaderProgram.deleteShader();
        GlStateManager.disableBlend();
    }

    private void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, float outlineThickness) {
        final ScaledResolution sr = new ScaledResolution(mc);
        shaderProgram.setUniformf("location", x * sr.getScaleFactor(), (mc.displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        shaderProgram.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        shaderProgram.setUniformf("radius", radius * sr.getScaleFactor());
        shaderProgram.setUniformf("outlineThickness", outlineThickness);
    }

}