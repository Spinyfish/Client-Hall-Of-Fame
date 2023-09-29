package dev.eternal.client.render.engine.renderer;

import dev.eternal.client.Client;
import dev.eternal.client.render.engine.RenderContext;
import dev.eternal.client.render.engine.dispatch.DispatcherMode;
import dev.eternal.client.render.engine.dispatch.RenderDispatcher;
import dev.eternal.client.render.engine.renderable.Renderable;
import dev.eternal.client.render.engine.renderable.RenderableType;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class BatchRenderer implements Renderer {

  private final List<Renderable> renderables = new ArrayList<>();

  private RenderDispatcher renderer;
  private int dirtyVertices;
  private DispatcherMode mode;

  @Override
  public void begin(RenderableType type) {
    var resolution = new ScaledResolution(Minecraft.getMinecraft());
    renderer = Client.singleton().renderPipeline().dispatcherRegistry().get(type);
    renderer.begin((float) resolution.getScaledWidth_double(),
        (float) resolution.getScaledHeight_double());
    renderer.program().program().use();
  }

  @Override
  public void push(Renderable... renderables) {
    for (Renderable renderable : renderables) {
      var clazz = renderable.getClass();
      if (clazz != renderer.metaData().allowed()) {
        throw new IllegalStateException(
            "The type " + clazz + " cannot be used for the renderer " + renderer.metaData()
                .allowed());
      }
      this.renderables.add(renderable);
    }
  }

  @Override
  public void render(RenderContext context) {
    var buffer = this.renderer.buffer();
    int totalVertices = 0;
    for (Renderable renderable : this.renderables) {
      totalVertices += renderable.put(buffer, this.renderer.width(), this.renderer.height());
    }
    var renderMode = this.mode != null ? this.mode : this.renderer.metaData().renderMode();
    if (renderables.size() > 0 || dirtyVertices > 0) {
      if (renderer.metaData().attributes()) {
        renderer.dispatchRenderer(renderMode.mode(), totalVertices + dirtyVertices,
            () -> renderer.applyUniforms(renderer.program().program()));
      }
    }
    this.renderer.program().program().unUse();
    renderables.clear();
    buffer.clear();
    dirtyVertices = 0;
    mode = null;
  }
}
