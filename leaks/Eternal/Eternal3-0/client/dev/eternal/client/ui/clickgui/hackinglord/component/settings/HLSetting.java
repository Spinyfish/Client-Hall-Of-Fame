package dev.eternal.client.ui.clickgui.hackinglord.component.settings;

import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.property.Property;
import lombok.Getter;
import scheme.Scheme;

public abstract class HLSetting {

  protected double x, y;
  protected final TrueTypeFontRenderer fr = FontManager.getFontRenderer(FontType.ROBOTO_MEDIUM, 16);
  @Getter
  private final Property<?> property;

  public HLSetting(Property<?> property) {
    this.property = property;
  }

  public abstract void drawSetting(double x, double y, int mouseX, int mouseY);

  public abstract boolean mouseClicked(int mouseX, int mouseY, int mouseButton);

  public abstract void mouseReleased(int mouseX, int mouseY, int mouseButton);

  public void keyTyped(char typedChar, int keyCode) {}

  public abstract double getHeight();

}
