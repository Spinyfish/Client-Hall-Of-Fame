package dev.eternal.client.render.engine.vertex;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import lombok.Getter;
import org.lwjgl.BufferUtils;

public class VertexBuffer {

  @Getter
  private final FloatBuffer buffer = BufferUtils.createFloatBuffer(4096);

  public void flip() {
    this.buffer.flip();
  }

  public void clear() {
    this.buffer.clear();
  }

  public int remaining() {
    return this.buffer.remaining();
  }

  public int capacity() {
    return this.buffer.capacity();
  }

  public VertexBuffer position(float x, float y) {
    put(x).put(y);
    return this;
  }

  public VertexBuffer tex(float w, float h) {
    put(w).put(h);
    return this;
  }

  public VertexBuffer color(int color) {
    float red = ((color >> 16) & 0xFF) / 255f;
    float green = ((color >> 8) & 0xFF) / 255f;
    float blue = ((color) & 0xFF) / 255f;
    float alpha = (((color >> 24) & 0xFF) / 255f);
    put(red).put(green).put(blue).put(alpha);
    return this;
  }

  public VertexBuffer put(float f) {
    this.buffer.put(f);
    return this;
  }

  public VertexBuffer position(float x, float y, float z, float w) {
    put(x).put(y).put(z).put(w);
    return this;
  }

  public VertexBuffer position(float x, float y, float z) {
    put(x).put(y).put(z);
    return this;
  }

  public VertexBuffer color(float r, float g, float b, float a) {
    put(r).put(g).put(b).put(a);
    return this;
  }

}
