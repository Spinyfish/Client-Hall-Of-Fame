package me.uncodable.srt.impl.modules.impl.ghost;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "Reach",
   name = "Reach",
   desc = "Allows you to completely out-reach your opponents.\nYou've transformed into the average Hypixel ranked skywars player!",
   category = Module.Category.GHOST
)
public class Reach extends Module {
   private static final String INTERNAL_REACH_VALUE = "INTERNAL_REACH_VALUE";
   private static final String REACH_VALUE_SETTING_NAME = "Reach";

   public Reach(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_REACH_VALUE", "Reach", 6.0, 0.0, 10.0);
   }
}
