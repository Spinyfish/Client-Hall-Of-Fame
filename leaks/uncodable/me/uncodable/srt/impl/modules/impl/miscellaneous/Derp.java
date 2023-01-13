package me.uncodable.srt.impl.modules.impl.miscellaneous;

import java.util.Random;
import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import org.apache.commons.lang3.RandomUtils;

@ModuleInfo(
   internalName = "Derp",
   name = "Derp",
   desc = "Allows you to look quite frankly retarded.\nI would add a funny remark, but I've harassed the special education students enough...",
   category = Module.Category.MISCELLANEOUS
)
public class Derp extends Module {
   private static final String INTERNAL_LOCKVIEW = "INTERNAL_LOCKVIEW";
   private static final String INTERNAL_SPIN = "INTERNAL_SPIN";
   private static final String INTERNAL_HEADLESS = "INTERNAL_HEADLESS";
   private static final String INTERNAL_SPIN_SPEED_VALUE = "INTERNAL_SPIN_SPEED_VALUE";
   private static final String INTERNAL_SPIN_RANDOMIZATION = "INTERNAL_SPIN_RANDOMIZATION";
   private static final String LOCKVIEW_SETTING_NAME = "Use Lock View";
   private static final String SPIN_SETTING_NAME = "Spin";
   private static final String HEADLESS_SETTING_NAME = "Headless";
   private static final String SPIN_SPEED_VALUE_SETTING_NAME = "Spin Speed";
   private static final String SPIN_RANDOMIZATION_SETTING_NAME = "Randomize Spin";
   private float yaw;
   private final Random random = new Random();

   public Derp(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_SPIN_SPEED_VALUE", "Spin Speed", 0.0, 0.0, 100.0, true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_SPIN", "Spin", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_HEADLESS", "Headless", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_LOCKVIEW", "Use Lock View");
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_SPIN_RANDOMIZATION", "Randomize Spin", true);
   }

   @Override
   public void onEnable() {
      if (MC.thePlayer != null) {
         this.yaw = MC.thePlayer.rotationYaw;
      } else {
         this.yaw = 0.0F;
      }
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_LOCKVIEW", Setting.Type.CHECKBOX).isTicked()) {
         if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_HEADLESS", Setting.Type.CHECKBOX).isTicked()) {
            MC.thePlayer.rotationPitch = -180.0F;
         }

         if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPIN", Setting.Type.CHECKBOX).isTicked()) {
            MC.thePlayer.rotationYaw = (float)(
               (double)MC.thePlayer.rotationYaw
                  + Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPIN_SPEED_VALUE", Setting.Type.SLIDER).getCurrentValue()
            );
            if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPIN_RANDOMIZATION", Setting.Type.CHECKBOX).isTicked()) {
               MC.thePlayer.rotationYaw += this.random.nextBoolean() ? RandomUtils.nextFloat(0.0F, 10.0F) : -RandomUtils.nextFloat(0.0F, 10.0F);
            }
         }
      } else {
         if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_HEADLESS", Setting.Type.CHECKBOX).isTicked()) {
            e.setRotationPitch(-180.0F);
         }

         if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPIN", Setting.Type.CHECKBOX).isTicked()) {
            e.setRotationYaw(this.yaw);
            this.yaw = (float)(
               (double)this.yaw + Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPIN_SPEED_VALUE", Setting.Type.SLIDER).getCurrentValue()
            );
            if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPIN_RANDOMIZATION", Setting.Type.CHECKBOX).isTicked()) {
               this.yaw += this.random.nextBoolean() ? RandomUtils.nextFloat(0.0F, 10.0F) : -RandomUtils.nextFloat(0.0F, 10.0F);
            }
         }
      }
   }
}
