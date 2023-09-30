package dev.eternal.client.module.impl.movement;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventJump;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.NumberSetting;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "HighJump", category = Module.Category.MOVEMENT, description = "Allows you to jump higher.", defaultKey = Keyboard.KEY_V)
public class HighJump extends Module {

  private final NumberSetting heightSetting = new NumberSetting(this, "Height", 1, 0.5, 5.0, 0.05);

  @Subscribe
  public void handleJump(EventJump eventJump) {
    eventJump.height(heightSetting.value().floatValue());
  }
}
