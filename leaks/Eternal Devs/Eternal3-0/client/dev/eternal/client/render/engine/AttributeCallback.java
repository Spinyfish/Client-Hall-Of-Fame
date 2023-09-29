package dev.eternal.client.render.engine;

import dev.eternal.client.render.engine.vertex.VertexAttributeFormat;

import java.util.function.Consumer;

public interface AttributeCallback extends Consumer<VertexAttributeFormat[]> {
  @Override
  void accept(VertexAttributeFormat[] vertexAttributeFormat);

}
