package dev.eternal.client.module.impl.combat;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import net.minecraft.entity.Entity;

@ModuleInfo(name = "AntiBot", description = "Marks entities as bots.", category = Module.Category.COMBAT)
public class AntiBot extends Module {
  @Subscribe
  public void onUpdate(EventUpdate eventUpdate) {
    for (Entity entity : mc.theWorld.loadedEntityList) {
      if (entity != mc.thePlayer) {
        if (!entity.getName().replaceAll("[a-zA-Z0-9_]", "").isEmpty()) {
          entity.isBot = true;
        }
      }
    }
  }
}
