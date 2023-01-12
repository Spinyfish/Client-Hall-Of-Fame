package dev.eternal.client.module.impl.movement.flight;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.event.events.EventTeleport;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.impl.movement.Flight;
import dev.eternal.client.module.impl.movement.Speed;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.movement.data.Position;
import dev.eternal.client.util.network.PacketUtil;
import dev.eternal.client.util.world.WorldUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class BufferAbuseMode extends Mode {

  public BufferAbuseMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleUpdate(EventMove eventMove) {
    if (WorldUtil.getBlockBellow() != BlockPos.ORIGIN && mc.gameSettings.keyBindSneak.isKeyDown())
      mc.thePlayer.setPosition(mc.thePlayer.posX, WorldUtil.getBlockBellow().getY() + 1, mc.thePlayer.posZ);
    mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 0.6 : 0;
    MovementUtil.setMotion(eventMove, ((Flight) getOwner()).speedSetting().value());
  }

  @Subscribe
  public void handlePacket(EventPacket eventPacket) {
    if (eventPacket.getPacket() instanceof C03PacketPlayer) {
      eventPacket.cancelled(true);
    }
  }

  @Override
  public void onDisable() {
    for (int i = 0; i < 40; i++) {
      PacketUtil.sendSilent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - WorldUtil.distanceToGround(), mc.thePlayer.posZ, -1, 0f, false));
    }
    MovementUtil.setMotion(0);
    mc.timer.timerSpeed = 1;
  }

  @Subscribe
  public void handleTeleport(EventTeleport eventTeleport) {
  }
}
