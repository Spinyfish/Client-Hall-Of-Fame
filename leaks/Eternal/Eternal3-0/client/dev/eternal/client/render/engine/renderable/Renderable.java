package dev.eternal.client.render.engine.renderable;

import dev.eternal.client.render.engine.vertex.VertexBuffer;

public interface Renderable {
  int put(VertexBuffer buffer, float width, float height);
}
