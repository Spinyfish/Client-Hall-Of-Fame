package dev.africa.pandaware.impl.ui.shader.utils;

import dev.africa.pandaware.utils.client.Printer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

@UtilityClass
public class ShaderUtils {
    public ShaderProgram loadShader(String vertexPath, String fragmentPath) {
        return createShaderProgram(BufferUtils.readFile(vertexPath), BufferUtils.readFile(fragmentPath));
    }

    public ShaderProgram createShaderProgram(String vertexShader, String fragmentShader) {
        int program = GL20.glCreateProgram();

        int vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        int fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        GL20.glShaderSource(vertexShaderID, vertexShader);
        GL20.glShaderSource(fragmentShaderID, fragmentShader);

        GL20.glCompileShader(vertexShaderID);
        if (GL20.glGetShaderi(vertexShaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            Printer.consoleError("Vertex shader failed to compile");
            Printer.consoleError(GL20.glGetShaderInfoLog(vertexShaderID, 500));
        }

        GL20.glCompileShader(fragmentShaderID);
        if (GL20.glGetShaderi(fragmentShaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            Printer.consoleError("Fragment shader failed to compile");
            Printer.consoleError(GL20.glGetShaderInfoLog(fragmentShaderID, 500));
        }

        GL20.glAttachShader(program, vertexShaderID);
        GL20.glAttachShader(program, fragmentShaderID);

        GL20.glLinkProgram(program);
        GL20.glValidateProgram(program);

        return new ShaderProgram(program, vertexShaderID, fragmentShaderID);
    }

    @Getter
    @AllArgsConstructor
    public static class ShaderProgram {
        private final int programID;
        private final int vertexShaderID;
        private final int fragmentShaderID;
    }
}