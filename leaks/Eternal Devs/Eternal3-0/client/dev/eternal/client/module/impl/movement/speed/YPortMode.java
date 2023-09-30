package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventAbsoluteMove;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

public class YPortMode extends Mode {

  private double moveSpeed, lastDist;
  private boolean slowdown;

  public YPortMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Override
  public void onEnable() {
    moveSpeed = lastDist = 0;
  }

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    eventUpdate.groundState(true);
    if (eventUpdate.pre()) this.lastDist = Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX,
        mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
  }

  @Subscribe
  public void handleAbsoluteMove(EventAbsoluteMove eventAbsoluteMove) {
    double base = MovementUtil.getPotionSpeed(0.2873);
    if (MovementUtil.isMoving()) {
      if (mc.thePlayer.onGround) {
        slowdown = true;
        moveSpeed *= 2.14999;
        eventAbsoluteMove.y(mc.thePlayer.motionY = 0.42F);
      } else {
        if (slowdown) {
          eventAbsoluteMove.y(mc.thePlayer.motionY = -1);
          double hDifference = 0.66 * (lastDist - base);
          moveSpeed = lastDist - hDifference;
          slowdown = false;
        } else {
          moveSpeed = lastDist - lastDist / 160 - 1.0E-4;
        }
      }
    } else {
      moveSpeed = lastDist = 0;
    }
    moveSpeed = Math.max(base, moveSpeed);
    MovementUtil.setMotion(eventAbsoluteMove, moveSpeed);
  }


}
