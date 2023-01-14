package me.uncodable.srt.impl.modules.impl.player;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import net.minecraft.network.play.client.C16PacketClientStatus;

@ModuleInfo(
   internalName = "FastRespawn",
   name = "Fast Respawn",
   desc = "Allows you to respawn faster or instantly.\nYou'll no longer have to feel like you've aged twenty years waiting to respawn.",
   category = Module.Category.PLAYER
)
public class FastRespawn extends Module {
   private static final String COMBO_BOX_SETTING_NAME = "Respawn Mode";
   private static final String INSTANT = "Instant";
   private static final String FAST = "Fast";

   public FastRespawn(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Respawn Mode", "Instant", "Fast");
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
      if (e.getState() == EventUpdate.State.PRE
         && (
            !Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo().equals("Fast")
               || !MC.thePlayer.isEntityAlive()
         )) {
         MC.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
      }
   }
}
