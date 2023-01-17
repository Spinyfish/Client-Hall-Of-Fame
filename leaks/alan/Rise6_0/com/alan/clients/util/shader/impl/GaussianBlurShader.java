package com.alan.clients.util.shader.impl;

import com.alan.clients.util.shader.base.RiseShader;
import com.alan.clients.util.shader.base.RiseShaderProgram;
import com.alan.clients.util.shader.base.ShaderRenderType;
import com.alan.clients.util.shader.base.ShaderUniforms;
import com.alan.clients.util.shader.kernel.GaussianKernel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.nio.FloatBuffer;
import java.util.List;

public class GaussianBlurShader extends RiseShader {

    private final RiseShaderProgram blurProgram = new RiseShaderProgram("blur.frag", "vertex.vsh");
    private Framebuffer inputFramebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    private Framebuffer outputFramebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    private GaussianKernel gaussianKernel = new GaussianKernel(0);

    @Override
    public void run(final ShaderRenderType type, final float partialTicks, List<Runnable> runnable) {
        // Prevent rendering
        if (!Display.isVisible()) {
            return;
        }

        switch (type) {
            case CAMERA: {
                this.update();
                this.setActive(!runnable.isEmpty());

                if (this.isActive()) {
                    this.inputFramebuffer.bindFramebuffer(true);
                    runnable.forEach(Runnable::run);
                    mc.getFramebuffer().bindFramebuffer(true);
                }
                break;
            }
            case OVERLAY: {
                this.setActive(this.isActive() || !runnable.isEmpty());

                if (this.isActive()) {
                    this.inputFramebuffer.bindFramebuffer(true);
                    runnable.forEach(Runnable::run);


                    // TODO: make radius and other things as a setting
                    final int radius = 8;
                    final float compression = 2.0F;
                    final int programId = this.blurProgram.getProgramId();

                    this.outputFramebuffer.bindFramebuffer(true);
                    this.blurProgram.start();

                    if (this.gaussianKernel.getSize() != radius) {
                        this.gaussianKernel = new GaussianKernel(radius);
                        this.gaussianKernel.compute();

                        final FloatBuffer buffer = BufferUtils.createFloatBuffer(radius);
                        buffer.put(this.gaussianKernel.getKernel());
                        buffer.flip();

                        ShaderUniforms.uniform1f(programId, "u_radius", radius);
                        ShaderUniforms.uniformFB(programId, "u_kernel", buffer);
                        ShaderUniforms.uniform1i(programId, "u_diffuse_sampler", 0);
                        ShaderUniforms.uniform1i(programId, "u_other_sampler", 20);
                    }

                    ShaderUniforms.uniform2f(programId, "u_texel_size", 1.0F / mc.displayWidth, 1.0F / mc.displayHeight);
                    ShaderUniforms.uniform2f(programId, "u_direction", compression, 0.0F);

                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
                    mc.getFramebuffer().bindFramebufferTexture();
                    RiseShaderProgram.drawQuad();

                    mc.getFramebuffer().bindFramebuffer(true);
                    ShaderUniforms.uniform2f(programId, "u_direction", 0.0F, compression);
                    outputFramebuffer.bindFramebufferTexture();
                    GL13.glActiveTexture(GL13.GL_TEXTURE20);
                    inputFramebuffer.bindFramebufferTexture();
                    GL13.glActiveTexture(GL13.GL_TEXTURE0);
                    RiseShaderProgram.drawQuad();
                    GlStateManager.disableBlend();

                    RiseShaderProgram.stop();
                }

                break;
            }
        }
    }

    @Override
    public void update() {
        this.setActive(false);

        if (mc.displayWidth != inputFramebuffer.framebufferWidth || mc.displayHeight != inputFramebuffer.framebufferHeight) {
            inputFramebuffer.deleteFramebuffer();
            inputFramebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);

            outputFramebuffer.deleteFramebuffer();
            outputFramebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        } else {
            inputFramebuffer.framebufferClear();
            outputFramebuffer.framebufferClear();
        }
    }
}
