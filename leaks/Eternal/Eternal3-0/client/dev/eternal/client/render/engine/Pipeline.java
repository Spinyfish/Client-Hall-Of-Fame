package dev.eternal.client.render.engine;

import dev.eternal.client.render.engine.dispatch.DispatcherRegistry;
import dev.eternal.client.render.engine.renderer.RendererRegistry;
import dev.eternal.client.render.engine.renderer.RendererType;
import dev.eternal.client.render.engine.renderer.Renderer;
import dev.eternal.client.render.engine.renderable.Renderable;
import dev.eternal.client.render.engine.renderable.RenderableType;
import lombok.Getter;
import net.minecraft.client.Minecraft;

@Getter
public class Pipeline {

  private final RenderContext renderContext = new RenderContext();
  private final RendererRegistry rendererRegistry = new RendererRegistry();
  private final DispatcherRegistry dispatcherRegistry = new DispatcherRegistry();

  private Renderer renderer;
  private boolean drawing;
  private float alpha = 1.0F;
  private float scaleX, scaleY, scaleZ;
  private float transX, transY, transZ;

  public void init() {
    rendererRegistry.init();
    dispatcherRegistry.init(renderContext);
  }

  public RenderContext begin(RendererType renderType, RenderableType renderableType) {
    drawing = true;
    renderer = rendererRegistry.get(renderType);
    renderer.begin(renderableType);
    renderContext.renderer(renderer);
    return renderContext;
  }

  public void push(Renderable renderable) {
    if (!drawing || renderer == null) {
      return;
    }
    renderer.push(renderable);
  }

  public void render() {
    if (!drawing || renderer == null) {
      return;
    }
    Minecraft.getMinecraft().mcProfiler.startSection("batchRenderer");
    renderer.render(renderContext);
    Minecraft.getMinecraft().mcProfiler.endSection();
    drawing = false;
    renderer = null;
  }

  public void scale(float x, float y, float z) {
    this.scaleX = x;
    this.scaleY = y;
    this.scaleZ = z;

  }

}
