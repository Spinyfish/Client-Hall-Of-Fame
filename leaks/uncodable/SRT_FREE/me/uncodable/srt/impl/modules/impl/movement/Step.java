package me.uncodable.srt.impl.modules.impl.movement;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;

@ModuleInfo(
   internalName = "Step",
   name = "Step",
   desc = "Allows you to step higher than a half-block (or, less if your heart desires).",
   category = Module.Category.MOVEMENT
)
public class Step extends Module {
   private static final String INTERNAL_STEP_HEIGHT_VALUE = "INTERNAL_STEP_HEIGHT_VALUE";
   private static final String COMBO_BOX_SETTING_NAME = "Step Mode";
   private static final String STEP_HEIGHT_VALUE_SETTING_NAME = "Step Height";
   private static final String VANILLA = "Vanilla";
   private static final String GUARD = "Guard";
   private float oldStepHeight;

   public Step(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Step Mode", "Vanilla", "Guard");
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_STEP_HEIGHT_VALUE", "Step Height", 1.0, 0.0, 10.0);
   }

   @Override
   public void onEnable() {
      if (MC.thePlayer != null) {
         this.oldStepHeight = MC.thePlayer.stepHeight;
      } else {
         this.oldStepHeight = 0.6F;
      }
   }

   @Override
   public void onDisable() {
      MC.thePlayer.stepHeight = this.oldStepHeight;
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      if (e.getState() == EventMotionUpdate.State.PRE) {
         float stepHeight = (float)Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_STEP_HEIGHT_VALUE", Setting.Type.SLIDER).getCurrentValue();
         String var3 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
         switch(var3) {
            case "Vanilla":
               MC.thePlayer.stepHeight = stepHeight;
               break;
            case "Guard":
               MC.thePlayer.stepHeight = MC.thePlayer.isCollidedHorizontally && MC.thePlayer.ticksExisted % 5 == 0 ? stepHeight : this.oldStepHeight;
         }
      }
   }
}
