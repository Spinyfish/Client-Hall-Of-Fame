package dev.eternal.client.module.impl.movement;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@ModuleInfo(name = "GuiMove", description = "Allows you to move in inventories.", category = Module.Category.MOVEMENT)
public class GuiMove extends Module {

  private final List<KeyBinding> keyBindingArray = Arrays.asList(
      mc.gameSettings.keyBindRight,
      mc.gameSettings.keyBindLeft,
      mc.gameSettings.keyBindForward,
      mc.gameSettings.keyBindBack,
      mc.gameSettings.keyBindJump,
      mc.gameSettings.keyBindTogglePerspective
  );

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    if (mc.currentScreen instanceof GuiChat) return;
    keyBindingArray.forEach(keyBinding -> KeyBinding.setKeyBindState(keyBinding.getKeyCode(), Keyboard.isKeyDown(keyBinding.getKeyCode())));
  }

}
