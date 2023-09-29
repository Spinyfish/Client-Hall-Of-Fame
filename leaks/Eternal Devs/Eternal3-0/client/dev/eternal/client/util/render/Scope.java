package dev.eternal.client.util.render;

import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public interface Scope {

  void scope(Runnable f);

  static Scope enable(Integer... capabilities) {
    Set<Integer> caps = Arrays.stream(capabilities)
        .filter(c -> !GL11.glIsEnabled(c))
        .collect(Collectors.toSet());

    return f -> {
      caps.forEach(GL11::glEnable);
      f.run();
      caps.forEach(GL11::glDisable);
    };
  }

  static Scope disable(Integer... capabilities) {
    Set<Integer> caps = Arrays.stream(capabilities)
        .filter(GL11::glIsEnabled)
        .collect(Collectors.toSet());

    return f -> {
      caps.forEach(GL11::glDisable);
      f.run();
      caps.forEach(GL11::glEnable);
    };
  }
}