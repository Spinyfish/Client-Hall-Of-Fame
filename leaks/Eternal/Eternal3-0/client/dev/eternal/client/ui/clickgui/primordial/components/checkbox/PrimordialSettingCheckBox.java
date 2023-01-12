package dev.eternal.client.ui.clickgui.primordial.components.checkbox;

import dev.eternal.client.property.impl.BooleanSetting;

public class PrimordialSettingCheckBox extends AbstractPrimordialCheckBox {

  private final BooleanSetting booleanSetting;

  public PrimordialSettingCheckBox(BooleanSetting booleanSetting) {
    super(booleanSetting.name());
    this.state = booleanSetting.value();
    this.setting = this.booleanSetting = booleanSetting;
  }

  @Override
  public void render(double x, double y, int mouseX, int mouseY) {
    super.render(x, y, mouseX, mouseY);
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    boolean b = super.mouseClicked(mouseX, mouseY, mouseButton);
    booleanSetting.value(super.state);
    return b;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY) {

  }
}
