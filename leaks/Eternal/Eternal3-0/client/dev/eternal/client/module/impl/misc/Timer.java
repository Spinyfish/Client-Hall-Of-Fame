package dev.eternal.client.module.impl.misc;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.NumberSetting;

@ModuleInfo(name = "GameSpeed", description = "Changes the games timer speed", category = Module.Category.MISC)
public class Timer extends Module {
  private final NumberSetting timerSpeed = new NumberSetting(this, "Timer Speed", 0.3, 1, 10.0, 1);


  @Subscribe
  public void handleUpdate(EventUpdate event) {
    mc.timer.timerSpeed = timerSpeed.value().floatValue();
  }


  @Override
  protected void onDisable() {
    mc.timer.timerSpeed = 1.0f;
  }
}
