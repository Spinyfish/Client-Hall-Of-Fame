package dev.eternal.client.module.impl.render;

import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.EnumSetting;
import dev.eternal.client.property.impl.interfaces.INameable;
import dev.eternal.client.ui.clickgui.ClickGuiManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lwjgl.input.Keyboard;

@Getter
@ModuleInfo(name = "ClickGUI", description = "Allows you to visually configure modules", category = Module.Category.RENDER, defaultKey = Keyboard.KEY_RSHIFT)
public class ClickUI extends Module {

  private final EnumSetting<Modes> enumSetting = new EnumSetting<>(this, "Mode", Modes.values());

  @Override
  public void onEnable() {
    this.mc.displayGuiScreen(ClickGuiManager.getClickGui(1));
    toggle();
  }

  @AllArgsConstructor
  @Getter
  private enum Modes implements INameable {
    ETERNAL("Eternal"),
    HACKING_LORD("Hacking_Lord");
    private final String getName;
  }

}
