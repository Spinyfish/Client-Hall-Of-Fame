package dev.eternal.client.render.engine.program;

import lombok.Getter;

@Getter
public class LinkedProgram {
  private final Link link;
  private final ShaderProgram program;

  public LinkedProgram(Link link, ShaderProgram program) {
    this.link = link;
    this.program = program;
  }

}
