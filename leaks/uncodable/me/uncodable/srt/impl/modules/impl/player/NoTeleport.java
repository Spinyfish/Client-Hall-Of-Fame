package me.uncodable.srt.impl.modules.impl.player;

import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.utils.PacketUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleInfo(
   internalName = "NoTeleport",
   name = "No Teleport",
   desc = "Prevents the server from teleporting you back.\nCan be used effectively in conjunction with an anti-cheat disabler.",
   category = Module.Category.PLAYER
)
public class NoTeleport extends Module {
   public boolean received;
   public double x;
   public double y;
   public double z;

   public NoTeleport(int key, boolean enabled) {
      super(key, enabled);
   }

   @Override
   public void onDisable() {
      this.received = false;
      this.x = this.y = this.z = 0.0;
   }

   @EventTarget(
      target = EventPacket.class
   )
   public void onPacket(EventPacket e) {
      if (e.getPacket() instanceof S08PacketPlayerPosLook) {
         S08PacketPlayerPosLook packet = PacketUtils.getPacket(e.getPacket());
         this.x = packet.getX();
         this.y = packet.getY();
         this.z = packet.getZ();
         this.received = true;
         e.setCancelled(true);
      }

      if (e.getPacket() instanceof C03PacketPlayer && this.received) {
         C03PacketPlayer packet = PacketUtils.getPacket(e.getPacket());
         packet.setX(this.x);
         packet.setY(this.y);
         packet.setZ(this.z);
         this.received = false;
      }
   }
}
