package dev.eternal.client.util.animate;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class Opacity {

  @Setter
  private Color colour;
  @Getter
  private final Animate alpha = new Animate(0, 0.15F);

  public Opacity(int colour) {
    this.colour = new Color(colour);
  }

  public Opacity(Color colour) {
    this.colour = colour;
  }

  public int getColour() {
    return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), (int) alpha.getValue()).getRGB();
  }

  public void alphaIn() {
    alpha.interpolate(255);
  }

  public void alphaOut() {
    alpha.interpolate(0);
  }

  public void setAlpha(float alpha) {
    this.alpha.setValue(alpha);
  }

}
