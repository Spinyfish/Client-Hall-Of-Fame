/* November.lol © 2023 */
package lol.november.utility.render.component;

/**
 * @author Gavin
 * @since 2.0.0
 */
public interface Drawable {
  void draw(int mouseX, int mouseY, float mouseButton);

  void mouseClicked(int mouseX, int mouseY, int mouseButton);

  void mouseReleased(int mouseX, int mouseY, int state);

  void keyTyped(char typedChar, int keyCode);

  default boolean visible() {
    return true;
  }
}
