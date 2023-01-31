package intent.AquaDev.aqua.modules.movement;

import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import net.minecraft.client.settings.KeyBinding;

public class Sprint extends Module {
   public Sprint() {
      super("Sprint", Module.Type.Movement, "Sprint", 0, Category.Movement);
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
      super.onDisable();
   }

   @Override
   public void onEvent(Event e) {
      if (e instanceof EventUpdate && mc.gameSettings.keyBindForward.pressed) {
         mc.thePlayer.setSprinting(true);
      }
   }
}
