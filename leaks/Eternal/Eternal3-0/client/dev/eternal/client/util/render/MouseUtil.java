package dev.eternal.client.util.render;

import dev.eternal.client.util.IMinecraft;

public class MouseUtil implements IMinecraft {
  public static boolean isHovered(double x, double y, double width, double height, int mouseX, int mouseY) {
    return mouseX >= x && mouseY >= y && mouseX <= width && mouseY <= height;
  }
}
