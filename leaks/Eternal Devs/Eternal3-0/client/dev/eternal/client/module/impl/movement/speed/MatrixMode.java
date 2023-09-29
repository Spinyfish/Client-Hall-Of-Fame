package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

public class MatrixMode extends Mode {

  public MatrixMode(Module module, String name) {
    super(module, name);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    if (MovementUtil.isMovingOnGround()) {
      eventMove.friction(eventMove.friction() + .2865F);
      mc.thePlayer.motionY = 0.42F;
    } else {
      eventMove.friction(eventMove.friction() + 0.05F);
    }
  }

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    if (eventUpdate.pre())
      mc.thePlayer.onGround = true; // this changes friction and is a shitty monkey-level fix but works, will be replaced ofc.
  }

}
