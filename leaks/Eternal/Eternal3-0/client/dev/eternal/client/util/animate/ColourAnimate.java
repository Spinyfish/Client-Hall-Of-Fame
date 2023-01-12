package dev.eternal.client.util.animate;

import java.awt.*;

public class ColourAnimate {

  private Color color;
  private final Animate red, green, blue;

  public ColourAnimate(Color color) {
    this.color = color;
    red = new Animate(color.getRed(), 0.6F);
    green = new Animate(color.getGreen(), 0.6F);
    blue = new Animate(color.getBlue(), 0.6F);
  }

  public int getColourInt() {
    return color.getRGB();
  }

  public Color getColorObject() {
    return this.color;
  }

  public void switchColour(Color newColour) {
    boolean same = newColour.getRGB() == color.getRGB();

    if (!same) {
      red.interpolate(newColour.getRed());
      green.interpolate(newColour.getGreen());
      blue.interpolate(newColour.getBlue());

      this.color = new Color((int) red.getValue(), (int) green.getValue(), (int) blue.getValue());
    }
  }

}
