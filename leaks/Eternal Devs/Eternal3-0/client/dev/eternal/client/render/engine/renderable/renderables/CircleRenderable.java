package dev.eternal.client.render.engine.renderable.renderables;

import dev.eternal.client.render.engine.program.Link;
import dev.eternal.client.render.engine.renderable.Renderable;
import dev.eternal.client.render.engine.vertex.VertexAttributeFormat;
import dev.eternal.client.render.engine.vertex.VertexBuffer;
import lombok.AllArgsConstructor;
import net.minecraft.util.MathHelper;

@Link(
    vertexShaderPath = "shapes/circle.vsh",
    fragmentShaderPath = "shapes/circle.fsh",
    attributes = {
        VertexAttributeFormat.POSITION,
        VertexAttributeFormat.COLOR
    }
)
@AllArgsConstructor(staticName = "of")
public class CircleRenderable implements Renderable {

  private final float x, y, radius;

  @Override
  public int put(VertexBuffer buffer, float width, float height) {

    for (int i = 0; i < 360; i++) {
      buffer.position(x + MathHelper.sin(i) * radius, y - MathHelper.cos(i) * radius);
    }

    return 360;
  }
}
