package me.uncodable.srt.impl.modules.impl.movement;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;

@ModuleInfo(
   internalName = "AntiVoid",
   name = "Anti-Void",
   desc = "Allows you to rubberband back after your dumbass fell into the void.\nUncodable can relate very well... is that a good thing, though?",
   category = Module.Category.MOVEMENT
)
public class AntiVoid extends Module {
   private static final String COMBO_BOX_SETTING_NAME = "Anti-Void Mode";
   private static final String BASIC = "Basic";

   public AntiVoid(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Anti-Void Mode", "Basic");
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      if ((double)MC.thePlayer.fallDistance > 10.0 && e.getState() == EventMotionUpdate.State.PRE) {
         String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
         byte var3 = -1;
         switch(var2.hashCode()) {
            case 63955982:
               if (var2.equals("Basic")) {
                  var3 = 0;
               }
            default:
               switch(var3) {
                  case 0:
                     e.setPosX(MC.thePlayer.posX + 10.1);
               }
         }
      }
   }
}
