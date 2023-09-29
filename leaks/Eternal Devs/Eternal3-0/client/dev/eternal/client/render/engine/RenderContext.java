package dev.eternal.client.render.engine;

import dev.eternal.client.render.engine.renderer.Renderer;
import dev.eternal.client.render.engine.renderable.Renderable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenderContext {

  private Renderer renderer;

  public void push(Renderable renderable) {
    renderer.push(renderable);
  }

  public void push(Renderable... renderables) {
    renderer.push(renderables);
  }

}
