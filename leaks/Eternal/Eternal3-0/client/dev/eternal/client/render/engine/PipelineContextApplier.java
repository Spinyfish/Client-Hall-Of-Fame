package dev.eternal.client.render.engine;

@FunctionalInterface
public interface PipelineContextApplier {

  void apply(RenderContext context);
}
