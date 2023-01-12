package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

public class NCPMode extends Mode {

  private boolean decrement;
  private double speed;

  public NCPMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    var base = MovementUtil.getUpdatedNCPBaseSpeed();
    if (MovementUtil.isMoving()) {
      if (mc.thePlayer.onGround) {
        speed = base * 1.7;
        mc.thePlayer.jump();
        decrement = true;
      } else if (decrement) {
        speed -= 0.66 * (speed - MovementUtil.getBaseGroundSpeed()); // 0.2 for diff calc moment
        decrement = false;
      } else {
        speed -= speed / 150;
      }
      MovementUtil.setMotion(eventMove, speed);
    } else {
      speed = base;
    }
  }

}
