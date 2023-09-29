package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

public class TubnetMode extends Mode {
  public TubnetMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    if (MovementUtil.isMovingOnGround()) {
      mc.thePlayer.jump();
    } else if (mc.thePlayer.motionY >= .32) {
      eventMove.friction(3.7f);
    }
  }

}
