package me.uncodable.srt.impl.modules.impl.movement;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.MovementUtils;

@ModuleInfo(
   internalName = "FastLadder",
   name = "Fast Ladder",
   desc = "Allows you to climb ladders like your life depends on it.",
   category = Module.Category.MOVEMENT
)
public class FastLadder extends Module {
   private static final String COMBO_BOX_SETTING_NAME = "Fast Ladder Mode";
   private static final String INTERNAL_MOTION_VALUE = "INTERNAL_MOTION_VALUE";
   private static final String MOTION_VALUE_SETTING_NAME = "Motion";
   private static final String VANILLA = "Vanilla";
   private boolean wasOnLadder;

   public FastLadder(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Fast Ladder Mode", "Vanilla");
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_MOTION_VALUE", "Motion", 1.0, 0.0, 10.0);
   }

   @Override
   public void onDisable() {
      this.wasOnLadder = false;
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      if (e.getState() == EventMotionUpdate.State.PRE) {
         if (MC.thePlayer.isOnLadder() && MovementUtils.isMoving2()) {
            double motion = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_MOTION_VALUE", Setting.Type.SLIDER).getCurrentValue();
            this.wasOnLadder = true;
            String var4 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
            byte var5 = -1;
            switch(var4.hashCode()) {
               case 1897755483:
                  if (var4.equals("Vanilla")) {
                     var5 = 0;
                  }
               default:
                  switch(var5) {
                     case 0:
                        MC.thePlayer.motionY = motion;
                  }
            }
         } else if (this.wasOnLadder) {
            MC.thePlayer.motionY = 0.0;
            this.wasOnLadder = false;
         }
      }
   }
}
