package dev.eternal.client.ui.clickgui.hackinglord.component.settings;

import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.util.animate.Animate;
import dev.eternal.client.util.client.MouseUtils;
import dev.eternal.client.util.math.MathUtil;
import dev.eternal.client.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import scheme.Scheme;

public class HLSlider extends HLSetting {

  private double x, y;
  private boolean dragging;
  private final Animate animate = new Animate(0, 0.7F);
  private final NumberSetting numberSetting;

  public HLSlider(NumberSetting numberSetting) {
    super(numberSetting);
    this.numberSetting = numberSetting;
  }

  @Override
  public void drawSetting(double x, double y, int mouseX, int mouseY) {
    this.x = x;
    this.y = y;
    final Scheme scheme = Client.singleton().scheme();
    final TrueTypeFontRenderer small = FontManager.getFontRenderer(FontType.ROBOTO_REGULAR, 12);
    final double amount = ((numberSetting.value() - numberSetting.min()) / (numberSetting.max() - numberSetting.min())) * 80;
    Gui.drawRect(x + 0.5, y, x + 84.5, y + getHeight(), scheme.getSurface());

    animate.interpolate(amount);

    small.drawString(numberSetting.name(), x + 2, y + 2, -1);
    String val = "" + (MathUtil.roundToPlace(numberSetting.value(), 2));
    small.drawString(val,
        x + 85 - small.getWidth(val),
        y + 2, -1);

    RenderUtil.drawRoundedRect(x + 2.5, y + 9, x + 82.5, y + 13, 1, 0xFF393939);
    RenderUtil.drawRoundedRect(x + 2.5, y + 9, x + 2.5 + animate.getValue(), y + 13, 1, scheme.getPrimary());

    if (dragging) {
      double offset = (numberSetting.max() - numberSetting.min());
      double diff = mouseX - x;
      numberSetting.setValue(MathUtil.round((diff / 80) * offset + numberSetting.min(), numberSetting.increment()));
    }
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (MouseUtils.isInArea(mouseX, mouseY, x + 2.5, y + 7, 80, 7)) {
      return dragging = true;
    }
    return false;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    dragging = false;
  }

  @Override
  public double getHeight() {
    return 15;
  }
}
