package dev.eternal.client.ui.clickgui.hackinglord.component.settings;

import dev.eternal.client.Client;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.util.client.MouseUtils;
import dev.eternal.client.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import scheme.Scheme;

public class HLCheckBox extends HLSetting {

  private final BooleanSetting booleanSetting;

  public HLCheckBox(BooleanSetting booleanSetting) {
    super(booleanSetting);
    this.booleanSetting = booleanSetting;
  }

  @Override
  public void drawSetting(double x, double y, int mouseX, int mouseY) {
    this.x = x;
    this.y = y;
    final Scheme scheme = Client.singleton().scheme();
    Gui.drawRect(x + 0.5, y, x + 84.5, y + getHeight(), scheme.getSurface());
    RenderUtil.drawRoundedRect(x + 2, y + 2, x + 12, y + 12, 4, booleanSetting.value() ? scheme.getPrimary() : scheme.getScrim());
    fr.drawString(booleanSetting.name(), x + 14, y + 4, -1);
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0 && MouseUtils.isInArea(mouseX, mouseY, x, y, 85, 15)) {
      booleanSetting.value(!booleanSetting.value());
      return true;
    }
    return false;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

  }

  @Override
  public double getHeight() {
    return 13;
  }
}
