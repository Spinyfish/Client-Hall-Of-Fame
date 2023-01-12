package dev.eternal.client.render.engine.dispatch;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_QUAD_STRIP;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DispatcherMode {
  POINTS(GL_POINTS),
  LINES(GL_LINES),
  LINE_LOOP(GL_LINE_LOOP),
  LINE_STRIP(GL_LINE_STRIP),
  TRIANGLES(GL_TRIANGLES),
  TRIANGLE_STRIP(GL_TRIANGLE_STRIP),
  TRAINGLE_FAN(GL_TRIANGLE_FAN),

  // Deprecated in lwjgl >3.2.2
  QUADS(GL_QUADS),
  QUAD_STRIP(GL_QUAD_STRIP),
  POLYGON(GL_POLYGON);
  private final int mode;
}
