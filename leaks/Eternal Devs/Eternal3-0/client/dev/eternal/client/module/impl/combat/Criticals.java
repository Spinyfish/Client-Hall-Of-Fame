package dev.eternal.client.module.impl.combat;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(name = "Criticals", description = "Deals critical hits.", category = Module.Category.COMBAT)
public class Criticals extends Module {

  @Subscribe
  public void onPacket(EventPacket eventPacket) {
    if (eventPacket.getPacket() instanceof C02PacketUseEntity c02PacketUseEntity) {
      if (c02PacketUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK) {
        if (mc.thePlayer.onGround) {
          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.05, mc.thePlayer.posZ, false));
          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.012511, mc.thePlayer.posZ, false));
          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }
      }
    }
  }
}
