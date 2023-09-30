package dev.eternal.client.render.engine;

import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import lombok.Getter;

@Getter
public class VBO {
  private final int id;

  public VBO() {
    id = glGenBuffers();
  }

  public void bind(int target) {
    glBindBuffer(target, id);
  }

  public void uploadData(int target, FloatBuffer data, int usage) {
    glBufferData(target, data, usage);
  }

  public void uploadData(int target, long size, int usage) {
    glBufferData(target, size, usage);
  }

  public void uploadSubData(int target, long offset, FloatBuffer data) {
    glBufferSubData(target, offset, data);
  }

  public void uploadData(int target, IntBuffer data, int usage) {
    glBufferData(target, data, usage);
  }

  public void delete() {
    glDeleteBuffers(id);
  }

  public int getID() {
    return id;
  }
}
