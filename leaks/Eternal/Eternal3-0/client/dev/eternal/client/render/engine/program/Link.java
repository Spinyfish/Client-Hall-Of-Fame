package dev.eternal.client.render.engine.program;

import dev.eternal.client.render.engine.vertex.VertexAttributeFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Link {
  String vertexShaderPath();

  String fragmentShaderPath();

  String geometryShaderPath() default "";

  VertexAttributeFormat[] attributes();

  String[] attributeLocations() default {};

}
