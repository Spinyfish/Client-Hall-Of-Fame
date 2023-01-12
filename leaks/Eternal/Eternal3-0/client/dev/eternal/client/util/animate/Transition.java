package dev.eternal.client.util.animate;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

@Getter
@Setter
public class Transition {

  private final Animate posX;
  private final Animate posY;
  private int centerX, centerY;
  private boolean scale, rotate;
  private static final Minecraft MC = Minecraft.getMinecraft();

  public Transition(float speed) {
    this.posX = new Animate(0, speed);
    this.posY = new Animate(0, speed);
  }

  public Transition(float speed, int centerX, int centerY) {
    this.posX = new Animate(0, speed);
    this.posY = new Animate(0, speed);
    this.centerX = centerX;
    this.centerY = centerY;
  }

  public Transition scale(boolean b) {
    scale = b;
    return this;
  }

  public Transition rotate(boolean b) {
    rotate = b;
    return this;
  }

  public void scale() {
    ScaledResolution sr = new ScaledResolution(MC);
    if (rotate) {
      if (centerX == 0 && centerY == 0) {
        GL11.glTranslated((sr.getScaledWidth() / 2f), (sr.getScaledHeight() / 2f), 1);
        GL11.glRotated(360 * posX.getValue(), 0, 0, 1);
        GL11.glTranslated(-(sr.getScaledWidth() / 2f), -(sr.getScaledHeight() / 2f), 1);
      } else {
        GL11.glTranslated(centerX, centerY, 1);
        GL11.glRotated(360 * posX.getValue(), 0, 0, 1);
        GL11.glTranslated(-centerX, -centerY, 1);
      }
    }

    if (scale) {
      if (centerX == 0 && centerY == 0)
        GL11.glTranslated(
            (sr.getScaledWidth() / 2f) * (1 - posX.getValue()),
            (sr.getScaledHeight() / 2f) * (1 - posY.getValue()),
            1);
      else
        GL11.glTranslated((centerX) * (1 - posX.getValue()), (centerY) * (1 - posY.getValue()), 1);
      GL11.glScaled(posX.getValue(), posX.getValue(), 1);
    }
  }

  public void setValue(double value) {
    posX.setValue(value);
    posY.setValue(value);
  }

  public void transitionIn() {
    posX.interpolate(1);
    posY.interpolate(1);
  }

  public void transitionOut() {
    posX.interpolate(0);
    posY.interpolate(0);
  }

}
