/* November.lol © 2023 */
package lol.november.feature.module;

import lombok.Getter;

/**
 * @author Gavin
 * @since 1.0.0
 */
@Getter
public enum Category {
  COMBAT("Combat"),
  EXPLOIT("Exploit"),
  GHOST("Ghost"),
  MOVEMENT("Movement"),
  PLAYER("Player"),
  VISUAL("Visual");

  private final String display;

  Category(String display) {
    this.display = display;
  }
}
