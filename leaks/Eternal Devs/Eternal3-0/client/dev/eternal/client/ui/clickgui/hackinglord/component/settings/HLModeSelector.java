package dev.eternal.client.ui.clickgui.hackinglord.component.settings;

import dev.eternal.client.Client;
import dev.eternal.client.property.impl.ModeSetting;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.client.MouseUtils;
import net.minecraft.client.gui.Gui;

import java.util.List;

public class HLModeSelector extends HLSetting {

  private final ModeSetting modeSetting;

  public HLModeSelector(ModeSetting modeSetting) {
    super(modeSetting);
    this.modeSetting = modeSetting;
  }

  @Override
  public void drawSetting(double x, double y, int mouseX, int mouseY) {
    this.x = x;
    this.y = y;
    Gui.drawRect(x + 0.5, y, x + 84.5, y + getHeight(), Client.singleton().scheme().getSurface());
    fr.drawString(String.format("%s: %s", modeSetting.name(), modeSetting.value().name()), x + 2, y + 3, -1);
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (MouseUtils.isInArea(mouseX, mouseY, x, y, 85, getHeight())) {
      List<Mode> modeList = modeSetting.modeList();
      int index = modeList.indexOf(modeSetting.value()) + (mouseButton == 0 ? 1 : -1);
      modeSetting.value(index > modeList.size() - 1 ? modeList.get(0) : index <= 0 ? modeList.get(modeList.size() - 1) : modeList.get(index));
      return true;
    }
    return false;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

  }

  @Override
  public double getHeight() {
    return 12;
  }
}
