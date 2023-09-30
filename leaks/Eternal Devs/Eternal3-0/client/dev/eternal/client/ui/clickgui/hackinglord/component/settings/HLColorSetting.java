package dev.eternal.client.ui.clickgui.hackinglord.component.settings;

import dev.eternal.client.property.impl.ColorSetting;

public class HLColorSetting extends HLSetting {

  private final ColorSetting colorSetting;

  public HLColorSetting(ColorSetting colorSetting) {
    super(colorSetting);
    this.colorSetting = colorSetting;
  }

  @Override
  public void drawSetting(double x, double y, int mouseX, int mouseY) {
    fr.drawStringWithShadow(colorSetting.name(), x, y, -1);
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    return false;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

  }

  @Override
  public double getHeight() {
    return 0;
  }
}
