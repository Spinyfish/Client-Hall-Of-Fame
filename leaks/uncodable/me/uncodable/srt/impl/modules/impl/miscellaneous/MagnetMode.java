package me.uncodable.srt.impl.modules.impl.miscellaneous;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.EntityUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;

@ModuleInfo(
   internalName = "MagnetMode",
   name = "Magnet Mode",
   desc = "Allows you to grab items from afar.\nNo, this module was NOT skidded from the Too Many Items mod.",
   category = Module.Category.MISCELLANEOUS
)
public class MagnetMode extends Module {
   private static final String INTERNAL_DISTANCE_VALUE = "INTERNAL_DISTANCE_VALUE";
   private static final String DISTANCE_VALUE_SETTING_NAME = "Grab Distance";

   public MagnetMode(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_DISTANCE_VALUE", "Grab Distance", 100.0, 0.0, 1000.0);
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      MC.theWorld
         .getLoadedEntityList()
         .parallelStream()
         .filter(
            o -> (o instanceof EntityItem || o instanceof EntityXPOrb)
                  && (double)MC.thePlayer.getDistanceToEntity(o)
                     <= Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_DISTANCE_VALUE", Setting.Type.SLIDER).getCurrentValue()
                  && MC.thePlayer.canEntityBeSeen(o)
         )
         .forEach(i -> EntityUtils.teleportToEntity(i, true));
   }
}
