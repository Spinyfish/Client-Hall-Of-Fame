package dev.eternal.client.render.engine.shard.shards;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

import dev.eternal.client.render.engine.program.ShaderProgram;
import dev.eternal.client.render.engine.shard.RenderShard;

import java.util.function.Consumer;

import lombok.Builder;
import net.minecraft.client.shader.Framebuffer;

@Builder
public class ShaderShard implements RenderShard {

  private final ShaderProgram shaderProgram;
  private final Framebuffer in;
  private final Framebuffer out;

  private final boolean unbindAfterDraw;

  public ShaderShard(
      ShaderProgram shaderProgram, Framebuffer in, Framebuffer out, boolean unbindAfterDraw) {
    this.shaderProgram = shaderProgram;
    this.in = in;
    this.out = out;
    this.unbindAfterDraw = unbindAfterDraw;
  }

  public void uniform(Consumer<ShaderProgram> programConsumer) {
    programConsumer.accept(shaderProgram);
  }

  @Override
  public void preRender() {
    glBindTexture(GL_TEXTURE_2D, in.framebufferTexture);
    out.bind();
    shaderProgram.use();
  }

  @Override
  public void postRender() {
    shaderProgram.unUse();
    glBindTexture(GL_TEXTURE_2D, in.framebufferTexture);
    out.bind();
  }
}
