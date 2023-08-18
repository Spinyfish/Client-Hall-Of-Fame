/* November.lol © 2023 */
package lol.november.utility.render.shader;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class Shader {

  /**
   * A default program ID for no shader specified
   */
  private static final int NO_SHADER = 0;

  private final int programId;
  private final Map<String, Integer> uniforms = new HashMap<>();

  /**
   * Compiles, attaches, and links a shader program
   *
   * @param vert     the location of the vertex shader
   * @param frag     the location of the fragment shader
   * @param consumer a consumer with the local {@link Shader} object to create uniforms
   */
  public Shader(String vert, String frag, Consumer<Shader> consumer) {
    int program = glCreateProgram();

    int fragShader = compileShader(frag, GL_FRAGMENT_SHADER);
    if (fragShader == NO_SHADER) throw new RuntimeException(
      "Failed to compile fragment shader"
    );
    glAttachShader(program, fragShader);

    int vertShader = compileShader(vert, GL_VERTEX_SHADER);
    if (vertShader == NO_SHADER) throw new RuntimeException(
      "Failed to compile vertex shader"
    );
    glAttachShader(program, vertShader);

    glLinkProgram(program);

    if (glGetShaderi(program, GL_LINK_STATUS) == GL_FALSE) {
      String log = glGetProgramInfoLog(
        program,
        glGetShaderi(program, GL_INFO_LOG_LENGTH)
      );
      throw new RuntimeException("Failed to link shader: " + log);
    }

    programId = program;
    if (consumer != null) createUniforms(consumer);
  }

  /**
   * Creates and caches uniforms supplied by the consumer
   *
   * @param consumer a runnable containing the local {@link Shader} object
   */
  public void createUniforms(Consumer<Shader> consumer) {
    use();
    consumer.accept(this);
    stop();
  }

  /**
   * Uses this shader
   *
   * @see org.lwjgl.opengl.GL20#glUseProgram(int)
   */
  public void use() {
    if (!glIsProgram(programId)) throw new RuntimeException(
      programId + " has been deleted"
    );

    glUseProgram(programId);
  }

  /**
   * Uses this shader
   *
   * @param consumer the consumer
   * @see org.lwjgl.opengl.GL20#glUseProgram(int)
   */
  public void use(Consumer<Integer> consumer) {
    use();
    consumer.accept(programId);
  }

  public int getLocation(String name) {
    return glGetUniformLocation(programId, name);
  }

  /**
   * Stops using this shader
   *
   * @see org.lwjgl.opengl.GL20#glUseProgram(int)
   */
  public void stop() {
    glUseProgram(NO_SHADER);
  }

  /**
   * Creates and caches a uniform
   *
   * @param name the uniform name
   */
  public void createUniform(String name) {
    if (uniforms.containsKey(name)) return;
    int location = glGetUniformLocation(programId, name);
    uniforms.put(name, location);
  }

  /**
   * Sets a uniform value
   *
   * @param name   the uniform name
   * @param values the value(s) to pipe into the uniform
   */
  public void set(String name, int... values) {
    int location = uniforms.getOrDefault(name, -1);
    if (location == -1) return;

    switch (values.length) {
      case 1 -> glUniform1i(location, values[0]);
      case 2 -> glUniform2i(location, values[0], values[1]);
      case 3 -> glUniform3i(location, values[0], values[1], values[2]);
      case 4 -> glUniform4i(
        location,
        values[0],
        values[1],
        values[2],
        values[3]
      );
    }
  }

  /**
   * Sets a uniform value
   *
   * @param name   the uniform name
   * @param values the value(s) to pipe into the uniform
   */
  public void set(String name, float... values) {
    int location = uniforms.getOrDefault(name, -1);
    if (location == -1) return;

    switch (values.length) {
      case 1 -> glUniform1f(location, values[0]);
      case 2 -> glUniform2f(location, values[0], values[1]);
      case 3 -> glUniform3f(location, values[0], values[1], values[2]);
      case 4 -> glUniform4f(
        location,
        values[0],
        values[1],
        values[2],
        values[3]
      );
    }
  }

  /**
   * Compiles a shader
   *
   * @param location   the shader location
   * @param shaderType the shader type
   * @return the resulting shader ID or 0
   */
  private int compileShader(String location, int shaderType) {
    // read shader
    StringBuilder builder = new StringBuilder();

    try {
      InputStream is = Shader.class.getResourceAsStream(location);
      if (is == null) return NO_SHADER;

      int i;
      while ((i = is.read()) != -1) {
        builder.append((char) i);
      }

      is.close();
    } catch (IOException e) {
      e.printStackTrace();
      return 0;
    }

    String content = builder.toString();
    if (content.isEmpty()) return NO_SHADER;

    int shader = glCreateShader(shaderType);
    glShaderSource(shader, content);
    glCompileShader(shader);

    if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
      String log = glGetProgramInfoLog(
        shader,
        glGetShaderi(shader, GL_INFO_LOG_LENGTH)
      );
      throw new RuntimeException(
        "Failed to compile shader with type " + shaderType + " - " + log
      );
    }

    return shader;
  }
}
