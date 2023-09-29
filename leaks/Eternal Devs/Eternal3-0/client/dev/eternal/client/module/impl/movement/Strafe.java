package dev.eternal.client.module.impl.movement;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.util.movement.MovementUtil;

@ModuleInfo(name = "Strafe", description = "jew", category = Module.Category.MOVEMENT)
public class Strafe extends Module {

  @Subscribe
  public void handleJews(EventMove eventMove) {
    if (!MovementUtil.isMoving())
      return;
    float speed = (float) Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    MovementUtil.setMotion(speed);
  }

}
