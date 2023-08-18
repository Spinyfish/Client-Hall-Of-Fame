/* November.lol © 2023 */
package lol.november.feature.module.gui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import lol.november.November;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.gui.panel.module.CategoryPanel;
import lol.november.feature.module.impl.visual.ClickGUIModule;
import lol.november.utility.render.component.Component;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class ClickGUIScreen extends GuiScreen {

  private final ClickGUIModule CLICK_GUI;
  private final List<Component> panels = new LinkedList<>();

  public ClickGUIScreen(ClickGUIModule module) {
    CLICK_GUI = module;

    double x = 20.0;
    for (Category category : Category.values()) {
      List<Module> categorized = November
        .instance()
        .modules()
        .values()
        .stream()
        .filter(m -> m.getCategory() == category)
        .toList();
      if (categorized.isEmpty()) continue;

      CategoryPanel categoryPanel = new CategoryPanel(category, categorized);
      categoryPanel.setX(x);
      categoryPanel.setY(30.0);
      categoryPanel.setWidth(120.0);
      categoryPanel.setHeight(16.5);

      panels.add(categoryPanel);

      x += categoryPanel.getWidth() + 4.0;
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);

    for (Component component : panels) {
      if (component instanceof CategoryPanel categoryPanel) {
        if (!categoryPanel.isInit()) categoryPanel.init();
      }

      component.draw(mouseX, mouseY, partialTicks);
    }
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);

    for (Component component : panels) {
      component.mouseClicked(mouseX, mouseY, mouseButton);
    }
  }

  @Override
  protected void mouseReleased(int mouseX, int mouseY, int state) {
    super.mouseReleased(mouseX, mouseY, state);

    for (Component component : panels) {
      component.mouseReleased(mouseX, mouseY, state);
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    super.keyTyped(typedChar, keyCode);

    for (Component component : panels) {
      component.keyTyped(typedChar, keyCode);
    }
  }

  @SneakyThrows
  @Override
  public void onGuiClosed() {
    super.onGuiClosed();
    CLICK_GUI.setState(false);
    November.instance().modules().getConfigs().saveDefault();
  }
}
