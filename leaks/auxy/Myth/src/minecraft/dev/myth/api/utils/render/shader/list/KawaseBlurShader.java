/**
 * @project Myth
 * @author CodeMan
 * @at 06.02.23, 18:01
 */
package dev.myth.api.utils.render.shader.list;

import dev.myth.api.interfaces.IMethods;
import dev.myth.api.utils.render.shader.KoksFramebuffer;
import dev.myth.api.utils.render.shader.ShaderAnnoation;
import dev.myth.api.utils.render.shader.ShaderProgram;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class KawaseBlurShader implements IMethods {

    public static KawaseBlurShader INSTANCE;
    private static ShaderProgram downsampleShader = new ShaderProgram("kawaseDown.glsl");
    private static ShaderProgram upsampleShader = new ShaderProgram("kawaseUp.glsl");

    private ArrayList<Framebuffer> framebuffers = new ArrayList<>();
    private ScaledResolution sr;

    private int currentPasses, offset;

    public KawaseBlurShader() {
        INSTANCE = this;
    }

    public void renderBlur(int passes, int offset) {
        this.offset = offset;
        sr = new ScaledResolution(MC);

        // set up framebuffers
        if (passes != currentPasses) {
            for (Framebuffer framebuffer : framebuffers) {
                framebuffer.deleteFramebuffer();
            }
            framebuffers.clear();

            framebuffers.add(KoksFramebuffer.doFrameBuffer(null));

            for (int i = 1; i <= passes; i++) {
                framebuffers.add(KoksFramebuffer.doFrameBuffer(new Framebuffer(MC.displayHeight, MC.displayWidth, false)));
            }
            currentPasses = passes;
        }

        // draw to framebuffers
        drawFrameBuffer(downsampleShader, framebuffers.get(1), MC.getFramebuffer().framebufferTexture);

        for (int i = 1; i < passes; i++) {
            drawFrameBuffer(downsampleShader, framebuffers.get(i + 1), framebuffers.get(i).framebufferTexture);
        }

        for (int i = passes; i > 1; i--) {
            drawFrameBuffer(upsampleShader, framebuffers.get(i - 1), framebuffers.get(i).framebufferTexture);
        }

        // draw to screen
        MC.getFramebuffer().bindFramebuffer(true);

        glBindTexture(GL_TEXTURE_2D, framebuffers.get(1).framebufferTexture);

        GL20.glUseProgram(upsampleShader.getShaderProgramID());
        upsampleShader.setShaderUniform("u_texelSize", 1F / MC.displayWidth, 1F / MC.displayHeight);
        upsampleShader.setShaderUniformI("u_texture", 0);
        upsampleShader.setShaderUniform("u_offset", offset, offset);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        float width = (float) (MC.displayWidth / sr.getScaleFactor());
        float height = (float) (MC.displayHeight / sr.getScaleFactor());

        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(0, 0, 0).tex(0, 1).endVertex();
        worldRenderer.pos(0, height, 0).tex(0, 0).endVertex();
        worldRenderer.pos(width, height, 0).tex(1, 0).endVertex();
        worldRenderer.pos(width, 0, 0).tex(1, 1).endVertex();
        tessellator.draw();

        GL20.glUseProgram(0);

        GlStateManager.enableBlend();
        MC.entityRenderer.setupOverlayRendering();
    }

    public void drawFrameBuffer(ShaderProgram shader, Framebuffer framebuffer, int framebufferTexture) {

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);

        GL20.glUseProgram(shader.getShaderProgramID());
        glBindTexture(GL_TEXTURE_2D, framebufferTexture);

        shader.setShaderUniform("u_texelSize", 1F / MC.displayWidth, 1F / MC.displayHeight);
        shader.setShaderUniformI("u_texture", 0);
        shader.setShaderUniform("u_offset", offset, offset);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        float width = (float) (MC.displayWidth / sr.getScaleFactor());
        float height = (float) (MC.displayHeight / sr.getScaleFactor());

        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(0, 0, 0).tex(0, 1).endVertex();
        worldRenderer.pos(0, height, 0).tex(0, 0).endVertex();
        worldRenderer.pos(width, height, 0).tex(1, 0).endVertex();
        worldRenderer.pos(width, 0, 0).tex(1, 1).endVertex();
        tessellator.draw();

        GL20.glUseProgram(0);

        framebuffer.unbindFramebuffer();
    }

}
