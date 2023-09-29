package dev.eternal.client.render.engine.vertex;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum VertexAttributeFormat {
  POSITION(2, GL_FLOAT, "position"),
  COLOR(4, GL_FLOAT, "color"),
  TEX(2, GL_FLOAT, "tex"),
  ;

  final int size;
  final int type;
  @Setter
  String location;

  VertexAttributeFormat(int size, int type, String location) {
    this.size = size;
    this.type = type;
    this.location = location;
  }

}
