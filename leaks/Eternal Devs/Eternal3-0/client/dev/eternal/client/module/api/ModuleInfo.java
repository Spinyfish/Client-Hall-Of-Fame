package dev.eternal.client.module.api;

import dev.eternal.client.module.Module;
import org.lwjgl.input.Keyboard;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
  String name();

  String description() default "No Description...";

  Module.Category category();

  int defaultKey() default Keyboard.KEY_NONE;
}