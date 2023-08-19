package dev.thread.api.util.render.shader.impl;

import dev.thread.api.util.IWrapper;
import dev.thread.api.util.render.shader.Shader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static net.minecraft.client.renderer.OpenGlHelper.glUniform1;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public class BlurShader extends Shader implements IWrapper {
    private Framebuffer framebuffer = new Framebuffer(1, 1, false); // the framebuffer the blur is rendered to

    private FloatBuffer weightBuffer; // buffer that stores weights
    private float oldRadius = -1; // used to check if weights have to be recalculated

    @Override
    public final String getSource() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D textureIn;\n" +
                "uniform vec2 texelSize, direction;\n" +
                "uniform float radius;\n" +
                "uniform float weights[256];\n" +
                "\n" +
                "#define offset texelSize * direction\n" +
                "\n" +
                "void main() {\n" +
                "    if (radius == 0.0 || texelSize == vec2(0.0, 0.0) || direction == vec2(0.0, 0.0)) return;\n" +
                "\n" +
                "    vec3 blr = texture2D(textureIn, gl_TexCoord[0].st).rgb * weights[0];\n" +
                "\n" +
                "    for (float f = 1.0; f <= radius; f++) {\n" +
                "        blr += texture2D(textureIn, gl_TexCoord[0].st + f * offset).rgb * (weights[int(abs(f))]);\n" +
                "        blr += texture2D(textureIn, gl_TexCoord[0].st - f * offset).rgb * (weights[int(abs(f))]);\n" +
                "    }\n" +
                "\n" +
                "    gl_FragColor = vec4(blr, 1.0);\n" +
                "}";
    }

    public void render(float radius) {
        ScaledResolution sr = new ScaledResolution(mc);

        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(1, 1, 1, 1);

        framebuffer = createFrameBuffer(framebuffer, mc.displayWidth, mc.displayHeight);
        framebuffer.framebufferClear();

        framebuffer.bindFramebuffer(true);
        use(pid -> setupUniforms(1, 0, radius, pid));
        glBindTexture(GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);
        renderQuad(0, 0, sr.getScaledWidth(), sr.getScaledHeight());
        framebuffer.unbindFramebuffer();
        unbind();

        mc.getFramebuffer().bindFramebuffer(true);
        use(pid -> setupUniforms(0, 1, radius, pid));
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        renderQuad(0, 0, sr.getScaledWidth(), sr.getScaledHeight());
        unbind();


        glColor4f(1, 1, 1, 1);
        GlStateManager.bindTexture(0);
    }

    /**
     * Calculate weights for this radius
     * @param radius the radius to calculate weights for
     */
    private void calculateWeights(float radius) {
        if (radius != oldRadius) {
            weightBuffer = BufferUtils.createFloatBuffer(256);
            for (int i = 0; i <= radius; i++) {
                weightBuffer.put(calculateGaussianValue(i, radius / 2));
            }

            weightBuffer.rewind();
        }
        oldRadius = radius;
    }

    /**
     * set shader uniforms
     *
     * @param horiz blur horizontally
     * @param vert blur vertically
     * @param radius blur radius
     * @param pid shader program id
     */
    private void setupUniforms(int horiz, int vert, float radius, int pid) {
        glUniform1i(glGetUniformLocation(pid, "textureIn"), 0);
        glUniform2f(glGetUniformLocation(pid, "texelSize"), 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        glUniform2f(glGetUniformLocation(pid, "direction"), horiz, vert);
        glUniform1f(glGetUniformLocation(pid, "radius"), radius);

        calculateWeights(radius);
        glUniform1(glGetUniformLocation(pid, "weights"), weightBuffer);
    }

    /**
     * calculates gaussian value for blur
     * @see <a href="https://en.wikipedia.org/wiki/Gaussian_function">Gaussian function</a>
     * @see <a href="https://en.wikipedia.org/wiki/Gaussian_blur">Gaussian blur</a>
     *
     * @param x is value passed to gaussian function
     * @param sigma horizontal/vertical distance
     * @return output of gaussian function
     */
    private float calculateGaussianValue(int x, float sigma) {
        double output = 1.0 / Math.sqrt(2.0 * Math.PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    /**
     * utility function to create/update buffer when screen size changes
     * TODO: move function to separate class for FrameBuffer utilities
     *
     * @param framebuffer the buffer to be updated
     * @return new buffer
     */
    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, float width, float height) {
        if (framebuffer == null || framebuffer.framebufferWidth != width || framebuffer.framebufferHeight != height) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer((int) width, (int) height, true);
        }
        return framebuffer;
    }
}
