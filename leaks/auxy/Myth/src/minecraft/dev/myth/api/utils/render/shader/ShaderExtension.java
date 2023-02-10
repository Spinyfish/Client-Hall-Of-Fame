package dev.myth.api.utils.render.shader;

import dev.myth.api.interfaces.IMethods;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;

@UtilityClass
public class ShaderExtension implements IMethods {

    /* Credits Felix */

    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ShaderExtension.class.getName());


    public String readShader(String fileName) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(String.format("assets/minecraft/shaders2/%s", fileName))));
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            bufferedReader.close();
            inputStreamReader.close();
        } catch (IOException e) {
            LOGGER.log(Level.ALL, e.getMessage());
        }

        return stringBuilder.toString();
    }

    public void drawFrameBuffer(final Framebuffer framebuffer) {
        ScaledResolution scaledResolution = new ScaledResolution(MC);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex2d(0, 0);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2d(0, scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex2d(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(scaledResolution.getScaledWidth(), 0);
        GL11.glEnd();
        GL20.glUseProgram(0);
    }

    public void bindZero() {
        GlStateManager.bindTexture(0);
    }

    public void useShader(int programID) {
        GL20.glUseProgram(programID);
    }

    public void deleteProgram() {
        GL20.glUseProgram(0);
    }

}