package dev.eternal.client.module.impl.movement;

import dev.eternal.client.Client;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.module.impl.movement.speed.*;
import dev.eternal.client.property.impl.ModeSetting;
import dev.eternal.client.property.impl.NumberSetting;
import lombok.Getter;
import org.lwjgl.input.Keyboard;

@Getter
@ModuleInfo(name = "Speed", description = "Allows you to move faster", category = Module.Category.MOVEMENT, defaultKey = Keyboard.KEY_F)
public class Speed extends Module {

  private final NumberSetting speedSetting = new NumberSetting(this, "Speed", 0.725, 0.2, 10.0, 0.05);
  private final ModeSetting modeSetting = new ModeSetting(this, "Mode",
      new VanillaMode(this, "Vanilla"),
      new WatchdogMode(this, "Watchdog"),
      new MineplexMode(this, "Mineplex"),
      new MorganMode(this, "Morgan"),
      new Buzz1_17Mode(this, "Buzz 1.17+"),
      new AGCMode(this, "AGC"),
      new AACMode(this, "AAC"),
      new PacketMode(this, "Packet"),
      new OldNCPMode(this, "Old NCP"),
      new NCPMode(this, "NCP"),
      new YPortMode(this, "Y-Port"),
      new OnGroundMode(this, "On Ground"),
      new AccelMode(this, "Acceleration"),
      new TestMode(this, "Test"),
      new TubnetMode(this, "Tubnet"),
      new VulcanMode(this, "Vulcan"));

  @Override
  protected void onEnable() {
    Flight flight = Client.singleton().moduleManager().getByClass(Flight.class);
    if (flight.isEnabled())
      flight.toggle();
  }

  @Override
  protected void onDisable() {
    mc.timer.timerSpeed = 1;
  }
}
