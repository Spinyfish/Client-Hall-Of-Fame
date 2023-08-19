package dev.thread.api.util.render.shader;

import dev.thread.api.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public class Shader {
    private final int programID;
    private final long startTime = System.currentTimeMillis();

    public String getSource() {
        return "";
    }

    public void use() throws IllegalArgumentException {
        if (!glIsProgram(programID)) {
            throw new IllegalArgumentException("Shader has been deleted");
        }

        glUseProgram(programID);

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        // default uniforms for every shader
        glUniform1f(glGetUniformLocation(programID, "time"), (System.currentTimeMillis() - startTime) / 1000.0f);
        glUniform2f(glGetUniformLocation(programID, "resolution"), sr.getScaledWidth(), sr.getScaledHeight());
    }

    public void use(Consumer<Integer> uniforms) {
        use();
        uniforms.accept(programID);
    }

    public void renderQuad(float x, float y, float width, float height) {
        RenderUtil.begin();
        GL11.glBegin(GL_QUADS);

        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(x + width, y);

        GL11.glEnd();
        RenderUtil.end();
    }

    public Shader() {
        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        String vertexShaderSrc = "#version 120\n" +
                "\n" +
                "void main() {\n" +
                "    gl_Position = ftransform();\n" +
                "    gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
                "}";

        GL20.glShaderSource(vertexShader, vertexShaderSrc);
        GL20.glShaderSource(fragmentShader, getSource());

        GL20.glCompileShader(vertexShader);
        GL20.glCompileShader(fragmentShader);

        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShader);
        GL20.glAttachShader(programID, fragmentShader);
        GL20.glLinkProgram(programID);

        if(GL20.glGetShaderi(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.out.println("Failed to compile " + this.getClass().getSimpleName() + ": " + GL20.glGetProgramInfoLog(programID, 512));
        }
    }

    protected void uniform1f(String name, float v) {
        GL20.glUniform1f(GL20.glGetUniformLocation(programID, name), v);
    }

    protected void uniform2f(String name, float v, float v1) {
        GL20.glUniform2f(GL20.glGetUniformLocation(programID,name), v, v1);
    }

    protected void uniform3f(String name, float v, float v1, float v2) {
        GL20.glUniform3f(GL20.glGetUniformLocation(programID,name), v, v1, v2);
    }

    protected void uniform4f(String name, float v, float v1, float v2, float v3) {
        GL20.glUniform4f(GL20.glGetUniformLocation(programID, name), v, v1, v2, v3);
    }

    protected void bind() {
        GL20.glUseProgram(programID);
    }

    protected void unbind() {
        GL20.glUseProgram(0);
    }
}
