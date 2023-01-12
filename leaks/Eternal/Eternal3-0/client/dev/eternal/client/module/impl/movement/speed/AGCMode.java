package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventAbsoluteMove;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

/**
 * TODO bypass possible strafe checks. (speed will ban if you strafe too much)
 */
public class AGCMode extends Mode {

  private double speed;

  public AGCMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleAbsoluteMove(EventAbsoluteMove eventAbsoluteMove) {
    if (MovementUtil.isMovingOnGround()) {
      eventAbsoluteMove.y(0.42F);
      speed = MovementUtil.getBaseGroundSpeed() + 0.625D;
    } else {
      speed *= 0.901;
    }
    MovementUtil.setMotion(eventAbsoluteMove, speed);
  }


}
