package dev.eternal.client.ui.clickgui.hackinglord.component.pane;

import dev.eternal.client.Client;
import dev.eternal.client.config.Config;
import dev.eternal.client.config.ConfigManager;
import dev.eternal.client.ui.clickgui.hackinglord.HLClickGui;
import dev.eternal.client.ui.clickgui.hackinglord.panel.HLPanel;
import dev.eternal.client.ui.notification.Notification;
import dev.eternal.client.ui.notification.NotificationManager;
import dev.eternal.client.ui.notification.NotificationType;
import dev.eternal.client.util.client.MouseUtils;

public class HLConfigPane extends HLPane {

  private double x, y;
  private final Config config;

  public HLConfigPane(Config config, HLPanel hlPanel) {
    super(hlPanel, config.name());
    this.config = config;
  }

  @Override
  public void drawPane(double x, double y, int mouseX, int mouseY) {
    this.x = x;
    this.y = y;
    fr.drawString(name, x + 2, y + 2, -1);
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (MouseUtils.isInArea(mouseX, mouseY, x, y, HLClickGui.WIDTH, getHeight())) {
      NotificationManager.pushNotification(new Notification("Config", String.format("Loaded config %s.", config.name()), 5000, NotificationType.INFO));
      ConfigManager.loadConfig(config);
      return true;
    }
    return false;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

  }

  @Override
  public double getHeight() {
    return 12;
  }
}
