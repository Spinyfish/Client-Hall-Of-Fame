package me.uncodable.srt.impl.modules.api;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.events.peripheral.EventKeyPress;

public class KeyBindSystem {
   public void onKeyPress(EventKeyPress e) {
      Ries.INSTANCE.getModuleManager().getModules().forEach(module -> {
         if (module.getPrimaryKey() == e.getKey()) {
            module.toggle();
         }
      });
   }
}
