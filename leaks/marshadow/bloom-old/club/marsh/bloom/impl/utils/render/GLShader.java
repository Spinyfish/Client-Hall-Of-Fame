package club.marsh.bloom.impl.utils.render;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class GLShader {

    private int program;

    private final Map<String, Integer> uniformLocationMap = new HashMap<>();

    public GLShader(final String vertexSource, final String fragSource) {
        this.program = glCreateProgram();

        // Attach vertex & fragment shader to the program
        glAttachShader(this.program, createShader(vertexSource, GL_VERTEX_SHADER));
        glAttachShader(this.program, createShader(fragSource, GL_FRAGMENT_SHADER));
        // Link the program
        glLinkProgram(this.program);
        // Check if linkage was a success
        final int status = glGetProgrami(this.program, GL_LINK_STATUS);
        // Check is status is a null ptr
        if (status == 0) {
            // Invalidate if error
            this.program = -1;
            return;
        }

        this.setupUniforms();
    }

    private static int createShader(final String source, final int type) {
        final int shader = glCreateShader(type); // Create new shader of passed type (vertex or fragment)
        glShaderSource(shader, source); // Specify the source (the code of the shader)
        glCompileShader(shader);               // Compile the code

        final int status = glGetShaderi(shader, GL_COMPILE_STATUS); // Check if the compilation succeeded

        if (status == 0) { // Equivalent to checking invalid ptr
            return -1;
        }

        return shader;
    }

    public void use() {
        // Use shader program
        glUseProgram(this.program);
        this.updateUniforms();
    }

    public int getProgram() {
        return program;
    }

    public void setupUniforms() {}

    public void updateUniforms() {}

    public void setupUniform(final String uniform) {
        this.uniformLocationMap.put(uniform, glGetUniformLocation(this.program, uniform));
    }

    public int getUniformLocation(final String uniform) {
        return this.uniformLocationMap.get(uniform);
    }
}