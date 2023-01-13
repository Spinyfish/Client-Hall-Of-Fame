package me.uncodable.srt.impl.modules.impl.movement;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "NoSlowdown",
   name = "No Slowdown",
   desc = "Allows you to never slow down again.\nIn the real world, this would be perfect for the drag strip!",
   category = Module.Category.MOVEMENT
)
public class NoSlowdown extends Module {
   private static final String COMBO_BOX_SETTING_NAME = "No Slowdown Mode";
   private static final String VANILLA = "Vanilla";

   public NoSlowdown(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "No Slowdown Mode", "Vanilla");
   }
}
