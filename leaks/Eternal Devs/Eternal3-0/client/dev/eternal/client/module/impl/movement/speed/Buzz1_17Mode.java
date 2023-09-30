package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventAbsoluteMove;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;


public class Buzz1_17Mode extends Mode {

  public Buzz1_17Mode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleAbsoluteMove(EventAbsoluteMove eventAbsoluteMove) {
  }

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    if (!eventUpdate.pre() && MovementUtil.isMovingOnGround()) {
      MovementUtil.setMotionPacket(0.26, (int) (5 + (MovementUtil.getPotionSpeed(2))), 0);
    }
  }


}
