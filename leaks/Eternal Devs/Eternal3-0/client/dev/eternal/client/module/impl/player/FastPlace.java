package dev.eternal.client.module.impl.player;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.NumberSetting;

@ModuleInfo(name = "FastPlace", category = Module.Category.PLAYER, description = "Places blocks fast")
public class FastPlace extends Module {
  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    mc.rightClickDelayTimer = 0;
  }
}
