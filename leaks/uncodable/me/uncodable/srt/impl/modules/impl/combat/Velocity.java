package me.uncodable.srt.impl.modules.impl.combat;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.PacketUtils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import store.intent.intentguard.annotation.Native;

@ModuleInfo(
   internalName = "Velocity",
   name = "Velocity",
   desc = "Allows you to change your velocity or cancel it out overall.\nReal cars in the real world use a form of this cheat called suspension.",
   category = Module.Category.COMBAT
)
public class Velocity extends Module {
   private static final String COMBO_BOX_SETTING_NAME = "Velocity Mode";
   private static final String VANILLA = "Vanilla";
   private static final String AGC = "AntiGamingChair";
   private boolean cancel;

   public Velocity(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Velocity Mode", "Vanilla", "AntiGamingChair");
   }

   @EventTarget(
      target = EventPacket.class
   )
   @Native
   public void onPacket(EventPacket e) {
      String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      switch(var2) {
         case "Vanilla":
            e.setCancelled(e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion);
            break;
         case "AntiGamingChair":
            if (e.getPacket() instanceof S12PacketEntityVelocity) {
               S12PacketEntityVelocity packet = PacketUtils.getPacket(e.getPacket());
               if (packet.getEntityID() == MC.thePlayer.getEntityId()) {
                  e.setCancelled(this.cancel = !this.cancel);
               }
            }
      }
   }
}
