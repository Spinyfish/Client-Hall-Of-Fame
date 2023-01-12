package dev.eternal.client.ui.clickgui.primordial.frames;

import dev.eternal.client.Client;
import dev.eternal.client.module.Module;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.ModeSetting;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.ui.clickgui.primordial.Component;
import dev.eternal.client.ui.clickgui.primordial.components.PrimordialSelectionBox;
import dev.eternal.client.ui.clickgui.primordial.components.PrimordialSlider;
import dev.eternal.client.ui.clickgui.primordial.components.checkbox.PrimordialModuleCheckBox;
import dev.eternal.client.ui.clickgui.primordial.components.checkbox.PrimordialSettingCheckBox;
import dev.eternal.client.util.render.RenderUtil;

import java.util.ArrayList;
import java.util.List;

public class ModulePrimordialFrame extends AbstractPrimordialFrame {

  private final Module module;
  private final List<Component> componentList = new ArrayList<>();
  private final PrimordialModuleCheckBox moduleCheckBox;

  public ModulePrimordialFrame(Module module) {
    this.module = module;
    this.moduleCheckBox = new PrimordialModuleCheckBox(module);
    Client.singleton().propertyManager().getStream(module)
        .forEach(setting -> {
          if (setting instanceof BooleanSetting booleanSetting)
            componentList.add(new PrimordialSettingCheckBox(booleanSetting));
          if (setting instanceof NumberSetting numberSetting) componentList.add(new PrimordialSlider(numberSetting));
          if (setting instanceof ModeSetting modeSetting) componentList.add(new PrimordialSelectionBox(modeSetting));
        });
  }

  @Override
  public void draw(int mouseX, int mouseY, double x, double y) {
    double height = componentList.stream().filter(this::nonBlacklisted).mapToDouble(Component::offset).sum() + 14;
    RenderUtil.drawRoundedRect(x + 1, y + 1, x + 163.5, y + height + 0.5, 12, 0xFF0C0C0C);
    RenderUtil.drawRoundedRect(x, y, x + 163, y + height, 12, 0xFF1F1F1F);
//    RenderUtil.drawSmallString(module.getModuleInfo().name(), x + 4, y + 4, -1);
    moduleCheckBox.render(x + 4, y + 4, mouseX, mouseY);

    double itr = 0;
    for (Component component : componentList) {
      if (blacklisted(component)) continue;
      component.render(x + 7, y + 14 + itr, mouseX, mouseY);
      itr += component.offset();
    }
  }

  public boolean blacklisted(Component c) {
    return false;
//    if(c.getSetting() == null) return false;
//    if(c.getSetting().parent() != null) {
//      if (c.getSetting().parent() instanceof SettingCheckBox) {
//        return !(Boolean) c.getSetting().parent().getValue();
//      } else if (c.getSetting().parent() instanceof SettingMode) {
//        if(c.getSetting().parent() != null) {
//          Property<?> set = c.getSetting().parent();
//          if (set.parent() != null) {
//            if (set.parent() instanceof SettingMode) {
//              if (!((SettingMode) set.parent()).getValue().equals(set.parentMode())) {
//                return true;
//              }
//            }
//          }
//        }
//        return !c.setting().parent().getValue().equals(c.setting().parentMode());
//      }
//    }
//    return false;
  }


  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (moduleCheckBox.mouseClicked(mouseX, mouseY, mouseButton)) return;
    for (Component component : componentList) {
      if (component.mouseClicked(mouseX, mouseY, mouseButton)) break;
    }
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    componentList.forEach(component -> {
      if (!blacklisted(component)) component.mouseReleased(mouseX, mouseY);
    });
  }

  public boolean nonBlacklisted(Component component) {
    return !blacklisted(component);
  }

  @Override
  public double getHeight() {
    return componentList.stream().filter(this::nonBlacklisted).mapToDouble(Component::offset).sum() + 14;
  }
}
