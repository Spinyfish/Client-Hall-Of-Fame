package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventAbsoluteMove;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.math.MathUtil;
import dev.eternal.client.util.movement.MovementUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class OldNCPMode extends Mode {

  private double moveSpeed, lastDist;
  private int stage;

  public OldNCPMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    if (eventUpdate.pre()) this.lastDist = Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX,
        mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
  }

  @Subscribe
  public void handleAbsoluteMove(EventAbsoluteMove eventAbsoluteMove) {
    if (MathUtil.roundToPlace(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtil.roundToPlace(0.138, 3)) {
      mc.thePlayer.motionY -= 0.138;
    }
    double base = MovementUtil.getPotionSpeed(0.2873);
    if (MovementUtil.isMoving()) {
      if (mc.thePlayer.onGround) {
        if (stage == 0) {
          moveSpeed = 0.4 + base;
        } else {
          stage = 0;
          moveSpeed *= 2.149;
          eventAbsoluteMove.y(mc.thePlayer.motionY = 0.42);
        }
      } else {
        if (stage == 1) {
          double hDifference = 0.67 * (lastDist - base);
          moveSpeed = lastDist - hDifference;
        } else {
          moveSpeed = lastDist - lastDist / 160 - 1.0E-4;
        }
      }
    } else stage = 0;
    stage++;
    moveSpeed = Math.max(base, moveSpeed);
    MovementUtil.setMotion(eventAbsoluteMove, moveSpeed);
  }


  @Override
  public void onEnable() {
    stage = 0;
  }
}
