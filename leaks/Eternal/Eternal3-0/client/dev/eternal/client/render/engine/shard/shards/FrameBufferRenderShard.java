package dev.eternal.client.render.engine.shard.shards;

import dev.eternal.client.render.engine.program.ShaderProgram;
import dev.eternal.client.render.engine.shard.RenderShard;

import java.util.function.Consumer;

import net.minecraft.client.shader.Framebuffer;

public class FrameBufferRenderShard implements RenderShard {

  private final Framebuffer framebuffer;

  public FrameBufferRenderShard(Framebuffer framebuffer) {
    this.framebuffer = framebuffer;
  }

  @Override
  public void preRender() {
    framebuffer.bind(false);
  }

  @Override
  public void postRender() {
    framebuffer.unbind();
  }

  @Override
  public void uniform(Consumer<ShaderProgram> programConsumer) {
    // Not needed
  }
}
