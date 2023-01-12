package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventAbsoluteMove;
import dev.eternal.client.module.Module;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

public class MorganMode extends Mode {

  private double speed;
  private int ticks;

  public MorganMode(Module module, String name) {
    super(module, name);
  }

  @Subscribe
  public void handleAbsoluteMove(EventAbsoluteMove eventAbsoluteMove) {
    if (MovementUtil.isMovingOnGround()) {
      eventAbsoluteMove.y(mc.thePlayer.motionY = 0.4F);
      speed = MovementUtil.getPotionSpeed(0.2873) * 2.29;
      ticks = 0;
    } else {
      if (ticks == 1) {
        speed -= 0.1 * (speed - MovementUtil.getBaseGroundSpeed());
      } else {
        speed -= speed / 109;
      }
    }
    ticks++;
    speed = Math.max(MovementUtil.getPotionSpeed(0.2873), speed);
    MovementUtil.setMotion(eventAbsoluteMove, speed);
  }

  @Override
  public void onEnable() {
    ticks = 0;
    speed = mc.thePlayer.onGround ? 0.0 : .46;
  }

  @Override
  public void onDisable() {
    mc.thePlayer.stepHeight = 0.6f;
    mc.timer.timerSpeed = 1;
  }
}
