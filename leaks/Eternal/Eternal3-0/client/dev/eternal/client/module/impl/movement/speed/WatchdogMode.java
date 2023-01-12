package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventTeleport;
import dev.eternal.client.module.impl.exploit.Disabler;
import dev.eternal.client.module.impl.movement.Speed;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.tracker.Tracker;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.movement.data.Motion;
import dev.eternal.client.util.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class WatchdogMode extends Mode {

  private double moveSpeed;
  private boolean decrement;
  private float lastYaw, currentYaw, nextYaw;

  private int stage;

  public WatchdogMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    if (eventMove.strafe() != 0) {
      eventMove.yaw(eventMove.yaw() - eventMove.yaw() % 2.5F);
    }
    var base = MovementUtil.getBaseGroundSpeed();
    if (MovementUtil.isMoving()) {
      if (mc.thePlayer.onGround) {
        moveSpeed = base * 1.7;
        mc.thePlayer.jump();
        decrement = true;
      } else if (decrement) {
        moveSpeed -= 0.66 * (moveSpeed - MovementUtil.getBaseGroundSpeed()); // 0.2 for diff calc moment
        decrement = false;
      } else {
        moveSpeed -= moveSpeed / 150;
      }
    } else {
      moveSpeed = base;
    }
    MovementUtil.setMotionCustomStrafe(eventMove, moveSpeed, 0.5f, true);
  }


  @Subscribe
  public void handleTeleport(EventTeleport eventTeleport) {
    Speed speed = this.getOwner();
    speed.toggle();
  }

  @Override
  public void onEnable() {
    moveSpeed = 0;
    stage = 0;
    currentYaw = mc.thePlayer.rotationYaw;
  }

  public void onDisable() {
    mc.thePlayer.motionY = -0.0784;
  }
}
