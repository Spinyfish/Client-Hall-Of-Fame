package me.uncodable.srt.impl.modules.impl.miscellaneous;

import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "NetherPortalFix",
   name = "Nether Portal Fix",
   desc = "Allows you to open GUIs while in a nether portal.\nHow does Mojang fuck this shit up?",
   category = Module.Category.MISCELLANEOUS
)
public class NetherPortalFix extends Module {
   public NetherPortalFix(int key, boolean enabled) {
      super(key, enabled);
   }
}
