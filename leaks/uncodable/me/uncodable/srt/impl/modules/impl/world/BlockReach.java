package me.uncodable.srt.impl.modules.impl.world;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "BlockReach",
   name = "Block Reach",
   desc = "Allows you to break blocks from afar.",
   category = Module.Category.WORLD
)
public class BlockReach extends Module {
   private static final String INTERNAL_BLOCK_REACH_VALUE = "INTERNAL_BLOCK_REACH_VALUE";
   private static final String BLOCK_REACH_VALUE_SETTING_NAME = "Block Reach";

   public BlockReach(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_BLOCK_REACH_VALUE", "Block Reach", 10.0, 0.0, 10.0);
   }
}
