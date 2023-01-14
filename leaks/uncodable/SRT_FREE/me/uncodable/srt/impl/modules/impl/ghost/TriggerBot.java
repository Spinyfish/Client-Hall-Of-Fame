package me.uncodable.srt.impl.modules.impl.ghost;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.Timer;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.RandomUtils;

@ModuleInfo(
   internalName = "TriggerBot",
   name = "Trigger Bot",
   desc = "Allows you to attack an entity as soon as the cross-hair is over their hit-box.\nIn other words, pull the trigga on them dumb niggas.",
   category = Module.Category.GHOST
)
public class TriggerBot extends Module {
   private static final String INTERNAL_MINIMUM_CPS_VALUE = "INTERNAL_MINIMUM_CPS_VALUE";
   private static final String INTERNAL_MAXIMUM_CPS_VALUE = "INTERNAL_MAXIMUM_CPS_VALUE";
   private static final String INTERNAL_STATE_COMBO_BOX = "INTERNAL_STATE_COMBO_BOX";
   private static final String MINIMUM_CPS_VALUE_SETTING_NAME = "Minimum CPS";
   private static final String MAXIMUM_CPS_VALUE_SETTING_NAME = "Maximum CPS";
   private static final String STATE_SETTING_NAME = "State";
   private static final String PRE = "Pre";
   private static final String POST = "Post";
   private static final String BOTH = "Both";
   private final Timer timer = new Timer();

   public TriggerBot(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_STATE_COMBO_BOX", "State", "Pre", "Post", "Both");
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_MINIMUM_CPS_VALUE", "Minimum CPS", 12.0, 1.0, 20.0, true);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_MAXIMUM_CPS_VALUE", "Maximum CPS", 12.0, 1.0, 20.0, true);
   }

   @Override
   public void onDisable() {
      this.timer.reset();
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      double max = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_MAXIMUM_CPS_VALUE", Setting.Type.SLIDER).getCurrentValue();
      double min = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_MINIMUM_CPS_VALUE", Setting.Type.SLIDER).getCurrentValue();
      if (min > max) {
         Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_MAXIMUM_CPS_VALUE", Setting.Type.SLIDER).setCurrentValue(min);
         max = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_MAXIMUM_CPS_VALUE", Setting.Type.SLIDER).getCurrentValue();
      }

      long actualCPS = RandomUtils.nextLong((long)min, (long)max);
      String var8 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_STATE_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      switch(var8) {
         case "Pre":
            if (e.getState() != EventMotionUpdate.State.PRE) {
               return;
            }
            break;
         case "Post":
            if (e.getState() != EventMotionUpdate.State.POST) {
               return;
            }
      }

      if (MC.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && this.timer.elapsed(1000L / actualCPS)) {
         MC.thePlayer.swingItem();
         MC.playerController.attackEntity(MC.thePlayer, MC.objectMouseOver.entityHit);
         this.timer.reset();
      }
   }
}
