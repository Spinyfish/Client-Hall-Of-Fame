package dev.eternal.client.ui.clickgui.primordial.frames;

public abstract class AbstractPrimordialFrame {

  public abstract void draw(int mouseX, int mouseY, double x, double y);

  public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);

  public abstract void mouseReleased(int mouseX, int mouseY, int mouseButton);

  public abstract double getHeight();

}
