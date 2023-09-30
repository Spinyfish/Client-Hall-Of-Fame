package club.pulsive.impl.util.render;


import club.pulsive.impl.module.impl.player.Scaffold;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.io.*;

import static club.pulsive.api.minecraft.MinecraftUtil.mc;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;

public class Shader {

    private int programID;

    public Shader(String fragmentShaderLoc, String vertexShaderLoc) {
        int program = glCreateProgram();

        try {
            glAttachShader(program, createShader(mc.getResourceManager().getResource(new ResourceLocation("pulsabo/shader/impls/" + fragmentShaderLoc)).getInputStream(), GL_FRAGMENT_SHADER));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            glAttachShader(program, createShader(mc.getResourceManager().getResource(new ResourceLocation("pulsabo/shader/" + vertexShaderLoc)).getInputStream(), GL_VERTEX_SHADER));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);

        if (status == 0) {
            throw new IllegalStateException("Shader failed to link!");
        }

        this.programID = program;
    }

    public Shader(String fragmentShaderLoc) {
        this (fragmentShaderLoc, "vertexQuad.vert");
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, readShader(inputStream));
        glCompileShader(shader);

        int state = glGetShaderi(shader, GL_COMPILE_STATUS);

        if (state == 0) {
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }

        return shader;
    }

    public void init() {
        glUseProgram(programID);
    }

    public void unInit() {
        glUseProgram(0);
    }

    public void drawCanvas(final ScaledResolution scaledResolution) {
        float width = scaledResolution.getScaledWidth();
        float height = scaledResolution.getScaledHeight();
        drawCanvas(0, 0, width, height);
    }

    public void drawCanvas(float x, float y, float width, float height) {
        if (mc.gameSettings.ofFastRender) return;
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(x, y);
        glTexCoord2f(0, 0);
        glVertex2f(x, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, y);
        glEnd();
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
    }

    public void setUniform(String name, float ... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) glUniform2f(loc, args[0], args[1]);
        else glUniform1f(loc, args[0]);
    }

    public void setUniform(String name, int ... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) glUniform2i(loc, args[0], args[1]);
        else glUniform1i(loc, args[0]);
    }

    private String readShader(InputStream inputStream) {
        final StringBuilder stringBuilder = new StringBuilder();

        try {
            final InputStreamReader inputReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputReader);

            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public int getProgramID() {
        return programID;
    }

}