package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.module.impl.movement.Speed;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

public class VanillaMode extends Mode {

  private final BooleanSetting hop = new BooleanSetting(this, "Hop", true);

  public VanillaMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    Speed speed = this.getOwner();
    if (MovementUtil.isMovingOnGround() && hop.value()) {
      mc.thePlayer.motionY = 0.42F;
    }
    MovementUtil.setMotion(eventMove, speed.speedSetting().value());
  }


}
