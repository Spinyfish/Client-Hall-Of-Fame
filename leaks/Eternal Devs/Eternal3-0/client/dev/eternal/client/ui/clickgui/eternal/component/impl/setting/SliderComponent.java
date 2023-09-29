package dev.eternal.client.ui.clickgui.eternal.component.impl.setting;

import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.module.Module;
import dev.eternal.client.property.Property;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.ui.clickgui.eternal.EternalClickGui;
import dev.eternal.client.ui.clickgui.eternal.component.impl.SettingComponent;
import dev.eternal.client.util.math.MathUtil;
import dev.eternal.client.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class SliderComponent extends SettingComponent {

  private boolean sliding = false;
  private final double x, y;
  private final TrueTypeFontRenderer smalliCiel = FontManager.getFontRenderer(FontType.ICIEL, 10);

  public SliderComponent(double x, double y, Module module, Property<?> property) {
    super(x, y, module, property);
    this.x = x;
    this.y = y;
  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTicks) {
    NumberSetting numberSetting = this.getProperty();
        /*
        background
         */
    Gui.drawRect(x(), y(), x() + 125, y() + 16, new Color(0x77000000, true).getRGB());
        /*
        actual slider
         */
    double startPos = x() + 3.5;
    double endPos = x() + 122.5;
    double diff = endPos - startPos;
    double renderVal = MathUtil.roundToPlace((numberSetting.value() - numberSetting.min()) / (numberSetting.max() - numberSetting.min()) * diff, 2);
    double renderValStr = MathUtil.roundToPlace(numberSetting.value(), 2);

    Gui.drawRect(x() + 3.5, y() + 8, x() + 122.5, y() + 16 - 2.5, new Color(0x494949).getRGB());
    if (sliding) {
      RenderUtil.drawGradientRect(x() + 3.5, y() + 8, renderVal, 5.5, EternalClickGui.color2.brighter(), EternalClickGui.color1.brighter());
    } else {
      RenderUtil.drawGradientRect(x() + 3.5, y() + 8, renderVal, 5.5, EternalClickGui.color2, EternalClickGui.color1);
    }

    if (sliding) {
      double length = endPos - mouseX;
      double value = numberSetting.max() - ((diff * numberSetting.min()) - (length * numberSetting.min()) + (length * numberSetting.max())) / diff + numberSetting.min();
      numberSetting.setValue(value - value % numberSetting.increment());
    }

        /*
        name&value
         */
    smalliCiel.drawStringWithShadow(numberSetting.name(), x() + 2.5, y() + 4, -1);
    smalliCiel.drawStringWithShadow(Double.toString(renderValStr), x() + 125 - smalliCiel.getWidth(Double.toString(renderValStr)), y() + 4, -1);
  }


  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0 && hovered(mouseX, mouseY))
      sliding = true;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0)
      sliding = false;
  }

  public boolean hovered(int mouseX, int mouseY) {
    return (mouseX >= x() + 3.5) && (mouseY >= y() + 8) && (mouseX <= x() + 122.5) && (mouseY <= y() + 16 - 2.5);
  }
}
