package dev.eternal.client.module.impl.movement.flight;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.module.Module;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

public class MorganMode extends Mode {

  private double speed;

  public MorganMode(Module module, String name) {
    super(module, name);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    EntityPlayerSP playerInstance = mc.thePlayer;
    playerInstance.motionY = mc.thePlayer.ticksExisted % 4 == 0 && mc.thePlayer.movementInput.jump ? .42F : mc.thePlayer.ticksExisted % 3 >= 1 ? mc.thePlayer.motionY : 0.09;
    if (mc.thePlayer.onGround || mc.thePlayer.isSneaking() || mc.thePlayer.fallDistance > 0) {
      MovementUtil.setMotion(eventMove, MovementUtil.getUpdatedNCPBaseSpeed());
      mc.thePlayer.fallDistance = 0;
      return;
    }
    double base = 0.2865;
    if (mc.thePlayer.isPotionActive(1)) {
      base *= 1 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
    }
    MovementUtil.setMotion(eventMove, base);
  }

  @Subscribe
  public void handlePacket(EventPacket eventPacket) {
    if (eventPacket.getPacket() instanceof C03PacketPlayer) {
      if (mc.thePlayer.ticksExisted % 3 == 0) {
        C03PacketPlayer packetPlayer = eventPacket.getPacket();
        packetPlayer.onGround(true);
        packetPlayer.y(packetPlayer.getPositionY() - packetPlayer.getPositionY() % 0.015625);
      }
    }
  }

  @Override
  public void onDisable() {
    mc.timer.timerSpeed = 1;
    mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
  }
}
