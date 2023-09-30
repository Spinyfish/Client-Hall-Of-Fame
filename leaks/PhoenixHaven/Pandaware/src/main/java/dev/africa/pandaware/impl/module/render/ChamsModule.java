package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.ColorSetting;
import lombok.Getter;

import java.awt.*;

@Getter
@ModuleInfo(name = "Chams", category = Category.VISUAL)
public class ChamsModule extends Module {
    private final BooleanSetting color = new BooleanSetting("Color", false);
    private final BooleanSetting alwaysColor = new BooleanSetting("Always Color", true,
            this.color::getValue);
    private final ColorSetting visibleColor = new ColorSetting("Visible Color", Color.WHITE,
            () -> this.alwaysColor.getValue() && this.color.getValue());
    private final ColorSetting hiddenColor = new ColorSetting("Hidden Color", Color.WHITE,
            this.color::getValue);

    public ChamsModule() {
        this.registerSettings(this.color, this.alwaysColor, this.visibleColor, this.hiddenColor);
    }
}
