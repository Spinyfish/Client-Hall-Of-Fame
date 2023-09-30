package dev.eternal.client.module.impl.render;

import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import lombok.Getter;

@Getter
@ModuleInfo(name = "Fullbright", description = "Turns the world bright.", category = Module.Category.RENDER, defaultKey = 0)
public class Fullbright extends Module {

  float prevGamma = mc.gameSettings.gammaSetting;

  @Override
  protected void onEnable() {
    prevGamma = mc.gameSettings.gammaSetting;
    mc.gameSettings.gammaSetting = 100000F;
  }

  @Override
  protected void onDisable() {
    mc.gameSettings.gammaSetting = prevGamma;
  }
}
