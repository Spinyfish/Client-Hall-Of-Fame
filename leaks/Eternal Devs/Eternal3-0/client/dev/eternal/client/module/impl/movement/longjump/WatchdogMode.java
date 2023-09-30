package dev.eternal.client.module.impl.movement.longjump;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventTeleport;
import dev.eternal.client.module.impl.movement.LongJump;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class WatchdogMode extends Mode {

  private double moveSpeed;
  private int stage;

  public WatchdogMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    final double base = MovementUtil.getBaseGroundSpeed();
    if (MovementUtil.isMovingOnGround()) {
      if (mc.thePlayer.hurtTime == 0) {
        eventMove.friction(0);
        return;
      }
      moveSpeed = base * 2.149;
      mc.thePlayer.motionY = 0.62F;
      stage = 0;
    } else if (stage == 1) {
      moveSpeed = 2.1f;
    } else if (stage == 2) {
      moveSpeed = base * 1.1;
    } else {
      moveSpeed -= moveSpeed / 89.97;
    }
    moveSpeed = Math.max(base, moveSpeed);
    MovementUtil.setMotion(eventMove, moveSpeed);
    stage++;
  }


  @Subscribe
  public void handleTeleport(EventTeleport eventTeleport) {
    LongJump longJump = this.getOwner();
    longJump.toggle();
  }


  @Override
  public void onEnable() {
    final double value = 0.42F;
    for (int i = 0; i < Math.ceil(3 / value); i++) {
      PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
          mc.thePlayer.posY + value,
          mc.thePlayer.posZ,
          false));
      PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
          mc.thePlayer.posY,
          mc.thePlayer.posZ,
          false));
    }
    PacketUtil.sendSilent(new C03PacketPlayer(true));
    stage = 0;
  }
}
