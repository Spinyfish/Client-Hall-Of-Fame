package dev.eternal.client.render.engine.program;

import dev.eternal.client.Client;
import dev.eternal.client.render.engine.RenderableRenderer;
import dev.eternal.client.render.engine.renderable.RenderableType;
import dev.eternal.client.render.engine.renderable.renderables.RectangleRenderable;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

/**
 * A OpenGL shader program wrapper, with support for geometry shaders
 */
@Getter
public class ShaderProgram {

  private final String vertexName;
  private final String geometryName;
  private final String fragmentName;

  private final int vertexStage;
  private final int geometryStage;
  private final int fragmentStage;

  private int program;

  private long timeStamp;

  @SneakyThrows
  public ShaderProgram(String vertexPath, String fragmentPath, String geometryPath) {
    this.vertexName = vertexPath;
    this.fragmentName = fragmentPath;
    this.geometryName = geometryPath;

    this.vertexStage = compileShader(GL20.GL_VERTEX_SHADER, readFile(vertexPath));
    this.fragmentStage = compileShader(GL20.GL_FRAGMENT_SHADER, readFile(fragmentPath));
    this.geometryStage =
        geometryPath.equals("")
            ? -1
            : compileShader(GL32.GL_GEOMETRY_SHADER, readFile(geometryPath));
    if (this.vertexStage != -1 && this.fragmentStage != -1) {

      this.program = GL20.glCreateProgram();
      GL20.glAttachShader(this.program, this.vertexStage);
      GL20.glAttachShader(this.program, this.fragmentStage);
      if (this.geometryStage != -1) {
        GL20.glAttachShader(this.program, this.geometryStage);
      }
      glBindFragDataLocation(this.program, 0, "fragColor");
      GL20.glLinkProgram(this.program);
      boolean compiled = GL20.glGetProgrami(this.program, GL20.GL_LINK_STATUS) == GL11.GL_TRUE;
      if (!compiled) {
        var log = GL20.glGetProgramInfoLog(this.program, 2048);
        System.out.printf("Failed to compile shader %s: %s%n", this.fragmentName, log);
        return;
      }
      this.timeStamp = System.currentTimeMillis();
    }
  }

  public void delete() {
    GL20.glUseProgram(0);
    GL20.glDeleteProgram(this.program);
    GL20.glDeleteShader(this.vertexStage);
    GL20.glDeleteShader(this.fragmentStage);
    if (this.geometryStage != -1) {
      GL20.glDeleteShader(this.geometryStage);
    }
  }

  public void use() {
    GL20.glUseProgram(this.program);
  }

  public void unUse() {
    GL20.glUseProgram(0);
  }

  /**
   * Please don't change this to immediate mode, minecraft is already making use of VBOs, which save
   * texture data on the gpu directly. TL;DR rendering is faster
   *
   * @param width
   * @param height
   */
  public void quad(float width, float height) {
    RenderableRenderer.of(RenderableType.RECTANGLE).batching().apply(ctx -> ctx.push(
        RectangleRenderable.of(0, 0, width, height, 0xFFFFFFFF))).render();
  }

  private int compileShader(int stage, String source) {
    var stageID = GL20.glCreateShader(stage);
    GL20.glShaderSource(stageID, source);
    GL20.glCompileShader(stageID);
    boolean compiled = GL20.glGetShaderi(stageID, GL20.GL_COMPILE_STATUS) == GL11.GL_TRUE;
    if (!compiled) {
      var log = GL20.glGetShaderInfoLog(stageID, 2048);
      System.out.printf("Failed to compile shader %s: %s%n", this.fragmentName, log);
      return -1;
    }
    return stageID;
  }

  private String readFile(String fileName) throws IOException {
    var builder = new StringBuilder();
    var inputStream =
        getClass().getResourceAsStream(
            String.format("/assets/minecraft/eternal/shader/%s", fileName));
    var reader = new InputStreamReader(inputStream);
    var buffered = new BufferedReader(reader);
    for (var s = ""; (s = buffered.readLine()) != null; builder.append(s).append("\n")) ;
    return builder.toString();
  }

  public int attribute(String name) {
    return GL20.glGetAttribLocation(this.program, name);
  }

  public int uniform(String name) {
    return GL20.glGetUniformLocation(this.program, name);
  }
}
