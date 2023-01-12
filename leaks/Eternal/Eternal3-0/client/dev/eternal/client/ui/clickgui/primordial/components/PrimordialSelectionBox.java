package dev.eternal.client.ui.clickgui.primordial.components;

import dev.eternal.client.property.impl.ModeSetting;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.ui.clickgui.primordial.Component;
import dev.eternal.client.util.client.MouseUtils;
import dev.eternal.client.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

public class PrimordialSelectionBox extends Component {

  private boolean extended;
  private double x, y;

  private final ModeSetting modeSetting;

  public PrimordialSelectionBox(ModeSetting modeSetting) {
    this.setting = this.modeSetting = modeSetting;
  }

  @Override
  public void render(double x, double y, int mouseX, int mouseY) {
    this.x = x;
    this.y = y;
    RenderUtil.drawSmallString(modeSetting.name(), x, y, -1);
    float offset = 7;
    Gui.drawRect(x, y + offset, x + 144, y + 9 + offset, 0xFF282828);
    Gui.drawRect(x + 0.5, y + 0.5 + offset, x + 143.5, y + 8.5 + offset, 0xFF161616);
    RenderUtil.drawSmallString(modeSetting.value().name(), x + 1.5, y + offset + 2.5, -1);

    if (extended) {
      GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
      GL11.glPolygonOffset(1.0F, -6900000.0f);

      Gui.drawRect(x, y + 16, x + 144, y + 16 + modeSetting.modeList().size() * 8, 0xFF282828);
      Gui.drawRect(x + 0.5, y + 16.5, x + 143.5, y + 15.5 + modeSetting.modeList().size() * 8, 0xFF161616);

      int itr = 0;
      for (Mode s : modeSetting.modeList()) {
        RenderUtil.drawSmallString(s.name(), x + 1.5, y + 18.5 + itr * 8, -1);
        itr++;
      }

      GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
      GL11.glPolygonOffset(1.0F, 6900000.0f);
    }
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0) {
      if (MouseUtils.isInArea(mouseX, mouseY, x, y + 7, 144, 9)) {
        extended = !extended;
        return true;
      }
      if (extended) {
        int itr = 0;
        for (Mode s : modeSetting.modeList()) {
          if (MouseUtils.isInArea(mouseX, mouseY, x + 1.5, y + 18.5 + itr * 8, 142, 9)) {
            modeSetting.value(s);
            return true;
          }
          itr++;
        }
      }
    }
    return false;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY) {

  }

  @Override
  public float offset() {
    return 20;
  }
}
