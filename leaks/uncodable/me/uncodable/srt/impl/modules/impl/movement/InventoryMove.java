package me.uncodable.srt.impl.modules.impl.movement;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
   internalName = "InventoryMove",
   name = "Inventory Move",
   desc = "Allows you to move while in your inventory.\nNo, there is no Azura-like mode that contradicts the functionality of this module.",
   category = Module.Category.MOVEMENT
)
public class InventoryMove extends Module {
   private static final String INTERNAL_ALLOW_JUMP = "INTERNAL_ALLOW_JUMP";
   private static final String ALLOW_JUMP_SETTING_NAME = "Allow Jump";

   public InventoryMove(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_ALLOW_JUMP", "Allow Jump");
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      if (MC.currentScreen instanceof GuiContainer) {
         if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_ALLOW_JUMP", Setting.Type.CHECKBOX).isTicked()) {
            MC.gameSettings.keyBindJump.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindJump.getKeyCode()));
         }

         MC.gameSettings.keyBindRight.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindRight.getKeyCode()));
         MC.gameSettings.keyBindLeft.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindLeft.getKeyCode()));
         MC.gameSettings.keyBindForward.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindForward.getKeyCode()));
         MC.gameSettings.keyBindBack.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindBack.getKeyCode()));
         MC.gameSettings.keyBindSprint.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindSprint.getKeyCode()));
      }
   }
}
