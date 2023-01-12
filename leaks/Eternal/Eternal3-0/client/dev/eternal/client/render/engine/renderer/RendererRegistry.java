package dev.eternal.client.render.engine.renderer;

import java.util.HashMap;

public class RendererRegistry {

  private final HashMap<RendererType, Renderer> rendererMap = new HashMap<>();

  public void init() {
    rendererMap.put(RendererType.BATCH, new BatchRenderer());
  }

  public Renderer get(RendererType rendererType) {
    return rendererMap.get(rendererType);
  }

}
