package dev.eternal.client.module.impl.movement;

import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.module.impl.movement.longjump.AACMode;
import dev.eternal.client.module.impl.movement.longjump.WatchdogMode;
import dev.eternal.client.property.impl.ModeSetting;
import dev.eternal.client.property.impl.NumberSetting;
import lombok.Getter;
import org.lwjgl.input.Keyboard;

@Getter
@ModuleInfo(name = "LongJump", description = "Allows you to jump further.", category = Module.Category.MOVEMENT, defaultKey = Keyboard.KEY_Z)
public class LongJump extends Module {

  private final NumberSetting speedSetting = new NumberSetting(this, "Speed", 0.825, 0.2, 5.0, 0.05);
  private final ModeSetting modeSetting = new ModeSetting(this, "Mode",
      new AACMode(this, "AAC"),
      new WatchdogMode(this, "Watchdog"));

  @Override
  protected void onDisable() {
    mc.timer.timerSpeed = 1;
  }
}
