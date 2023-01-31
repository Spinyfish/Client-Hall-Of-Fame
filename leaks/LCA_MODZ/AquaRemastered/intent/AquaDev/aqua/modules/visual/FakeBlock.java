package intent.AquaDev.aqua.modules.visual;

import events.Event;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;

public class FakeBlock extends Module {
   public FakeBlock() {
      super("FakeBlock", Module.Type.Visual, "FakeBlock", 0, Category.Visual);
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
   }
}
