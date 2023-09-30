package me.felix.shader.shaders;

import de.lirium.util.render.shader.FramebufferHelper;
import de.lirium.util.render.shader.ShaderProgram;
import de.lirium.util.render.shader.ShaderType;
import me.felix.shader.annotation.Info;
import me.felix.shader.container.ShaderContainer;
import me.felix.shader.data.ShaderRenderType;
import me.felix.shader.render.FramebufferQuads;
import me.felix.shader.render.Type;
import me.felix.shader.util.ProgramHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.List;

@Info(frag = "/fragment/acrylblur.glsl")
public class BlurShader extends ShaderContainer {

    public ShaderProgram shaderProgram = new ShaderProgram(vert, frag, ShaderType.GLSL);

    private Framebuffer input = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    private Framebuffer output = new Framebuffer(mc.displayWidth, mc.displayHeight, true);

    @Override
    public void reload() {
        this.status = -1;
        if (mc.displayWidth != input.framebufferWidth || mc.displayHeight != input.framebufferHeight) {
            input.deleteFramebuffer();
            input = FramebufferHelper.doFrameBuffer(input);

            output.deleteFramebuffer();
            output = FramebufferHelper.doFrameBuffer(output);
        } else {
            input.framebufferClear();
            output.framebufferClear();
        }
    }

    @Override
    public void doRender(ShaderRenderType type, Type renderType, List<Runnable> runnables) {

        this.reload();

        if (!Display.isVisible() || !Display.isActive()) {
            return;
        }

        if (runnables.isEmpty())
            status = -1;
        else
            status = 1;

        if (status == 1) {
            this.input.bindFramebuffer(true);

            runnables.forEach(Runnable::run);

            final int programId = this.shaderProgram.getProgramID();

            this.output.bindFramebuffer(true);
            this.shaderProgram.initShader();

            setupUniforms();

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

            mc.getFramebuffer().bindFramebufferTexture();

            FramebufferQuads.drawQuad();

            mc.getFramebuffer().bindFramebuffer(true);
            ProgramHelper.uniform2f(programId, "coords", 0.0f, 1.0f);
            output.bindFramebufferTexture();

            GL13.glActiveTexture(GL13.GL_TEXTURE20);

            input.bindFramebufferTexture();

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            FramebufferQuads.drawQuad();
            GlStateManager.disableBlend();

            shaderProgram.deleteShader();
        }
    }
    private void setupUniforms() {
        shaderProgram.setUniformi("currentTexture", 0);
        shaderProgram.setUniformf("blurRadius", 20);
        shaderProgram.setUniformf("blursigma", 12);

        shaderProgram.setUniformf("texelSize", (float) (1.0 / mc.displayWidth), (float) (1.0 / mc.displayHeight));
        shaderProgram.setUniformf("coords", 1, 0);

        shaderProgram.setUniformi("texture20", 20);
    }
}
