package dev.eternal.client.util.render;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * Creates an OpenGL shader instance from a given fragment shader, and an optional vertex shader
 * <br>
 *
 * @author Cedo, Hazsi
 */
@Getter
public class ShaderUtil {

  private final int programID;

  public ShaderUtil(final String fragmentShaderLoc, final String vertexShaderLoc) {
    final int program = GL20.glCreateProgram();
    int fragShaderID;
    int vertexShaderID;

    try {
      fragShaderID = createShader(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(fragmentShaderLoc)).getInputStream(), GL_FRAGMENT_SHADER);
      glAttachShader(program, fragShaderID);
      vertexShaderID = createShader(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(vertexShaderLoc)).getInputStream(), GL_VERTEX_SHADER);
      glAttachShader(program, vertexShaderID);
    } catch (Exception e) {
      e.printStackTrace();
    }

    glLinkProgram(program);
    final int status = glGetProgrami(program, GL_LINK_STATUS);

    if (GL_LINK_STATUS == 0) {
      throw new IllegalStateException("Shader failed to link!");
    }

    this.programID = program;
  }

  public ShaderUtil(final String fragmentShaderLoc) {
    this(fragmentShaderLoc, "eternal/shader/vertex.vsh");
  }

  public void init() {
    glUseProgram(programID);
  }

  public void unload() {
    glUseProgram(0);
  }

  public static void drawQuads(final ScaledResolution sr) {
    if (Minecraft.getMinecraft().gameSettings.ofFastRender) return;

    final float width = (float) sr.getScaledWidth_double();
    final float height = (float) sr.getScaledHeight_double();

    glBegin(GL_QUADS);
    glTexCoord2f(0, 1);
    glVertex2f(0, 0);
    glTexCoord2f(0, 0);
    glVertex2f(0, height);
    glTexCoord2f(1, 0);
    glVertex2f(width, height);
    glTexCoord2f(1, 1);
    glVertex2f(width, 0);
    glEnd();
  }

  public static void drawQuads(float x, float y, float width, float height) {
    if (Minecraft.getMinecraft().gameSettings.ofFastRender) return;

    glBegin(GL_QUADS);
    glTexCoord2f(0, 1);
    glVertex2f(0, 0);
    glTexCoord2f(0, 0);
    glVertex2f(0, height);
    glTexCoord2f(1, 0);
    glVertex2f(width, height);
    glTexCoord2f(1, 1);
    glVertex2f(width, 0);
    glEnd();
  }

  private int createShader(final InputStream inputStream, final int shaderType) {
    final int shader = glCreateShader(shaderType);
    glShaderSource(shader, readShader(inputStream));
    glCompileShader(shader);

    final int state = glGetShaderi(shader, GL_COMPILE_STATUS);

    if (state == 0) {
      throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
    }
    return shader;
  }

  public void setUniformf(final String name, final float... args) {
    final int loc = glGetUniformLocation(programID, name);
    if (args.length > 1) glUniform2f(loc, args[0], args[1]);
    else glUniform1f(loc, args[0]);
  }

  public int getUniform(final String name) {
    return glGetUniformLocation(programID, name);
  }

  public void setUniformi(final String name, final int... args) {
    final int loc = glGetUniformLocation(programID, name);
    if (args.length > 1) glUniform2i(loc, args[0], args[1]);
    else glUniform1i(loc, args[0]);
  }

  private String readShader(final InputStream inputStream) {
    final StringBuilder stringBuilder = new StringBuilder();

    try {
      final InputStreamReader inputReader = new InputStreamReader(inputStream);
      final BufferedReader bufferedReader = new BufferedReader(inputReader);

      String line;
      while ((line = bufferedReader.readLine()) != null)
        stringBuilder.append(line).append('\n');
    } catch (final IOException e) {
      e.printStackTrace();
    }

    return stringBuilder.toString();
  }
}
