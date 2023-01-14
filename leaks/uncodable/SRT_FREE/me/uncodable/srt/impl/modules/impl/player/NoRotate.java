package me.uncodable.srt.impl.modules.impl.player;

import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.utils.PacketUtils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleInfo(
   internalName = "NoRotate",
   name = "No Rotate",
   desc = "Prevents the server from setting your rotations.\nCompletely negates server-induced Parkinson's disease.",
   category = Module.Category.PLAYER
)
public class NoRotate extends Module {
   public NoRotate(int key, boolean enabled) {
      super(key, enabled);
   }

   @EventTarget(
      target = EventPacket.class
   )
   public void onPacket(EventPacket e) {
      if (e.getPacket() instanceof S08PacketPlayerPosLook) {
         S08PacketPlayerPosLook packet = PacketUtils.getPacket(e.getPacket());
         packet.setYaw(MC.thePlayer.rotationYaw);
         packet.setPitch(MC.thePlayer.rotationPitch);
      }
   }
}
