package dev.eternal.client.module.impl.render;

import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;

@ModuleInfo(name = "Xray", description = "Allows you to see blocks through walls.", category = Module.Category.RENDER)
public class Xray extends Module {

  @Override
  protected void onEnable() {
    mc.renderGlobal.loadRenderers();
  }

  @Override
  protected void onDisable() {
    mc.renderGlobal.loadRenderers();
  }
}