package dev.eternal.client.render.engine;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VAO {
  private final int id;

  public VAO() {
    id = glGenVertexArrays();
  }

  public void bind() {
    glBindVertexArray(id);
  }

  public void delete() {
    glDeleteVertexArrays(id);
  }

  public int getID() {
    return id;
  }
}