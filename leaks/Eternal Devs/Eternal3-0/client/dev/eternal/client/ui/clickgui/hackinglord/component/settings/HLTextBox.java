package dev.eternal.client.ui.clickgui.hackinglord.component.settings;

import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.TextSetting;
import dev.eternal.client.ui.clickgui.hackinglord.panel.HLCategoryPanel;
import dev.eternal.client.ui.clickgui.hackinglord.panel.HLPanel;
import dev.eternal.client.util.client.MouseUtils;
import dev.eternal.client.util.render.MouseUtil;
import dev.eternal.client.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import scheme.Scheme;

import java.util.List;

public class HLTextBox extends HLSetting {

  private double x, y;
  private final TextSetting textSetting;
  private final GuiTextField textField = new GuiTextField(69420, Minecraft.getMinecraft().fontRendererObj, 0, 0, 0, 0);
  private boolean focused = false;

  public HLTextBox(TextSetting textSetting) {
    super(textSetting);
    this.textSetting = textSetting;
  }

  @Override
  public void drawSetting(double x, double y, int mouseX, int mouseY) {
    this.x = x;
    this.y = y;
    final Scheme scheme = Client.singleton().scheme();
    final TrueTypeFontRenderer small = FontManager.getFontRenderer(FontType.ROBOTO_REGULAR, 12);
    Gui.drawRect(x + 0.5, y, x + 84.5, y + getHeight(), scheme.getSurface());
    final float width = fr.getWidth(textField.getText().substring(textField.getText().length() - textField.lineScrollOffset));
    if (focused && (System.currentTimeMillis() % 1000) / 500 > 0) {
      Gui.drawRect(x + width, y + 1, x + 1 + width, y + 9, -1);
    }
    Gui.drawRect(x + 2, y + 10, x + 83, y + 11, Client.singleton().scheme().getOnBackground());
    fr.drawString(textField.getText(), x + 2, y + 2, -1);
    textSetting.value(textField.getText());
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    return focused = MouseUtils.isInArea(mouseX, mouseY, x, y, 85, 12);
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

  }

  @Override
  public void keyTyped(char typedChar, int keyCode) {
    textField.setFocused(true);
    if (focused) textField.textboxKeyTyped(typedChar, keyCode);
  }

  @Override
  public double getHeight() {
    return 13;
  }
}
