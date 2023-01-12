package dev.eternal.client.ui.clickgui.hackinglord.component.pane;

import dev.eternal.client.Client;
import dev.eternal.client.ui.clickgui.hackinglord.panel.HLCategoryPanel;
import dev.eternal.client.ui.clickgui.hackinglord.panel.HLPanel;
import dev.eternal.client.util.client.MouseUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;

import java.util.List;

public class HLTextPane extends HLPane {

  private boolean focused;
  private double x, y;
  private final GuiTextField textField = new GuiTextField(69, Minecraft.getMinecraft().fontRendererObj, 0, 0, 0, 0);

  public HLTextPane(HLPanel hlPanel) {
    super(hlPanel, "Text");
  }

  @Override
  public void drawPane(double x, double y, int mouseX, int mouseY) {
    this.x = x;
    this.y = y;
    Gui.drawRect(x + 2, y + 10, x + 83, y + 11, Client.singleton().scheme().getOnBackground());
    final float width = fr.getWidth(textField.getText().substring(textField.getText().length() - textField.lineScrollOffset));
    if (focused && (System.currentTimeMillis() % 1000) / 500 > 0) {
      Gui.drawRect(x + width, y + 1, x + 1 + width, y + 9, -1);
    }
    fr.drawString(textField.getText(), x + 2, y + 2, -1);
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
    if (!textField.getText().isEmpty()) {
      hlPanel.hlClickGui().panels().stream()
          .filter(HLCategoryPanel.class::isInstance)
          .map(HLPanel::panes)
          .flatMap(List::stream)
          .forEach(hlPane -> hlPane.visible(hlPane.name().toLowerCase().contains(textField.getText().toLowerCase())));
    } else {
      hlPanel.hlClickGui().panels().stream()
          .filter(HLCategoryPanel.class::isInstance)
          .map(HLPanel::panes)
          .flatMap(List::stream)
          .forEach(hlPane -> hlPane.visible(true));
    }
  }

  @Override
  public double getHeight() {
    return 13;
  }
}
