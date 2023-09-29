package dev.eternal.client.module.impl.movement;

import dev.eternal.client.Client;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.module.impl.movement.flight.*;
import dev.eternal.client.property.impl.ModeSetting;
import dev.eternal.client.property.impl.NumberSetting;
import lombok.Getter;
import org.lwjgl.input.Keyboard;

@Getter
@SuppressWarnings("deprecated")
@ModuleInfo(name = "Flight", description = "Allows you to fly", category = Module.Category.MOVEMENT, defaultKey = Keyboard.KEY_G)
public class Flight extends Module {
  private final NumberSetting speedSetting = new NumberSetting(this, "Speed", 2.725, 0.2, 10.0, 0.05);
  private final ModeSetting modeSetting = new ModeSetting(this, "Mode",
      new GhostlyMode(this, "Ghostly"),
      new BufferAbuseMode(this, "Buffer Abuse"),
      new VanillaMode(this, "Vanilla"),
      new MorganMode(this, "Morgan"),
      new CollideMode(this, "Collision"),
      new TeleportMode(this, "Teleport"),
      new DashMode(this, "Dash"),
      new HypixelMode(this, "Hypixel"),
      new TestMode(this, "Test"),
      new HousingMode(this, "Housing"));

  @Override
  protected void onEnable() {
    Speed speed = Client.singleton().moduleManager().getByClass(Speed.class);
    if (speed.isEnabled())
      speed.toggle();
  }
}
