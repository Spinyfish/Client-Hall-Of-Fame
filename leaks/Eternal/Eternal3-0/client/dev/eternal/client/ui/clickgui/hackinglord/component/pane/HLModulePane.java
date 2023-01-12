package dev.eternal.client.ui.clickgui.hackinglord.component.pane;

import dev.eternal.client.Client;
import dev.eternal.client.module.Module;
import dev.eternal.client.property.Property;
import dev.eternal.client.property.impl.*;
import dev.eternal.client.ui.clickgui.hackinglord.component.settings.*;
import dev.eternal.client.ui.clickgui.hackinglord.panel.HLPanel;
import dev.eternal.client.util.animate.Animate;
import dev.eternal.client.util.client.MouseUtils;
import dev.eternal.client.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import scheme.Scheme;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HLModulePane extends HLPane {

  private final Animate animate = new Animate(0, 0.7F);
  private double x, y;

  private final Module module;
  private final List<HLSetting> settingBaseList = new ArrayList<>();

  public HLModulePane(Module module, HLPanel hlPanel) {
    super(hlPanel, module.moduleInfo().name());
    this.module = module;
    Client.singleton().propertyManager()
        .stream()
        .filter(property -> property.owner().equals(module))
        .sorted(Comparator.comparingInt(value -> value.propertyType().ordinal()))
        .forEach(this::addSettings);
  }

  private void addSettings(Property<?> property) {
    if (property instanceof BooleanSetting booleanSetting)
      settingBaseList.add(new HLCheckBox(booleanSetting));
    if (property instanceof NumberSetting numberSetting)
      settingBaseList.add(new HLSlider(numberSetting));
    if (property instanceof ModeSetting modeSetting) {
      settingBaseList.add(new HLModeSelector(modeSetting));
      modeSetting.modeList().forEach(mode -> Client.singleton().propertyManager().getStream(mode).forEach(this::addSettings));
    }
    if (property instanceof EnumSetting enumSetting)
      settingBaseList.add(new HLEnumSelector(enumSetting));
    if(property instanceof TextSetting textSetting)
      settingBaseList.add(new HLTextBox(textSetting));
  }

  private boolean blackListed(Property<?> property) {
    return !property.visible().getAsBoolean();
  }

  @Override
  public void drawPane(double x, double y, int mouseX, int mouseY) {
    this.x = x;
    this.y = y;
    final Scheme scheme = Client.singleton().scheme();
    fr.drawString(name, x + 2, y + 3, scheme.getOnBackground());

    animate.interpolate(module.isEnabled() ? 84.5 : 0);

    if (animate.getValue() > 0) {
//      GL11.glEnable(3089);
      RenderUtil.prepareScissorBox(x + 0.5, hlPanel.y() + 12, x + animate.getValue(), hlPanel.y() + hlPanel.getHeight());
      Gui.drawRect(x + 0.5, y, x + 84.5, y + 12, scheme.getPrimary());

      fr.drawString(name, x + 2, y + 3, scheme.getOnSecondary());
//      GL11.glDisable(3089);
    }

    RenderUtil.prepareScissorBox(x, hlPanel.y() + 12, x + hlPanel.width(), hlPanel.y() + hlPanel.getHeight());
    if (extended) {
      double offset = 12;
      for (HLSetting settingBase : settingBaseList) {
        if (blackListed(settingBase.property())) continue;
        settingBase.drawSetting(x, y + offset, mouseX, mouseY);
        offset += settingBase.getHeight();
      }
    }
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (MouseUtils.isInArea(mouseX, mouseY, x, y, 85, 12)) {
      switch (mouseButton) {
        case 0 -> {
          module.toggle();
          return true;
        }
        case 1 -> {
          return extended = !extended;
        }
      }
    }

    if (extended) {
      for (HLSetting setting : settingBaseList) {
        if (blackListed(setting.property())) continue;
        if (setting.mouseClicked(mouseX, mouseY, mouseButton)) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    if (extended) {
      for (HLSetting setting : settingBaseList) {
        if (blackListed(setting.property())) continue;
        setting.mouseReleased(mouseX, mouseY, mouseButton);
      }
    }
  }

  //kala code (sorry)
  @Override
  public void keyTyped(char typedChar, int keyCode) {
    settingBaseList.forEach(setting -> {
      if(blackListed(setting.property()))
        return;
      setting.keyTyped(typedChar, keyCode);
    });
  }

  private boolean nonBlacklisted(Property<?> property) {
    return !blackListed(property);
  }

  @Override
  public double getHeight() {
    double returnValue = 12;
    if (extended) returnValue += settingBaseList.stream()
        .filter(hlSetting -> nonBlacklisted(hlSetting.property()))
        .mapToDouble(HLSetting::getHeight)
        .sum();
    return returnValue;
  }
}
