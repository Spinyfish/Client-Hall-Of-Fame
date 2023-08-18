/* November.lol © 2023 */
package lol.november.utility.render.font;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@Setter
public class Glyph {

  private final char character;
  private double x, y, width, height;

  public Glyph(char character) {
    this.character = character;
  }
}
