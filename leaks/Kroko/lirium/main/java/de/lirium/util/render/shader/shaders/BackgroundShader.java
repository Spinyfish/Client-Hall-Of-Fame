/*
 * Copyright Felix Hans from BackgroundShader coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package de.lirium.util.render.shader.shaders;

import de.lirium.util.interfaces.IMinecraft;
import de.lirium.util.render.shader.FramebufferHelper;
import de.lirium.util.render.shader.ShaderProgram;
import de.lirium.util.render.shader.ShaderType;
import de.lirium.util.render.shader.TextureRenderer;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL11.*;

public class BackgroundShader implements IMinecraft {

    private Framebuffer framebuffer;

    public ShaderProgram shaderProgram = new ShaderProgram("vertex/vertex.vsh", "/fragment/background.glsl", ShaderType.GLSL);

    private final long shaderTime;

    public BackgroundShader() {
        shaderTime = System.currentTimeMillis();
    }

    public void render() {
        framebuffer = FramebufferHelper.doFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        mc.getFramebuffer().bindFramebuffer(false);
        shaderProgram.initShader();
        handleUniforms();
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        TextureRenderer.renderFRFscreen(framebuffer);
        shaderProgram.deleteShader();
    }

    private void handleUniforms() {
        float time = (System.currentTimeMillis() - this.shaderTime) / 1000f;
        GL20.glUniform1f(shaderProgram.getUniform("time"), time);
        GL20.glUniform2f(shaderProgram.getUniform("resolution"), mc.displayWidth, mc.displayHeight);
    }

}
