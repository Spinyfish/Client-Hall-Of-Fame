package dev.eternal.client.ui.window.impl;

import dev.eternal.client.ui.window.WindowBase;

public class BasicWindow extends WindowBase {

  public BasicWindow(String name, double xPos, double yPos, double width, double height) {
    name(name);
    xPos(xPos);
    yPos(yPos);
    width(width);
    height(height);
  }
}
