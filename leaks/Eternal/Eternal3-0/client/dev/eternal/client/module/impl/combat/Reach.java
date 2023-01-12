package dev.eternal.client.module.impl.combat;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventReach;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.NumberSetting;

@ModuleInfo(name = "Reach", category = Module.Category.COMBAT, description = "Customisable reach.")
public class Reach extends Module {

  private final NumberSetting reach = new NumberSetting(this, "Reach", "Amout of reach.", 4.2, 3, 6, 0.1);

  @Subscribe
  public void onReach(EventReach eventReach) {
    eventReach.reach(this.reach.value());
  }

}
