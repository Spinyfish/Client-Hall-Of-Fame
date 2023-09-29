package dev.eternal.client.util.client;

public class MouseUtils {

  public static boolean isInArea(int mouseX, int mouseY, int x, int y, int width, int height) {
    return (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height);
  }

  public static boolean isInArea(double mouseX, double mouseY, double x, double y, double width, double height) {
    return (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height);
  }

  public static boolean isInAreaOther(int mouseX, int mouseY, int x, int y, int x2, int y2) {
    return (mouseX > x && mouseX < x2 && mouseY > y && mouseY < y2);
  }

  public static boolean isInAreaOther(int mouseX, int mouseY, double x, double y, double x2, double y2) {
    return (mouseX > x && mouseX < x2 && mouseY > y && mouseY < y2);
  }

}
