package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

public class AACMode extends Mode {

  public AACMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    if (MovementUtil.isMovingOnGround()) {
      mc.thePlayer.motionY = 0.42F;
      MovementUtil.setMotion(Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ) + 0.24F);
    } else if (mc.thePlayer.motionY > 0.2F) {
      MovementUtil.setMotion(Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ) + 0.025F);
    }
  }

}