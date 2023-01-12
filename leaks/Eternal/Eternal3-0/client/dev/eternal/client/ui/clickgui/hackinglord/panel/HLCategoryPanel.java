package dev.eternal.client.ui.clickgui.hackinglord.panel;

import dev.eternal.client.Client;
import dev.eternal.client.module.Module;
import dev.eternal.client.ui.clickgui.hackinglord.HLClickGui;
import dev.eternal.client.ui.clickgui.hackinglord.component.pane.HLModulePane;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Getter
@Setter
public class HLCategoryPanel extends HLPanel {

  private final Module.Category category;

  public HLCategoryPanel(Module.Category category, HLClickGui hlClickGui) {
    super(hlClickGui);
    this.category = category;
    Client.singleton().moduleManager()
        .getModulesByCategory(category)
        .sorted(Comparator.comparing(module -> module.moduleInfo().name()))
        .map(module -> new HLModulePane(module, this))
        .forEach(panes::add);
    name(category.name());
    this.y = 5;
    this.x = category.ordinal() * 90 + 100;
  }

  @Override
  public void drawPanel(int mouseX, int mouseY) {
    render(mouseX, mouseY);
  }

}