package dev.eternal.client.module.impl.movement.flight;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.impl.movement.Flight;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

public class HousingMode extends Mode {

  private final NumberSetting numberSetting =
      new NumberSetting(this, "Acceleration", "Acceleration", 1, 0.1, 10, 0.1);
  private double current = 0;

  public HousingMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Override
  public void onEnable() {
    current = 0;
  }

  @Override
  public void onDisable() {
    current = 0;
  }

  @Subscribe
  public void onUpdate(EventUpdate eventUpdate) {
    Flight flight = this.getOwner();
    if (current > flight.speedSetting().value())
      current = flight.speedSetting().value();
    current += numberSetting.value() / (flight.speedSetting().value() * 10);
    if (!MovementUtil.isMoving()) current = 0;
    mc.thePlayer.motionY =
        mc.gameSettings.keyBindSneak.isKeyDown()
            ? mc.gameSettings.keyBindJump.isKeyDown() ? 0 : -current
            : mc.gameSettings.keyBindJump.isKeyDown() ? current : 0;
    MovementUtil.setMotion(current);
  }
}
