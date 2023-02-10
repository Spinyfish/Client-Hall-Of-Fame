package dev.myth.api.utils.render.shader;

import dev.myth.api.interfaces.IMethods;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL20;

import java.util.logging.Level;
import java.util.logging.Logger;

import static net.minecraft.client.renderer.GlStateManager.glBegin;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

@Getter
@Setter
public class ShaderProgram implements IMethods {

    /* Credits Felix */

    private final static Logger LOGGER = Logger.getLogger(ShaderProgram.class.getName());

    private final int shaderProgramID;

    public String fragmentName = getClass().isAnnotationPresent(ShaderAnnoation.class) ? getClass().getAnnotation(ShaderAnnoation.class).fragName() : "";

    private final ShaderRenderType shaderRenderType = getClass().isAnnotationPresent(ShaderAnnoation.class) ? getClass().getAnnotation(ShaderAnnoation.class).renderType() : ShaderRenderType.RENDER2D;

    public void doRender() {

        switch (shaderRenderType) {

            case RENDER2D: {
                GlStateManager.enableBlend();
                MC.entityRenderer.setupOverlayRendering();
            }
            break;
            case RENDER3D: {
                GlStateManager.enableBlend();
                MC.entityRenderer.setupCameraTransform(ShaderPartialTicks.partialTicks, 0);
            }
        }

    }

    public ShaderProgram(String fragName) {

        final int shaderProgramID = GL20.glCreateProgram();

        final String vertexSource = ShaderExtension.readShader("vertex/vertex.glsl");

        final int vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

        { //compiles vertex shader
            GL20.glShaderSource(vertexShaderID, vertexSource);
            GL20.glCompileShader(vertexShaderID);


            if (GL20.glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
                LOGGER.log(Level.ALL, glGetShaderInfoLog(vertexShaderID, 4096));
                throw new IllegalStateException("Unable to decompile vertex shader: " + GL_VERTEX_SHADER);
            }
        }

        //compiles frag or glsl shader
        final String fragmentSource = ShaderExtension.readShader("fragment/" + fragName);
        final int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, fragmentSource);
        glCompileShader(fragmentShaderID);

        if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.out.println(glGetShaderInfoLog(fragmentShaderID, 4096));
            throw new IllegalStateException("Unable to decompile shader: " + fragName + GL_VERTEX_SHADER);
        }

        glAttachShader(shaderProgramID, vertexShaderID);
        glAttachShader(shaderProgramID, fragmentShaderID);
        glLinkProgram(shaderProgramID);

        this.shaderProgramID = shaderProgramID;
    }


    public ShaderProgram() {

        final int shaderProgramID = GL20.glCreateProgram();

        final String vertexSource = ShaderExtension.readShader("vertex/vertex.glsl");

        final int vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

        { //compiles vertex shader
            GL20.glShaderSource(vertexShaderID, vertexSource);
            GL20.glCompileShader(vertexShaderID);


            if (GL20.glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
                LOGGER.log(Level.ALL, glGetShaderInfoLog(vertexShaderID, 4096));
                throw new IllegalStateException("Unable to decompile vertex shader: " + GL_VERTEX_SHADER);
            }
        }

        //compiles frag or glsl shader
        final String fragmentSource = ShaderExtension.readShader("fragment/" + fragmentName);
        final int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, fragmentSource);
        glCompileShader(fragmentShaderID);

        if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.out.println(glGetShaderInfoLog(fragmentShaderID, 4096));
            throw new IllegalStateException("Unable to decompile shader: " + fragmentName + GL_VERTEX_SHADER);
        }

        glAttachShader(shaderProgramID, vertexShaderID);
        glAttachShader(shaderProgramID, fragmentShaderID);
        glLinkProgram(shaderProgramID);

        this.shaderProgramID = shaderProgramID;
    }

    public float texelHeight(double... downscale) {

        double downScale = downscale[0] == 0.0 ? 1.0 : downscale[0];

        final ScaledResolution scaledResolution = new ScaledResolution(MC);
        return (float) (downScale / scaledResolution.getScaledHeight());
    }

    public float texelWidth(double... downscale) {

        double downScale = downscale[0] == 0.0 ? 1.0 : downscale[0];
        final ScaledResolution scaledResolution = new ScaledResolution(MC);
        return (float) (downScale / scaledResolution.getScaledWidth());
    }

    public int getUniform(String name) {
        return glGetUniformLocation(shaderProgramID, name);
    }

    public void setShaderUniformI(String name, int... args) {
        int loc = glGetUniformLocation(getShaderProgramID(), name);
        if (args.length > 1) {
            glUniform2i(loc, args[0], args[1]);
        } else {
            glUniform1i(loc, args[0]);
        }
    }

    public void drawQuads(ScaledResolution sr) {
        float width = sr.getScaledWidth();
        float height = sr.getScaledHeight();
        glBegin(GL_QUADS);
        glTexCoord2f(0f, 1f);
        glVertex2f(0f, 0f);
        glTexCoord2f(0f, 0f);
        glVertex2f(0f, height);
        glTexCoord2f(1f, 0f);
        glVertex2f(width, height);
        glTexCoord2f(1f, 1f);
        glVertex2f(width, 0f);
        glEnd();
    }

    public void setShaderUniform(String name, float... args) {
        int loc = glGetUniformLocation(shaderProgramID, name);
        if (args.length > 1) {
            if (args.length > 2) {
                if (args.length > 3) {
                    GL20.glUniform4f(loc, args[0], args[1], args[2], args[3]);
                } else {
                    GL20.glUniform3f(loc, args[0], args[1], args[2]);
                }
            } else {
                GL20.glUniform2f(loc, args[0], args[1]);
            }
        } else {
            GL20.glUniform1f(loc, args[0]);
        }
    }
}