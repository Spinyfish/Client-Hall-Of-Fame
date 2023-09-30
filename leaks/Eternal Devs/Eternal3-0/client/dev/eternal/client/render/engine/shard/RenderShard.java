package dev.eternal.client.render.engine.shard;

import dev.eternal.client.render.engine.program.ShaderProgram;

import java.util.function.Consumer;

public interface RenderShard {

  void preRender();

  void postRender();

  void uniform(Consumer<ShaderProgram> programConsumer);

}
