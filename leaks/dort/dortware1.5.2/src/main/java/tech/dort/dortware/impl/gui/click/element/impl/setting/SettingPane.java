package tech.dort.dortware.impl.gui.click.element.impl.setting;

import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.property.Value;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.StringValue;
import tech.dort.dortware.impl.gui.click.element.Element;
import tech.dort.dortware.impl.gui.click.element.impl.setting.impl.EnumSetting;
import tech.dort.dortware.impl.gui.click.element.impl.setting.impl.ModeSetting;
import tech.dort.dortware.impl.gui.click.element.impl.setting.impl.SliderSetting;
import tech.dort.dortware.impl.gui.click.element.impl.setting.impl.ToggleSetting;

import java.util.ArrayList;
import java.util.List;

public class SettingPane extends Element {

    public List<Setting> getSettings() {
        return settings;
    }

    private final List<Setting> settings = new ArrayList<>();

    public SettingPane(Module module, int width) {
        this.width = width;
        for (Value<?> value : valueManager.getValuesFromOwner(module)) {
            if (value instanceof BooleanValue) {
                settings.add(new ToggleSetting(value, this.width));
            } else if (value instanceof StringValue) {
                settings.add(new ModeSetting(value, this.width));
            } else if (value instanceof NumberValue) {
                settings.add(new SliderSetting(value, this.width));
            } else if (value instanceof EnumValue) {
                settings.add(new EnumSetting(value, this.width));
            }
        }
    }

    @Override
    public void onDraw(int mouseX, int mouseY, float partialTicks) {
        int settingOffset = 0;
        for (Setting setting : settings) {
            if (setting.value.getParent() == null || (Boolean) setting.value.getParent().getValue()) {
                setting.setPos(posX, posY + settingOffset);
                setting.onDraw(mouseX, mouseY, partialTicks);
                settingOffset += setting.getHeight();
            }
        }
    }

    public int calculateHeight() {
        int settingOffset = 0;
        for (Setting setting : settings)
            if (setting.value.getParent() == null || (Boolean) setting.value.getParent().getValue())
                settingOffset += setting.getHeight();
        this.height = settingOffset;
        return this.height;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        settings.forEach(setting -> setting.mouseClicked(mouseX, mouseY, mouseButton));
    }
}
