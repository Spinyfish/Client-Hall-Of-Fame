package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.impl.movement.Speed;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

public class AccelMode extends Mode {

  double speed = 0;

  public AccelMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Override
  public void onEnable() {
    speed = 0;
  }

  @Subscribe
  public void onUpdate(EventUpdate eventUpdate) {
    if (!MovementUtil.isMoving())
      speed = 0;
    Speed speed2 = client.moduleManager().getByClass(Speed.class);
    speed += speed2.speedSetting().value() / 100;
    MovementUtil.setMotion(speed > speed2.speedSetting().value() ? speed2.speedSetting().value() : speed);
  }

}
