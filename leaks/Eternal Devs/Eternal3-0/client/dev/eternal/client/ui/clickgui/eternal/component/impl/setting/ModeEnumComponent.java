package dev.eternal.client.ui.clickgui.eternal.component.impl.setting;

import dev.eternal.client.module.Module;
import dev.eternal.client.property.Property;
import dev.eternal.client.property.impl.EnumSetting;
import dev.eternal.client.property.impl.interfaces.INameable;
import dev.eternal.client.ui.clickgui.eternal.component.impl.SettingComponent;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.List;

public class ModeEnumComponent extends SettingComponent {

  public ModeEnumComponent(double x, double y, Module module, Property<?> property) {
    super(x, y, module, property);
  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTicks) {
    Gui.drawRect(x(), y(), x() + 125, y() + 16, new Color(0x77000000, true).getRGB());
    iCiel.drawStringWithShadow(String.format("%s : %s", this.<EnumSetting<?>>getProperty().name(), this.<EnumSetting<?>>getProperty().value().name()), x() + 2.5, y() + 5.5, -1);
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (hovered(mouseX, mouseY)) {
      EnumSetting<? extends INameable> modeSetting = this.getProperty();
      List<? extends INameable> modeList = modeSetting.enumList();
      int index = modeList.indexOf(modeSetting.value()) + (mouseButton == 0 ? 1 : -1);
      if (index < 0 || index > modeList.size() - 1) {
        modeSetting.setValue(modeList.get(0).getName());
      } else {
        modeSetting.setValue(modeList.get(index).getName());
      }
    }
  }

  public boolean hovered(int mouseX, int mouseY) {
    return (mouseX >= x()) && (mouseY >= y()) && (mouseX <= x() + 125) && (mouseY <= y() + 16);
  }
}
