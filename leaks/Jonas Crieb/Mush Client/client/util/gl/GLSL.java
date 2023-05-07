/*
 * Decompiled with CFR 0.150.
 */
package client.util.gl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.lwjgl.opengl.GL20;

public class GLSL {
    private final int programId;
    private final int timeUniform;
    private final int mouseUniform;
    private final int resolutionUniform;

    public GLSL(String fragmentShaderLocation) throws IOException {
        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, this.createShader(GLSL.class.getResourceAsStream("/assets/minecraft/stardust/shaders/passthrough.vsh"), 35633));
        GL20.glAttachShader(program, this.createShader(GLSL.class.getResourceAsStream(fragmentShaderLocation), 35632));
        GL20.glLinkProgram(program);
        int linked = GL20.glGetProgrami(program, 35714);
        if (linked == 0) {
            System.err.println(GL20.glGetProgramInfoLog(program, GL20.glGetProgrami(program, 35716)));
            throw new IllegalStateException("Shader failed to link");
        }
        this.programId = program;
        GL20.glUseProgram(program);
        this.timeUniform = GL20.glGetUniformLocation(program, "time");
        this.mouseUniform = GL20.glGetUniformLocation(program, "mouse");
        this.resolutionUniform = GL20.glGetUniformLocation(program, "resolution");
        GL20.glUseProgram(0);
    }

    public void useShader(int width, int height, float mouseX, float mouseY, float time) {
        GL20.glUseProgram(this.programId);
        GL20.glUniform2f(this.resolutionUniform, width, height);
        GL20.glUniform2f(this.mouseUniform, mouseX / (float)width, 1.0f - mouseY / (float)height);
        GL20.glUniform1f(this.timeUniform, time);
    }

    private int createShader(InputStream inputStream, int shaderType) throws IOException {
        int shader = GL20.glCreateShader(shaderType);
        GL20.glShaderSource(shader, this.readStreamToString(inputStream));
        GL20.glCompileShader(shader);
        int compiled = GL20.glGetShaderi(shader, 35713);
        if (compiled == 0) {
            System.err.println(GL20.glGetShaderInfoLog(shader, GL20.glGetShaderi(shader, 35716)));
            throw new IllegalStateException("Failed to compile shader");
        }
        return shader;
    }

    private String readStreamToString(InputStream inputStream) throws IOException {
        int read;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[512];
        while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, read);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}

