package dev.eternal.client.module.impl.render;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventPostRenderGui;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;

@ModuleInfo(name = "Radar", description = "Renders a list of near-by players.", category = Module.Category.RENDER)
public class Radar extends Module {

  @Subscribe
  public void handleRender2D(EventPostRenderGui eventPostRenderGui) {

  }

}
