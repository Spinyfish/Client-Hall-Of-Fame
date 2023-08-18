/* November.lol © 2023 */
package lol.november.feature.module.impl.visual;

import static org.lwjgl.input.Keyboard.KEY_RSHIFT;

import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.module.gui.ClickGUIScreen;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "ClickGUI",
  description = "Displays a GUI that allows you to configure modules",
  category = Category.VISUAL
)
public class ClickGUIModule extends Module {

  private ClickGUIScreen screen;

  public ClickGUIModule() {
    // default keybind
    getKeyBind().getValue().setKey(KEY_RSHIFT);
  }

  @Override
  public void enable() {
    super.enable();

    if (mc.thePlayer == null) {
      setState(false);
      return;
    }

    if (screen == null) screen = new ClickGUIScreen(this);

    mc.displayGuiScreen(screen);
  }
}
