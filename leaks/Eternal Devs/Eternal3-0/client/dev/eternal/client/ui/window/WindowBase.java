package dev.eternal.client.ui.window;

import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.ui.window.components.WindowComponent;
import dev.eternal.client.util.client.MouseUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

/**
 * Window class used as the base class
 * of the Windows in the window-manager.
 *
 * @author Eternal
 */
@Getter
@Setter
public class WindowBase {

  protected String name;
  protected double width, height;
  protected double xPos, yPos;
  private int prevX, prevY;
  private boolean dragging;

  protected final List<WindowComponent> components = new ArrayList<>();

  protected final Minecraft mc = Minecraft.getMinecraft();
  protected final TrueTypeFontRenderer fr = FontManager.getFontRenderer(FontType.ICIEL, 18);

  public void render(int mouseX, int mouseY) {
    drawWindowOutline();

    if (dragging) {
      xPos += (mouseX - prevX);
      yPos += (mouseY - prevY);
    }

    prevX = mouseX;
    prevY = mouseY;
  }

  protected void drawWindowOutline() {
    //Background
    Gui.drawRect(xPos, yPos - 10,
        xPos + width,
        yPos + height,
        0xFF000000);
    Gui.drawRect(xPos + 0.5, yPos + 0.5,
        xPos + width - 0.5,
        yPos + height - 0.5,
        0xFF0C0C0C);

    //top thing
    Gui.drawRect(xPos + 0.5, yPos - 9.5,
        xPos + width - 0.5,
        yPos, 0xFFAA002C);

    fr.drawStringWithShadow(name, xPos + 1, yPos - 7.5, -1);
  }

  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (MouseUtils.isInArea(mouseX, mouseY, xPos, yPos - 10, width, 10))
      dragging = true;
  }

  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    dragging = false;
  }

}
