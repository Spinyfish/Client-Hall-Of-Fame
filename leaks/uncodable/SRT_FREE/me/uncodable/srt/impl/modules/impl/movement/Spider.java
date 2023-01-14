package me.uncodable.srt.impl.modules.impl.movement;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import store.intent.intentguard.annotation.Native;

@ModuleInfo(
   internalName = "Spider",
   name = "Spider",
   desc = "Allows you to climb walls like a spider.\nYou venomous prick!",
   category = Module.Category.MOVEMENT
)
public class Spider extends Module {
   private static final String COMBO_BOX_SETTING_NAME = "Spider Mode";
   private static final String KARHU = "Karhu";
   private static final String LEGACY_MATRIX = "Legacy Matrix";

   public Spider(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Spider Mode", "Karhu", "Legacy Matrix");
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   @Native
   public void onMotion(EventMotionUpdate e) {
      if (e.getState() == EventMotionUpdate.State.PRE && MC.thePlayer.isCollidedHorizontally) {
         String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
         switch(var2) {
            case "Karhu":
               if (MC.thePlayer.ticksExisted % 3 == 0) {
                  MC.thePlayer.jump();
               }
               break;
            case "Legacy Matrix":
               if (MC.thePlayer.ticksExisted % 4 == 0) {
                  MC.thePlayer.motionY = 0.25;
               }
         }
      }
   }
}
