package dev.eternal.client.ui.clickgui.primordial.components;

import dev.eternal.client.Client;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.ui.clickgui.primordial.Component;
import dev.eternal.client.util.animate.Animate;
import dev.eternal.client.util.client.MouseUtils;
import dev.eternal.client.util.render.RenderUtil;
import net.minecraft.util.MathHelper;

public class PrimordialSlider extends Component {

  private boolean dragging;
  private double x, y;

  private final NumberSetting valueSetting;
  private final Animate animate = new Animate(0, 1);

  public PrimordialSlider(NumberSetting valueSetting) {
    this.setting = this.valueSetting = valueSetting;
  }

  @Override
  public void render(double x, double y, int mouseX, int mouseY) {
    this.x = x;
    this.y = y;
    double d = (144 * (valueSetting.value() - valueSetting.min()) / (valueSetting.max() - valueSetting.min()));
    animate.interpolate((float) d);
    RenderUtil.drawSmallString(valueSetting.name() + " " + valueSetting.value(), x, y, -1);
    float offset = 7;
//    RenderUtil.drawLine(x, y + offset, 143, 5, 0xFF0C0C0C);
//    RenderUtil.drawLine(x, y + offset + 1, animate.getValue(), 3, 0xFFD59EAF);
    RenderUtil.drawRoundedRect(x, y + offset, x + 143, y + 5 + offset, 4, 0xFF0C0C0C);
    RenderUtil.drawRoundedRect(x + 0.5, y + 0.5 + offset, x + animate.getValueF(), y + 4.5 + offset, 4, Client.singleton().scheme().getPrimary());
    if (dragging) {
      double off = (valueSetting.max() - valueSetting.min());
      double diff = mouseX - x;
      valueSetting.setValue(((diff / 144) * off + valueSetting.min()));
      valueSetting.setValue(MathHelper.clamp_double(valueSetting.value(), valueSetting.min(), valueSetting.max()));
    }
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (MouseUtils.isInArea(mouseX, mouseY, x, y + 7, 144, 8)) {
      dragging = true;
    }
    return dragging;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY) {
    dragging = false;
  }

  @Override
  public float offset() {
    return 16;
  }
}
