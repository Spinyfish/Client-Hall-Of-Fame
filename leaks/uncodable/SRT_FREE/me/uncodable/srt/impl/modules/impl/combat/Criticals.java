package me.uncodable.srt.impl.modules.impl.combat;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.Timer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(
   internalName = "Criticals",
   name = "Criticals",
   desc = "Allows you to deal significantly more damage to an opponent.\nAlso allows you to cripple any special education kid with ease, with love <3.",
   category = Module.Category.COMBAT
)
public class Criticals extends Module {
   private final Timer timer = new Timer();
   private static final String INTERNAL_DELAY_VALUE = "INTERNAL_DELAY_VALUE";
   private static final String COMBO_BOX_SETTING_NAME = "Critical Mode";
   private static final String DELAY_VALUE_SETTING_NAME = "Delay";
   private static final String VANILLA = "Demon";

   public Criticals(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Critical Mode", "Demon");
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_DELAY_VALUE", "Delay", 0.0, 0.0, 1000.0, true);
   }

   @Override
   public void onDisable() {
      this.timer.reset();
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      if (MC.thePlayer.isSwingInProgress
         && MC.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
         && this.timer.elapsed((long)Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_DELAY_VALUE", Setting.Type.SLIDER).getCurrentValue())) {
         String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
         switch(var2) {
            case "Demon":
               for(int i = 0; i < 3; ++i) {
                  MC.thePlayer
                     .sendQueue
                     .packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(MC.thePlayer.posX, MC.thePlayer.posY + 0.2F, MC.thePlayer.posZ, false));
                  MC.thePlayer
                     .sendQueue
                     .packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(MC.thePlayer.posX, MC.thePlayer.posY, MC.thePlayer.posZ, false));
               }
         }

         this.timer.reset();
      }
   }
}
