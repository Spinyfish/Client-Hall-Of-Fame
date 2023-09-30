package de.lirium.util.render.shader.shaders;

import de.lirium.util.interfaces.IMinecraft;
import de.lirium.util.render.shader.FramebufferHelper;
import de.lirium.util.render.shader.ShaderProgram;
import de.lirium.util.render.shader.ShaderType;
import de.lirium.util.render.shader.TextureRenderer;
import god.buddy.aot.BCompiler;
import net.minecraft.client.shader.Framebuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class AcrylBlurShader implements IMinecraft {

    public ShaderProgram shaderProgram = new ShaderProgram("vertex/vertex.vsh", "/fragment/acrylblur.glsl", ShaderType.GLSL);

    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void draw() {
        framebuffer = FramebufferHelper.doFrameBuffer(framebuffer);

        shaderProgram.initShader();

        //first pass
        setupUniforms(1, 0);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);
        TextureRenderer.drawTexture(framebuffer);
        framebuffer.unbindFramebuffer();

        //second pass
        shaderProgram.initShader();
        setupUniforms(0, 1);
        mc.getFramebuffer().bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        TextureRenderer.drawTexture(framebuffer);
        shaderProgram.deleteShader();
    }

    private void setupUniforms(int x, int y) {
        shaderProgram.setUniformi("currentTexture", 0);
        shaderProgram.setUniformf("texelSize", (float) (1.0 / mc.displayWidth), (float) (1.0 / mc.displayHeight));
        shaderProgram.setUniformf("coords", x, y);
        shaderProgram.setUniformf("blurRadius", 20);
        shaderProgram.setUniformf("blursigma", 12);
    }

}