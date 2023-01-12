package dev.eternal.client.render.engine.program;

import dev.eternal.client.render.engine.renderable.Renderable;

import java.util.HashMap;
import java.util.Map;

public class ProgramLinker {

  private final Map<Class<? extends Renderable>, LinkedProgram> programMap = new HashMap<>();

  public ProgramLinker() {

  }

  public LinkedProgram linkProgram(Class<? extends Renderable> renderable) {
    // First check cached programs
    if (this.programMap.containsKey(renderable)) {
      return this.programMap.get(renderable);
    }

    if (!renderable.isAnnotationPresent(Link.class)) {
      throw new IllegalStateException(
          "Missing Link annotation on RenderTask: " + renderable.getSimpleName());
    }
    Link link = renderable.getAnnotation(Link.class);

    var vertexPath = link.vertexShaderPath();
    var fragmentPath = link.fragmentShaderPath();
    var geometryPath = link.geometryShaderPath();
    var program = new ShaderProgram(vertexPath, fragmentPath, geometryPath);
    program.use();
    LinkedProgram linkedProgram = new LinkedProgram(link, program);
    this.programMap.putIfAbsent(renderable, linkedProgram);
    return linkedProgram;
  }

}
