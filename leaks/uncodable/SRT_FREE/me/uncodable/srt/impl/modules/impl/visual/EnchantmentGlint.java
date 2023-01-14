package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "EnchantmentGlint",
   name = "Enchantment Glint",
   desc = "Allows you to change the color of the enchantment glint.\nNo more generic blue!",
   category = Module.Category.VISUAL,
   legit = true
)
public class EnchantmentGlint extends Module {
   private static final String INTERNAL_RED_VALUE = "INTERNAL_RED_VALUE";
   private static final String INTERNAL_GREEN_VALUE = "INTERNAL_GREEN_VALUE";
   private static final String INTERNAL_BLUE_VALUE = "INTERNAL_BLUE_VALUE";
   private static final String INTERNAL_DISABLE_GLINT = "INTERNAL_DISABLE_GLINT";
   private static final String RED_VALUE_SETTING_NAME = "Red";
   private static final String GREEN_VALUE_SETTING_NAME = "Green";
   private static final String BLUE_VALUE_SETTING_NAME = "Blue";
   private static final String DISABLE_GLINT = "Disable Glint";

   public EnchantmentGlint(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_RED_VALUE", "Red", 255.0, 0.0, 255.0, true);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_GREEN_VALUE", "Green", 0.0, 0.0, 255.0, true);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_BLUE_VALUE", "Blue", 0.0, 0.0, 255.0, true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_DISABLE_GLINT", "Disable Glint");
   }
}
