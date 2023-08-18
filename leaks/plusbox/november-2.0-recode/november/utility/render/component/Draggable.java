/* November.lol © 2023 */
package lol.november.utility.render.component;

/**
 * @author Gavin
 * @since 2.0.0
 */
public abstract class Draggable extends Component {

  private static final int DRAG_BUTTON = 0;

  private double dragX, dragY;
  private boolean dragging;

  @Override
  public void draw(int mouseX, int mouseY, float mouseButton) {
    if (dragging) {
      setX(dragX + mouseX);
      setY(dragY + mouseY);
    }
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (inBounds(mouseX, mouseY) && mouseButton == DRAG_BUTTON) {
      dragging = true;
      dragX = getX() - mouseX;
      dragY = getY() - mouseY;
    }
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int state) {
    if (dragging && state == DRAG_BUTTON) {
      dragging = false;
    }
  }

  public boolean isDragging() {
    return dragging;
  }
}
