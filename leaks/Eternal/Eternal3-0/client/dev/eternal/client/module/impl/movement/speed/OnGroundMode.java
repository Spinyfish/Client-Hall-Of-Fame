package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.impl.movement.Speed;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.world.WorldUtil;

public class OnGroundMode extends Mode {
  public OnGroundMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    Speed speed = getOwner();
    if (MovementUtil.isMovingOnGround())
      MovementUtil.setMotion(speed.speedSetting().value());
  }

}
