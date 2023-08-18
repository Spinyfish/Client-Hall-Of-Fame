/* November.lol © 2023 */
package lol.november.utility.render.component;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@Setter
public abstract class Component implements Drawable {

  private final List<Component> components = new LinkedList<>();
  private double x, y, width, height;

  /**
   * Checks if the mouse is in bounds
   *
   * @param mouseX the mouse x coordinate
   * @param mouseY the mouse y coordinate
   * @return if the mouse is in bounds
   */
  public boolean inBounds(int mouseX, int mouseY) {
    return inBounds(mouseX, mouseY, x, y, width, height);
  }

  /**
   * Checks if the mouse is in bounds
   *
   * @param mouseX the mouse x coordinate
   * @param mouseY the mouse y coordinate
   * @param x      the x coordinate
   * @param y      the y coordinate
   * @param w      the width
   * @param h      the height
   * @return if the mouse is in bounds
   */
  public static boolean inBounds(
    int mouseX,
    int mouseY,
    double x,
    double y,
    double w,
    double h
  ) {
    return mouseX >= x && x + w >= mouseX && mouseY >= y && y + h >= mouseY;
  }
}
