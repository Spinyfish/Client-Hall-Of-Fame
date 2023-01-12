package dev.eternal.client.ui.clickgui.eternal.component;

import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.ui.clickgui.eternal.EternalClickGui;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public abstract class Component {

  private double x, y;

  public static EternalClickGui clickGUI;
  public static final TrueTypeFontRenderer iCiel = FontManager.getFontRenderer(FontType.ICIEL, 16);

  public abstract void draw(int mouseX, int mouseY, float partialTicks);

  public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);

  public void mouseReleased(int mouseX, int mouseY, int state) {
  }

  public void keyPress(char typedChar, int keyPressed) {

  }
}
