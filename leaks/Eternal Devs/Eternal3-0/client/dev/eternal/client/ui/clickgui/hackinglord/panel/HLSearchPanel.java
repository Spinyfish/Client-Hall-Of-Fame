package dev.eternal.client.ui.clickgui.hackinglord.panel;

import dev.eternal.client.ui.clickgui.hackinglord.HLClickGui;
import dev.eternal.client.ui.clickgui.hackinglord.component.pane.HLTextPane;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.StringUtils;

public class HLSearchPanel extends HLPanel {
  public HLSearchPanel(HLClickGui hlClickGui) {
    super(hlClickGui);
    name("SEARCH");
    this.x = 5;
    this.y = new ScaledResolution(mc()).getScaledHeight() - 12;
    this.panes.add(new HLTextPane(this));
  }

  @Override
  public void drawPanel(int mouseX, int mouseY) {
    super.render(mouseX, mouseY);
  }
}
