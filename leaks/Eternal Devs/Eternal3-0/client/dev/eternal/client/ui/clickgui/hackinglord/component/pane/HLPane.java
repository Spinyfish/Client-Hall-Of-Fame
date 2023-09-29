package dev.eternal.client.ui.clickgui.hackinglord.component.pane;

import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.ui.clickgui.hackinglord.panel.HLPanel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class HLPane {

  protected final HLPanel hlPanel;
  protected final String name;
  protected boolean extended, visible = true;
  protected final TrueTypeFontRenderer fr = FontManager.getFontRenderer(FontType.ROBOTO_MEDIUM, 16);

  public abstract void drawPane(double x, double y, int mouseX, int mouseY);

  public abstract boolean mouseClicked(int mouseX, int mouseY, int mouseButton);

  public abstract void mouseReleased(int mouseX, int mouseY, int mouseButton);

  public void keyTyped(char typedChar, int keyCode) {
  }

  public abstract double getHeight();

}