package dev.eternal.client.render.engine.dispatch;

import dev.eternal.client.render.engine.RenderContext;
import dev.eternal.client.render.engine.dispatch.dispatchers.RectangleRenderDispatcher;
import dev.eternal.client.render.engine.dispatch.dispatchers.RoundedRectangleRenderDispatcher;
import dev.eternal.client.render.engine.renderable.RenderableType;

import java.util.HashMap;

public class DispatcherRegistry {
  private final HashMap<RenderableType, RenderDispatcher> rendererMap = new HashMap<>();

  public void init(RenderContext context) {
    rendererMap.put(RenderableType.RECTANGLE, new RectangleRenderDispatcher());
    rendererMap.put(RenderableType.ROUNDED_RECT, new RoundedRectangleRenderDispatcher());

    rendererMap.values().forEach(renderDispatcher -> renderDispatcher.init(context));
  }

  public RenderDispatcher get(RenderableType type) {
    return rendererMap.get(type);
  }

}
