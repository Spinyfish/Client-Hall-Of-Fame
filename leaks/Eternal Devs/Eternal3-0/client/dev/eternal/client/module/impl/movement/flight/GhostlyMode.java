package dev.eternal.client.module.impl.movement.flight;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.module.Module;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

public class GhostlyMode extends Mode {

  private double speed;
  private boolean accelerate;

  public GhostlyMode(Module module, String name) {
    super(module, name);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    mc.thePlayer.cameraYaw = 0.1F;
    mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 2.72F : mc.thePlayer.onGround ? 0.42 : (mc.thePlayer.ticksExisted % 3 != 0 ? 0.02 : -0.04);
    MovementUtil.setMotion(eventMove, mc.thePlayer.ticksExisted % 2 == 0 ? 2.2 : 2.2 * .91);
  }

  @Override
  public void onEnable() {
    if (mc.thePlayer != null)
      speed = MovementUtil.getBaseGroundSpeed() * 2.149;
  }
}
