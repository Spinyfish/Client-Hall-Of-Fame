/* November.lol © 2023 */
package lol.november.feature.keybind;

import lombok.Getter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
public enum KeyBindType {
  KEYBOARD("keyboard"),
  MOUSE("mouse"),
  UNKNOWN("unknown");

  private final String name;

  KeyBindType(String name) {
    this.name = name;
  }

  /**
   * Gets a {@link KeyBindType} from the supplied name
   *
   * @param name the name
   * @return the {@link KeyBindType} enum constant or null
   */
  public static KeyBindType from(String name) {
    for (KeyBindType keyBindType : values()) {
      if (keyBindType.getName().equalsIgnoreCase(name)) return keyBindType;
    }

    return null;
  }
}
