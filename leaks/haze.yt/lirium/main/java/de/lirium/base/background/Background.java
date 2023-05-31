package de.lirium.base.background;

import de.lirium.base.feature.Feature;
import de.lirium.util.render.shader.FramebufferHelper;
import de.lirium.util.render.shader.ShaderProgram;
import de.lirium.util.render.shader.TextureRenderer;
import god.buddy.aot.BCompiler;
import lombok.AllArgsConstructor;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL11.*;

public class Background implements Feature {
    private final String name;
    public final ShaderProgram program;
    private final long shaderTime;

    public int draggedX;

    public Background(String name, ShaderProgram program) {
        this.name = name;
        this.program = program;
        this.shaderTime = System.currentTimeMillis();
    }

    private Framebuffer framebuffer;

    @Override
    public String getName() {
        return name;
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void render() {
        framebuffer = FramebufferHelper.doFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        mc.getFramebuffer().bindFramebuffer(false);
        program.initShader();
        handleUniforms();
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        TextureRenderer.renderFRFscreen(framebuffer);
        program.deleteShader();
    }

    public void handleUniforms() {
        float time = (System.currentTimeMillis() - this.shaderTime) / 1000f;
        GL20.glUniform1f(program.getUniform("time"), time);
        GL20.glUniform2f(program.getUniform("resolution"), mc.displayWidth + draggedX, mc.displayHeight);
    }
}
