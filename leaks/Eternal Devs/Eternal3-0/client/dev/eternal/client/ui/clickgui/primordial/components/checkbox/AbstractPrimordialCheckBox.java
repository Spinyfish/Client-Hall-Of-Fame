package dev.eternal.client.ui.clickgui.primordial.components.checkbox;

import dev.eternal.client.Client;
import dev.eternal.client.ui.clickgui.primordial.Component;
import dev.eternal.client.util.client.MouseUtils;
import dev.eternal.client.util.render.RenderUtil;

public abstract class AbstractPrimordialCheckBox extends Component {
  protected final String name;
  protected boolean state;
  protected double x, y;

  protected AbstractPrimordialCheckBox(String name) {
    this.name = name;
  }

  @Override
  public void render(double x, double y, int mouseX, int mouseY) {
    this.x = x;
    this.y = y;
    RenderUtil.drawRoundedRect(x, y, x + 5, y + 5, 2, 0xFF0C0C0C);
    RenderUtil.drawRoundedRect(x + 0.5, y + 0.5, x + 4.5, y + 4.5, 2, state ? Client.singleton().scheme().getPrimary() : 0xFF161616);
    RenderUtil.drawSmallString(name, x + 7, y + 0.5, -1);
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0 && MouseUtils.isInArea(mouseX, mouseY, x, y, 7 + FR.getStringWidth(name) / 2f, 6)) {
      state = !state;
      return true;
    }
    return false;
  }
}
