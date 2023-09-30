package dev.eternal.client.render.engine.renderable.renderables;

import dev.eternal.client.render.engine.program.Link;
import dev.eternal.client.render.engine.renderable.Renderable;
import dev.eternal.client.render.engine.vertex.VertexAttributeFormat;
import dev.eternal.client.render.engine.vertex.VertexBuffer;
import lombok.AllArgsConstructor;

@Link(
    vertexShaderPath = "shapes/rectangle.vsh",
    fragmentShaderPath = "shapes/rectangle.fsh",
    attributes = {
        VertexAttributeFormat.POSITION,
        VertexAttributeFormat.COLOR
    }
)
@AllArgsConstructor(staticName = "of")
public class RectangleRenderable implements Renderable {

  private final float x, y, w, h;
  private final int color;

  @Override
  public int put(VertexBuffer buffer, float width, float height) {
    if (buffer.remaining() < 8 * 6) {
      return 0;
    }
    float x1 = x;
    float x2 = (x + w);
    float y1 = y;
    float y2 = (y + h);

    buffer.position(x1, y1).color(color);
    buffer.position(x1, y2).color(color);
    buffer.position(x2, y2).color(color);

    buffer.position(x1, y1).color(color);
    buffer.position(x2, y1).color(color);
    buffer.position(x2, y2).color(color);

    return 6;
  }
}
