package dev.eternal.client.render.engine.renderable.renderables;

import dev.eternal.client.render.engine.program.Link;
import dev.eternal.client.render.engine.renderable.Renderable;
import dev.eternal.client.render.engine.vertex.VertexAttributeFormat;
import dev.eternal.client.render.engine.vertex.VertexBuffer;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

@Link(
    vertexShaderPath = "shapes/roundedrectangle.vsh",
    fragmentShaderPath = "shapes/roundedrectangle.fsh",
    attributes = {
        VertexAttributeFormat.POSITION,
        VertexAttributeFormat.POSITION,
        VertexAttributeFormat.COLOR,
        VertexAttributeFormat.POSITION,
        VertexAttributeFormat.POSITION
    },
    attributeLocations = {"position", "size", "color", "radius", "location"})
@AllArgsConstructor(staticName = "of")
public class RoundedRectangleRenderable implements Renderable {

  private float x, y, width, height, radius;
  private final int color;

  @Override
  public int put(VertexBuffer buffer, float screenWidth, float screenHeight) {
    if (buffer.remaining() < 8 * 8) {
      return 0;
    }

    final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
    final int scaleFactor = scaledResolution.getScaleFactor();

    y = scaledResolution.getScaledHeight() - y - height;

    x *= scaleFactor;
    y *= scaleFactor;
    width *= scaleFactor;
    height *= scaleFactor;

    final float x2 = scaledResolution.getScaledWidth();
    final float y2 = scaledResolution.getScaledHeight();

//    final float radius = (float) Math.abs(Math.sin((System.currentTimeMillis() % 50000) / 1500f) * 20);

    buffer.position(0, 0).position(width, height).color(color).position(radius, 0).position(x, y);
    buffer.position(0, y2).position(width, height).color(color).position(radius, 0).position(x, y);
    buffer.position(x2, y2).position(width, height).color(color).position(radius, 0).position(x, y);

    buffer.position(0, 0).position(width, height).color(color).position(radius, 0).position(x, y);
    buffer.position(x2, 0).position(width, height).color(color).position(radius, 0).position(x, y);
    buffer.position(x2, y2).position(width, height).color(color).position(radius, 0).position(x, y);

    return 6;
  }
}
