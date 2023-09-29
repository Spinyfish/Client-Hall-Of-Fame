package dev.eternal.client.ui.clickgui.eternal.component.impl;

import dev.eternal.client.Client;
import dev.eternal.client.module.Module;
import dev.eternal.client.property.Property;
import dev.eternal.client.property.impl.*;
import dev.eternal.client.ui.clickgui.eternal.EternalClickGui;
import dev.eternal.client.ui.clickgui.eternal.component.Component;
import dev.eternal.client.ui.clickgui.eternal.component.impl.setting.TextComponent;
import dev.eternal.client.ui.clickgui.eternal.component.impl.setting.*;
import dev.eternal.client.util.math.MathUtil;
import dev.eternal.client.util.render.RenderUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.minecraft.client.gui.Gui.drawRect;

@Getter
@Setter
public class ModuleComponent extends Component {

  private final CategoryComponent parent;
  private final Module module;

  private final List<SettingComponent> settingComponentList = new ArrayList<>();

  private double expandY;

  private long hoverStartTime, hoverStopTime;
  private boolean lastHover, expanded, binding;

  public ModuleComponent(Module module, CategoryComponent parent) {
    super(0, 0);
    this.module = module;
    this.parent = parent;
    var y = parent().moduleHeight;
    for (Property<?> property : Client.singleton().propertyManager().get(module)) {
      if (property instanceof TextSetting textSetting)
        settingComponentList.add(new TextComponent(x(), y, module, textSetting));
      if (property instanceof BooleanSetting booleanSetting)
        settingComponentList.add(new BooleanComponent(x(), y, module, booleanSetting));
      if (property instanceof EnumSetting<?> enumComponent)
        settingComponentList.add(new ModeEnumComponent(x(), y, module, enumComponent));
      if (property instanceof ModeSetting modeSetting)
        settingComponentList.add(new ModeComponent(x(), y, module, modeSetting));
      if (property instanceof NumberSetting numberSetting)
        settingComponentList.add(new SliderComponent(x(), y, module, numberSetting));
      y += 16;
    }
    settingComponentList.sort(Comparator.comparingInt(value -> value.getClass().hashCode())); //temp sorting method
    expandY(expandY);
  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTicks) {
    // Draw module background
    if (module.isEnabled())
      RenderUtil.drawGradientRect((float) x(), (float) y(), parent.moduleWidth, parent.moduleHeight, EternalClickGui.color1, EternalClickGui.color2);
    else
      drawRect(x(), y(), x() + parent.moduleWidth, y() + parent.moduleHeight, EternalClickGui.moduleBackground.getRGB());

    // Draw module colour mod
    boolean hovered = hovered(mouseX, mouseY);

    if (hovered != lastHover) {
      if (hovered) hoverStartTime = System.currentTimeMillis();
      else hoverStopTime = System.currentTimeMillis();

      lastHover = !lastHover;
    }

    final double hoverProgress = Math.min(1, (System.currentTimeMillis() - Math.max(hoverStartTime, hoverStopTime)) / 250D);
    final Color moduleColor = new Color(1, 1, 1, (float) MathUtil.lerp(0, module.isEnabled() ? 50 : 15, hovered ? hoverProgress : 1 - hoverProgress) / 255F);

    drawRect(x(), y(), x() + parent.moduleWidth, y() + parent.moduleHeight, moduleColor.getRGB());
    iCiel.drawString(module.moduleInfo().name(), x() + 8, y() + 5.5, -1);

    String key = String.format("[%s]", binding() ? "..." : Keyboard.getKeyName(module.keyBind()));

    iCiel.drawString(key, x() + parent.moduleWidth - iCiel.getWidth(key), y() + 5.5, -1);

    double itr = parent().moduleHeight;
    if (this.expanded()) {
      for (SettingComponent component : settingComponentList) {
        component.x(x());
        component.y(y() + itr);
        component.draw(mouseX, mouseY, partialTicks);
        itr += parent().moduleHeight;
      }
    }
    ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    if (hovered(mouseX, mouseY)) {
      Gui.drawRect(0, sr.getScaledHeight() - 10, iCiel.getWidth(module.moduleInfo().description()), sr.getScaledHeight(), 0xB5111111);
      iCiel.drawStringWithShadow(module.moduleInfo().description(), 0, sr.getScaledHeight() - 7.5, -1);
    }
    // Draw arrow
    drawArrow();
  }

  @Override
  public void keyPress(char typedChar, int keyPressed) {
    if (this.binding()) {
      module.keyBind(keyPressed == 1 ? 0 : keyPressed);
      Client.singleton().displayMessage(String.format("Bound %s to: %s", module.moduleInfo().name(),
          Keyboard.getKeyName(module.keyBind())));
      binding(false);
    }
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (hovered(mouseX, mouseY)) {
      switch (mouseButton) {
        case 0 -> module.toggle();
        case 1 -> expanded(!expanded());
        case 2 -> binding(!binding());
      }
    }
    if (expanded)
      settingComponentList.forEach(comp -> comp.mouseClicked(mouseX, mouseY, mouseButton));
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    if (expanded)
      settingComponentList.forEach(comp -> comp.mouseReleased(mouseX, mouseY, mouseButton));
  }

  private void drawArrow() {
  }

  private boolean hovered(int mouseX, int mouseY) {
    return x() < mouseX && mouseX < x() + parent.moduleWidth && y() < mouseY && mouseY < y() + parent.moduleHeight;
  }
}
