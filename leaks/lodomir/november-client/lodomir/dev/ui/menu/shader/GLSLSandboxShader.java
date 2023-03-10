/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL20
 */
package lodomir.dev.ui.menu.shader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.lwjgl.opengl.GL20;

public class GLSLSandboxShader {
    private final int programId;
    private final int timeUniform;
    private final int mouseUniform;
    private final int resolutionUniform;

    public GLSLSandboxShader(String fragmentShaderLocation) throws IOException {
        int program = GL20.glCreateProgram();
        GL20.glAttachShader((int)program, (int)this.createShader(GLSLSandboxShader.class.getClassLoader().getResourceAsStream("assets/minecraft/passtrough.vsh"), 35633));
        GL20.glAttachShader((int)program, (int)this.createShader(GLSLSandboxShader.class.getClassLoader().getResourceAsStream(fragmentShaderLocation), 35632));
        GL20.glLinkProgram((int)program);
        int linked = GL20.glGetProgrami((int)program, (int)35714);
        if (linked == 0) {
            System.err.println(GL20.glGetProgramInfoLog((int)program, (int)GL20.glGetProgrami((int)program, (int)35716)));
            throw new IllegalStateException("Shader failed to link");
        }
        this.programId = program;
        GL20.glUseProgram((int)program);
        this.timeUniform = GL20.glGetUniformLocation((int)program, (CharSequence)"time");
        this.mouseUniform = GL20.glGetUniformLocation((int)program, (CharSequence)"mouse");
        this.resolutionUniform = GL20.glGetUniformLocation((int)program, (CharSequence)"resolution");
        GL20.glUseProgram((int)0);
    }

    public void useShader(int width, int height, float mouseX, float mouseY, float time) {
        GL20.glUseProgram((int)this.programId);
        GL20.glUniform2f((int)this.resolutionUniform, (float)width, (float)height);
        GL20.glUniform2f((int)this.mouseUniform, (float)(mouseX / (float)width), (float)(1.0f - mouseY / (float)height));
        GL20.glUniform1f((int)this.timeUniform, (float)time);
    }

    private int createShader(InputStream inputStream, int shaderType) throws IOException {
        int shader = GL20.glCreateShader((int)shaderType);
        GL20.glShaderSource((int)shader, (CharSequence)this.readStreamToString(inputStream));
        GL20.glCompileShader((int)shader);
        int compiled = GL20.glGetShaderi((int)shader, (int)35713);
        if (compiled == 0) {
            System.err.println(GL20.glGetShaderInfoLog((int)shader, (int)GL20.glGetShaderi((int)shader, (int)35716)));
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

