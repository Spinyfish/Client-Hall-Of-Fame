/* November.lol © 2023 */
package lol.november.feature.keybind;

import static lol.november.feature.keybind.KeyBind.INVALID_KEY;

import java.util.HashMap;
import java.util.Map;
import lol.november.November;
import lol.november.feature.keybind.config.KeyBindConfig;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.input.EventKeyInput;
import lol.november.listener.event.input.EventMouseInput;
import lombok.Getter;
import net.minecraft.client.Minecraft;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
public class KeyBindManager {

  /**
   * The {@link Minecraft} game instance
   */
  private final Minecraft mc = Minecraft.getMinecraft();

  /**
   * A {@link HashMap} of the key name and its {@link KeyBind} object
   */
  private final Map<String, KeyBind> keyBinds = new HashMap<>();

  public KeyBindManager() {
    November.instance().configs().add(new KeyBindConfig(this));
    November.bus().subscribe(this);
  }

  @Subscribe
  private final Listener<EventKeyInput> keyInput = event -> {
    if (event.keyCode() == INVALID_KEY || mc.currentScreen != null) return;

    for (KeyBind keyBind : keyBinds.values()) {
      // i swear to christ if this ever breaks ima kms
      if (
        keyBind.getKey() != event.keyCode() ||
        keyBind.getType() != KeyBindType.KEYBOARD
      ) continue;
      keyBind.setState(!keyBind.toggled());
    }
  };

  @Subscribe
  private final Listener<EventMouseInput> mouseInput = event -> {
    if (event.mouseButton() == INVALID_KEY || mc.currentScreen != null) return;

    for (KeyBind keyBind : keyBinds.values()) {
      // i swear to christ if this ever breaks ima kms
      if (
        keyBind.getKey() != event.mouseButton() ||
        keyBind.getType() != KeyBindType.MOUSE
      ) continue;
      keyBind.setState(!keyBind.toggled());
    }
  };

  /**
   * Registers a {@link KeyBind} object
   *
   * @param name    the name
   * @param keyBind the {@link KeyBind} object
   */
  public void register(String name, KeyBind keyBind) {
    keyBinds.put(name, keyBind);
  }

  /**
   * Gets a {@link KeyBind} by its name
   *
   * @param name the name
   * @return the {@link KeyBind} object or null
   */
  public KeyBind get(String name) {
    return keyBinds.getOrDefault(name, null);
  }
}
