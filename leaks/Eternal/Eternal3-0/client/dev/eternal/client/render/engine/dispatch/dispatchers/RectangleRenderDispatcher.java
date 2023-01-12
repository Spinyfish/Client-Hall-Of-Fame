package dev.eternal.client.render.engine.dispatch.dispatchers;

import dev.eternal.client.render.engine.RenderContext;
import dev.eternal.client.render.engine.dispatch.DispatcherMetaData;
import dev.eternal.client.render.engine.dispatch.DispatcherMode;
import dev.eternal.client.render.engine.dispatch.RenderDispatcher;
import dev.eternal.client.render.engine.program.LinkedProgram;
import dev.eternal.client.render.engine.program.ProgramLinker;
import dev.eternal.client.render.engine.program.ShaderProgram;
import dev.eternal.client.render.engine.renderable.renderables.RectangleRenderable;

import java.util.function.Consumer;
import java.util.function.Function;

import lombok.Getter;

public class RectangleRenderDispatcher extends RenderDispatcher {

  @Getter
  private final Consumer<ShaderProgram> uniformCallback = shaderProgram -> {

  };

  @Getter
  private final DispatcherMetaData metaData = DispatcherMetaData.builder().attributes(true)
      .renderMode(
          DispatcherMode.TRIANGLE_STRIP).allowed(RectangleRenderable.class).build();

  @Override
  protected Function<ProgramLinker, LinkedProgram> internalInit(RenderContext context) {
    this.initBuffers(4096 * 16);
    return programLinker -> programLinker.linkProgram(RectangleRenderable.class);
  }
}
