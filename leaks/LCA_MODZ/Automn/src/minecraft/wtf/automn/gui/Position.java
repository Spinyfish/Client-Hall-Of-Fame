package wtf.automn.gui;

import com.google.gson.annotations.Expose;

public class Position {

  @Expose
  public float x, y, width, height;

  public Position(float x, float y, float width, float height) {
    super();
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public static Position empty() {
    return new Position(-1, -1, 0, 0);
  }

  public boolean isHovered(int mouseX, int mouseY) {
    return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
  }

  public boolean isHovered(int mouseX, int mouseY, int offsetX, int offsetY, int cWidth, int cHeight) {
    return mouseX >= x + offsetX && mouseX <= x + offsetX + cWidth && mouseY >= y + offsetY && mouseY <= y + offsetY + cHeight;
  }

  public float[] clicksOff(int mouseX, int mouseY) {
    return new float[]{mouseX - x, mouseY - y};
  }

}
