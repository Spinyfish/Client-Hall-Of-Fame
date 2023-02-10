package dev.myth.api.utils.render.shader.list;

import dev.myth.api.utils.render.shader.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;


/**
 *  Auxys Testing Shaders
 *  Remove = Gay
 *  Im Turmswinkel 1
 *  und nein ich werde nicht felix sachen benutzen
 * */
public class CoolShader {


    public static CoolShader INSTANCE;
    public CoolShader() {
        INSTANCE = this;
    }

    private int program;
    private int vertexShader;
    private int fragmentShader;
    private int timeLocation;
    private int resolutionLocation;

    public void create() {
        // Load the vertex shader
        vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader,
                "#version 330\n" +
                        "in vec2 position;\n" +
                        "void main() {\n" +
                        "    gl_Position = vec4(position, 0.0, 1.0);\n" +
                        "}");
        GL20.glCompileShader(vertexShader);

        // Load the fragment shader
        fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader,
                "#version 330\n" +
                        "uniform float time;\n" +
                        "uniform vec2 resolution;\n" +
                        "out vec4 outColor;\n" +
                        "void main() {\n" +
                        "    vec2 p = (gl_FragCoord.xy * 2.0 - resolution) / min(resolution.x, resolution.y);\n" +
                        "    float d = length(p);\n" +
                        "    float f = 0.5 + 0.5 * sin(time + 6.2831 * d);\n" +
                        "    outColor = vec4(0.2, 0.5, 1.0, f);\n" +
                        "}");
        GL20.glCompileShader(fragmentShader);

        // Create the program
        program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vertexShader);
        GL20.glAttachShader(program, fragmentShader);
        GL20.glLinkProgram(program);

        // Get the locations of the uniforms
        timeLocation = GL20.glGetUniformLocation(program, "time");
        resolutionLocation = GL20.glGetUniformLocation(program, "resolution");
    }

    public void bind() {
        GL20.glUseProgram(program);
    }

    public void setTime(float time) {
        GL20.glUniform1f(timeLocation, time);
    }

    public void setResolution(int width, int height) {
        GL20.glUniform2f(resolutionLocation, width, height);
    }


}
