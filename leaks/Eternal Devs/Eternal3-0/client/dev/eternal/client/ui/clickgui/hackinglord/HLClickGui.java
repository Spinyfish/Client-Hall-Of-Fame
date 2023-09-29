package dev.eternal.client.ui.clickgui.hackinglord;

import com.google.gson.*;
import dev.eternal.client.module.Module;
import dev.eternal.client.ui.clickgui.ClickGui;
import dev.eternal.client.ui.clickgui.hackinglord.panel.HLConfigPanel;
import dev.eternal.client.ui.clickgui.hackinglord.panel.HLPanel;
import dev.eternal.client.ui.clickgui.hackinglord.panel.HLCategoryPanel;
import dev.eternal.client.ui.clickgui.hackinglord.panel.HLSearchPanel;
import dev.eternal.client.util.Savable;
import dev.eternal.client.util.files.FileUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiScreen;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// TODO: Improve this. If you're not named Eternal then don't touch this.
@Getter
public class HLClickGui extends ClickGui {

  public static final int WIDTH = 85;
  private final Gson gson = new Gson();
  private final List<HLPanel> panels = new ArrayList<>();

  public HLClickGui() {
    Arrays.stream(Module.Category.values())
        .map(category -> new HLCategoryPanel(category, this))
        .forEach(panels::add);
    panels.add(new HLConfigPanel(this));
    panels.add(new HLSearchPanel(this));
    load();
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  public void save() {
    final File configFile = FileUtils.getFileFromFolder("SaveState", "clickgui.cfg");
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    JsonObject jsonObject = new JsonObject();
    panels.forEach(
        hlPanel -> {
          JsonObject hlPanelObject = new JsonObject();
          hlPanelObject.add("x", new JsonPrimitive(hlPanel.x()));
          hlPanelObject.add("y", new JsonPrimitive(hlPanel.y()));
          JsonObject paneInfo = new JsonObject();
          hlPanel
              .panes()
              .forEach(
                  hlPane -> {
                    JsonObject hlPaneInfo = new JsonObject();
                    hlPaneInfo.add("extended", new JsonPrimitive(hlPane.extended()));
                    paneInfo.add(hlPane.name(), hlPaneInfo);
                  });
          hlPanelObject.add("paneInfo", paneInfo);
          jsonObject.add(hlPanel.name(), hlPanelObject);
        });

    FileUtils.writeToFile(configFile, Collections.singletonList(gson.toJson(jsonObject)));
  }

  @Override
  @SneakyThrows
  public void load() {
    final File configFile = FileUtils.getFileLocationFromFolder("savestate", "clickgui.cfg");
    if (!configFile.exists()) {
      save();
      return;
    }
    final JsonObject jsonObject = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
    panels.forEach(
        hlPanel -> {
          JsonObject hlPanelObject = jsonObject.getAsJsonObject(hlPanel.name());
          if (hlPanelObject == null) return;
          hlPanel.x(hlPanelObject.get("x").getAsInt());
          hlPanel.y(hlPanelObject.get("y").getAsInt());
          hlPanel
              .panes()
              .forEach(
                  hlPane -> {
                    JsonObject paneInfo = hlPanelObject.getAsJsonObject("paneInfo");
//                    hlPane.setExtended(
//                        paneInfo.getAsJsonObject(hlPane.getName()).get("extended").getAsBoolean());
                  });
        });
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    panels.forEach(abstractHLPanel -> abstractHLPanel.drawPanel(mouseX, mouseY));
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    for (HLPanel panel : panels) {
      if (panel.mouseClicked(mouseX, mouseY, mouseButton)) {
        return;
      }
    }
  }

  @Override
  protected void mouseReleased(int mouseX, int mouseY, int state) {
    panels.forEach(abstractHLPanel -> abstractHLPanel.mouseReleased(mouseX, mouseY, state));
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    panels.forEach(hlPanel -> hlPanel.keyTyped(typedChar, keyCode));
    super.keyTyped(typedChar, keyCode);
  }
}
