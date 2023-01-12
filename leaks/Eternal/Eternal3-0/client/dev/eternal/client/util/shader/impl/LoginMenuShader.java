package dev.eternal.client.util.shader.impl;

import dev.eternal.client.util.shader.Shader;
import net.minecraft.client.Minecraft;

public class LoginMenuShader {

  private final Minecraft mc = Minecraft.getMinecraft();
  private final Shader shader = new Shader("menu/login.glsl");

  public void useShader() {
    shader.useShader();
    shader.setUniform1f("time", (System.currentTimeMillis() % 500000) / 250f);
    shader.setUniform2f("resolution", mc.displayWidth, mc.displayHeight);
  }

  public void stopShader() {
    shader.stopShader();
  }

}
