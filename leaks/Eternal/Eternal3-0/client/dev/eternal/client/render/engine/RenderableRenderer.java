package dev.eternal.client.render.engine;

import dev.eternal.client.Client;
import dev.eternal.client.render.engine.renderer.RendererType;
import dev.eternal.client.render.engine.renderable.RenderableType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenderableRenderer {

  private static final Pipeline PIPELINE = Client.singleton().renderPipeline();

  private RenderContext context;

  private RenderableType renderableType;

  public static RenderableRenderer of(RenderableType type) {
    RenderableRenderer builder = new RenderableRenderer();
    builder.renderableType(type);
    return builder;
  }

  public RenderableRenderer batching() {
    this.context = PIPELINE.begin(RendererType.BATCH, renderableType);
    return this;
  }

  public RenderableRenderer apply(PipelineContextApplier applier) {
    applier.apply(context);
    return this;
  }

  public void render() {
    PIPELINE.render();
  }

}
