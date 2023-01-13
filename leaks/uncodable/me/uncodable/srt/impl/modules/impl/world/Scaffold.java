package me.uncodable.srt.impl.modules.impl.world;

import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "Scaffold",
   name = "Scaffold",
   desc = "Allows you to place blocks beneath you.\nAlso tends to anger Hypixel Bedwars Players!",
   category = Module.Category.WORLD,
   exp = true
)
public class Scaffold extends Module {
   public Scaffold(int key, boolean enabled) {
      super(key, enabled);
   }
}
