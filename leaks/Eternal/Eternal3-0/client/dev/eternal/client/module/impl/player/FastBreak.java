package dev.eternal.client.module.impl.player;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.NumberSetting;

@ModuleInfo(name = "FastBreak", category = Module.Category.PLAYER, description = "Breaks blocks fast")
public class FastBreak extends Module {
  private final NumberSetting speed = new NumberSetting(this, "Activation Damage", "When you should speedbreak", 0.7, 0.25, 1, 0.01);
  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    mc.playerController.blockHitDelay = 0;
    if (mc.playerController.curBlockDamageMP >= speed.value()) {
      mc.playerController.curBlockDamageMP = 1.0F;
    }
  }
}
