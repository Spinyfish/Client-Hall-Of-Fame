package me.uncodable.srt.impl.modules.impl.movement;

import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(
   internalName = "Unstuck",
   name = "Unstuck",
   desc = "Allows you to get away from an anti-cheat holding you hostage in mid-air.",
   category = Module.Category.MOVEMENT
)
public class Unstuck extends Module {
   public Unstuck(int key, boolean enabled) {
      super(key, enabled);
   }

   @EventTarget(
      target = EventPacket.class
   )
   public void onPacket(EventPacket e) {
      e.setCancelled(e.getPacket() instanceof C03PacketPlayer);
   }
}
