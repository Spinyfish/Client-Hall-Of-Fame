package dev.eternal.client.util.shader.impl;

import dev.eternal.client.util.shader.Shader;
import lombok.Getter;
import net.minecraft.client.Minecraft;

public class RainbowShader {

  private final Minecraft mc = Minecraft.getMinecraft();
  @Getter
  private final Shader shader = new Shader("impl/rainbow.glsl");

  public void useShader() {
    shader.useShader();
    shader.setUniform1f("time", (System.currentTimeMillis() % 500000) / 5000f);
    shader.setUniform2f("size", mc.displayWidth, mc.displayHeight);
    shader.setUniform1i("texture", 0);
  }

  public void stopShader() {
    shader.stopShader();
  }

}
