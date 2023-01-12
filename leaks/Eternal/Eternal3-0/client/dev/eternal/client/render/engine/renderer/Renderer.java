package dev.eternal.client.render.engine.renderer;

import dev.eternal.client.render.engine.RenderContext;
import dev.eternal.client.render.engine.renderable.Renderable;
import dev.eternal.client.render.engine.renderable.RenderableType;

public interface Renderer {

  void begin(RenderableType renderable);

  void push(Renderable... renderables);

  void render(RenderContext context);

}
