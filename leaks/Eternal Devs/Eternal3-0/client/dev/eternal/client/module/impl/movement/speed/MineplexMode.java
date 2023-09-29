package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class MineplexMode extends Mode {

  private double moveSpeed;

  public MineplexMode(IToggleable owner, String name) {
    super(owner, name);
  }


  @Subscribe
  public void handleMove(EventMove eventMove) {
    if (MovementUtil.isMoving()) {
      if (mc.thePlayer.onGround) {
        mc.thePlayer.motionY = 0.42F;
        moveSpeed = 0.6125; // max from AAC v1.9.10.
      } else {
        // prevent flag by silently switching horizontal distance, bypasses timer as pos diff is < 0.0626
        if (moveSpeed > 0.493)
          PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        // 159 is ncp max, however mineplex speed checks are heavily modified AAC v1.9.10 speed checks
        // 103.5 is pre-AAC 3 speed checks friction.
        moveSpeed -= moveSpeed / 103.5;
      }
      MovementUtil.setMotion(eventMove, moveSpeed);
    }
  }

  @Override
  public void onDisable() {
    mc.timer.timerSpeed = 1.0f;
    moveSpeed = 0;
  }
}
