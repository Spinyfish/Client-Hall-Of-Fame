package store.femboy.spring.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;
import store.femboy.spring.impl.module.impl.visual.Blur;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform2f;

public class BlurUtil {
    public static final String BLUR_STRING = "#version 120\n" +
            "\n" +
            "uniform sampler2D texture;\n" +
            "uniform vec2 texelSize;\n" +
            "uniform vec2 direction;\n" +
            "uniform float radius;\n" +
            "\n" +
            "float gauss(float offset, float sigma) {\n" +
            "    return ((1.0 / sqrt(2.0 * 3.1415926 * sigma * sigma)) * (pow((2.7182818284), -(offset * offset) / (2.0 * sigma * sigma))));\n" +
            "}\n" +
            "\n" +
            "void main() {\n" +
            "    vec4 color = vec4(0.0);\n" +
            "    vec2 texCoord = gl_TexCoord[0].st;\n" +
            "    for (float r = -radius; r <= radius; r++) {\n" +
            "        color += texture2D(texture, texCoord + r * texelSize * direction) * gauss(r, radius / 2);\n" +
            "    }\n" +
            "    gl_FragColor = vec4(color.rgb, 1.0);\n" +
            "}";
    public static final ShaderUtil gaussian = new ShaderUtil(BLUR_STRING);
    private static Framebuffer framebuffer, sec;

    public static void drawBlur2() {
        if (framebuffer == null || sec == null || !Display.isActive()) return;
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        sec.framebufferClear();
        sec.bindFramebuffer(false);
        drawQuads();
        framebuffer.bindFramebuffer(false);
        gaussian.startProgram();
        updateUniforms(1);
        RenderUtil.drawFrameBuffer(sr, Minecraft.getMinecraft().getFramebuffer());
        gaussian.stopProgram();
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        gaussian.startProgram();
        updateUniforms(0);
        RenderUtil.bindTexture(sec.framebufferTexture);
        RenderUtil.drawFrameBuffer(sr, framebuffer);
        GlStateManager.color(1, 1, 1, 1);
        gaussian.stopProgram();
    }

    private static void updateUniforms(float sex) {
        glUniform1i(gaussian.getUniformLoc("texture"), 0);
        glUniform1f(gaussian.getUniformLoc("radius"), Blur.radius.getValue());
        glUniform2f(gaussian.getUniformLoc("texelSize"), 1.0f / Display.getWidth(), 1.0f / Display.getHeight());
        glUniform2f(gaussian.getUniformLoc("direction"), 1 - sex, sex);
    }

    public static void drawQuads() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float width = (float) sr.getScaledWidth_double();
        float height = (float) sr.getScaledHeight_double();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(0, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, 0);
        glEnd();
    }

    public static void onFrameBufferResize(final int width, final int height) {
        if (framebuffer != null)
            framebuffer.deleteFramebuffer();

        if (sec != null)
            sec.deleteFramebuffer();
        framebuffer = new Framebuffer(width, height, false);
        sec = new Framebuffer(width, height, false);
    }

    public static void setupBlur() {
        StencilUtil.INSTANCE.initStencilToWrite();
    }

    public static void doBlur() {
        StencilUtil.INSTANCE.readStencilBuffer(1);
        BlurUtil.drawBlur2();
        StencilUtil.INSTANCE.uninitStencilBuffer();
    }
}