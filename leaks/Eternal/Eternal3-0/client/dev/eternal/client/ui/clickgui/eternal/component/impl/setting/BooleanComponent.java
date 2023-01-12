package dev.eternal.client.ui.clickgui.eternal.component.impl.setting;

import dev.eternal.client.module.Module;
import dev.eternal.client.property.Property;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.ui.clickgui.eternal.EternalClickGui;
import dev.eternal.client.ui.clickgui.eternal.component.impl.SettingComponent;
import dev.eternal.client.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class BooleanComponent extends SettingComponent {

  private final double x, y;

  public BooleanComponent(double x, double y, Module module, Property<?> property) {
    super(x, y, module, property);
    this.x = x;
    this.y = y;
  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTicks) {
        /*
        background
         */
    Gui.drawRect(x(), y(), x() + 125, y() + 16, new Color(0x77000000, true).getRGB());
        /*
        button
         */
    if (this.<BooleanSetting>getProperty().value()) {
      RenderUtil.drawGradientRect(x() + 2.5, y() + 2.5, 10, 10, EternalClickGui.color1.darker(), EternalClickGui.color2.darker());
      RenderUtil.drawGradientRect(x() + 3.5, y() + 3.5, 8, 8, EternalClickGui.color1, EternalClickGui.color2);
    } else {
      RenderUtil.drawGradientRect(x() + 2.5, y() + 2.5, 10, 10, new Color(0x2A2A2A).darker(), new Color(0x1F1F1F).darker());
      RenderUtil.drawGradientRect(x() + 3.5, y() + 3.5, 8, 8, new Color(0x2A2A2A), new Color(0x1F1F1F));
    }
        /*
        draw string
         */
    iCiel.drawStringWithShadow(this.getProperty().name(), x() + 15, y() + 5.5, -1);
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (hovered(mouseX, mouseY) && mouseButton == 0) {
      this.<BooleanSetting>getProperty().value(!this.<BooleanSetting>getProperty().value());
    }
  }

  public boolean hovered(int mouseX, int mouseY) {
    return (mouseX >= x() + 2.5F) && (mouseY >= y() + 2.5f) && (mouseX <= x() + 10) && (mouseY <= y() + 10);
  }

}
