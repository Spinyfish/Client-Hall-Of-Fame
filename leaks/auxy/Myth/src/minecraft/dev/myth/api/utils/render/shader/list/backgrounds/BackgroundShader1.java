/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 12.10.22, 21:17
 */

/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 12.10.22, 16:45
 */
package dev.myth.api.utils.render.shader.list.backgrounds;

import dev.myth.api.utils.render.shader.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

@ShaderAnnoation(fragName = "background1.glsl", renderType = ShaderRenderType.NONE)
public class BackgroundShader1 extends ShaderProgram {

    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    public final long startTime;

    public BackgroundShader1() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void doRender() {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        framebuffer = KoksFramebuffer.doFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        MC.getFramebuffer().bindFramebuffer(true);
        MC.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        ShaderExtension.useShader(getShaderProgramID());
        setUniforms();
        MC.entityRenderer.setupOverlayRendering();
        KoksFramebuffer.renderFRFscreen(framebuffer);
        ShaderExtension.deleteProgram();
        GlStateManager.disableBlend();
        MC.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
        GlStateManager.disableDepth();
        super.doRender();
    }

    private void setUniforms() {
        float time = (System.currentTimeMillis() - this.startTime) / 700f;
        setShaderUniform("resolution", MC.displayWidth, MC.displayHeight);
        setShaderUniform("time", time);
    }

}
