package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@ModuleInfo(name = "Animations", description = "Item animations & more", category = Category.VISUAL)
public class AnimationsModule extends Module {
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.VANILLA);

    private final BooleanSetting swingSpeedEdit = new BooleanSetting("Swing speed edit", false);
    private final NumberSetting swingSpeed = new NumberSetting("Swing speed",
            30, 0, 14, 0.5f, this.swingSpeedEdit::getValue);

    private final NumberSetting itemOffset = new NumberSetting("Item offset",
            1.5, 0, 0, 0.05);
    private final NumberSetting itemScale = new NumberSetting("Item scale",
            1.5, 0.1, 1, 0.05);
    private final NumberSetting itemDistance = new NumberSetting("Item distance",
            3, 1, 1, 0.05);

    public AnimationsModule() {
        this.registerSettings(
                this.mode,
                this.swingSpeedEdit,
                this.swingSpeed,
                this.itemOffset,
                this.itemScale,
                this.itemDistance
        );
    }

    @Override
    public String getSuffix() {
        return this.mode.getValue().label;
    }

    @AllArgsConstructor
    public enum Mode {
        VANILLA("Vanilla"),
        VIRTUE("Virtue"),
        EXHIBITION("Exhibition"),
        EXHIBITION2("Exhibition2"),
        SLIDE("Slide"),
        SLIDE2("Slide2"),
        PULL("Pull"),
        SWANG("Swang"),
        SWANK("Swank");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
