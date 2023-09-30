package dev.eternal.client.ui.clickgui.hackinglord.panel;

import dev.eternal.client.Client;
import dev.eternal.client.module.Module;
import dev.eternal.client.ui.clickgui.hackinglord.HLClickGui;
import dev.eternal.client.ui.clickgui.hackinglord.component.pane.HLConfigPane;

public class HLConfigPanel extends HLPanel {

  public HLConfigPanel(HLClickGui hlClickGui) {
    super(hlClickGui);
    addPanes();
    this.y = 5;
    this.x = Module.Category.values().length * 90 + 100;
    name("CONFIGS");
  }

  private void addPanes() {
    panes.clear();
    Client.singleton().configRepository().configList().stream()
        .map(config -> new HLConfigPane(config, this))
        .forEach(panes::add);
  }

  @Override
  public void drawPanel(int mouseX, int mouseY) {
    if (panes.size() != Client.singleton().configRepository().configList().size()) {
      addPanes();
    }
    render(mouseX, mouseY);
  }
}
