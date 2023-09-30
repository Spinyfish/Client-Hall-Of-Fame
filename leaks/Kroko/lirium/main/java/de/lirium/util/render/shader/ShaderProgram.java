package de.lirium.util.render.shader;


import de.lirium.util.interfaces.IMinecraft;
import de.lirium.util.interfaces.Logger;
import god.buddy.aot.BCompiler;
import lombok.Getter;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram implements IMinecraft {

    private final String vertexName, fragmentName;

    @Getter
    private final int programID;

    private final ShaderType shaderType;


    public ShaderProgram(String vertexName, String fragmentName, ShaderType shaderType) {
        this.vertexName = vertexName;
        this.fragmentName = fragmentName;
        this.shaderType = shaderType;

        String ending = "";

        switch (shaderType) {
            case VERTEX:
                ending += ".vsh";
                break;
            case GLSL:
                ending += ".glsl";
                break;
        }
        final int program = glCreateProgram();

        final String vertexSource = ShaderUtils.readShader(vertexName);

        final int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, vertexSource);
        glCompileShader(vertexShaderID);

        if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            Logger.print(glGetShaderInfoLog(vertexShaderID, 4096));
            System.out.printf("Vertex Shader (%s) failed to compile!%n", GL_VERTEX_SHADER);
            this.programID = 0;
            return;
        }

        final String fragmentSource = ShaderUtils.readShader(fragmentName);
        int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, fragmentSource);
        glCompileShader(fragmentShaderID);

        if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            Logger.print(glGetShaderInfoLog(fragmentShaderID, 4096));
            System.out.printf("Fragment Shader failed to compile!: " + fragmentName, GL_FRAGMENT_SHADER);
            this.programID = 0;
            return;
        }

        glAttachShader(program, vertexShaderID);
        glAttachShader(program, fragmentShaderID);
        glLinkProgram(program);
        this.programID = program;
    }

    public ShaderProgram(String vertexName, String fragment) {
        this.vertexName = vertexName;
        this.fragmentName = "background";
        this.shaderType = ShaderType.GLSL;

        String ending = ".glsl";
        final int program = glCreateProgram();

        final String vertexSource = ShaderUtils.readShader(vertexName);

        final int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, vertexSource);
        glCompileShader(vertexShaderID);

        if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            Logger.print(glGetShaderInfoLog(vertexShaderID, 4096));
            System.out.printf("Vertex Shader (%s) failed to compile!%n", GL_VERTEX_SHADER);
            this.programID = 0;
            return;
        }

        int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, fragment);
        glCompileShader(fragmentShaderID);

        if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            Logger.print(glGetShaderInfoLog(fragmentShaderID, 4096));
            System.out.printf("Fragment Shader failed to compile!: background shader", GL_FRAGMENT_SHADER);
            this.programID = 0;
            return;
        }

        glAttachShader(program, vertexShaderID);
        glAttachShader(program, fragmentShaderID);
        glLinkProgram(program);
        this.programID = program;
    }

    public void initShader() {
        glUseProgram(this.programID);
    }

    public void deleteShader() {
        glUseProgram(0);
    }

    public int getUniform(String name) {
        return glGetUniformLocation(this.programID, name);
    }

    public void setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) {
            if (args.length > 2) {
                if (args.length > 3) glUniform4f(loc, args[0], args[1], args[2], args[3]);
                else glUniform3f(loc, args[0], args[1], args[2]);
            } else glUniform2f(loc, args[0], args[1]);
        } else glUniform1f(loc, args[0]);
    }


    public void setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) glUniform2i(loc, args[0], args[1]);
        else glUniform1i(loc, args[0]);
    }

    @Override
    public String toString() {
        return "ShaderProgram{" + "programID=" + programID + ", vertexName='" + vertexName + '\'' + ", fragmentName='" + fragmentName + '\'' + '}';
    }

}