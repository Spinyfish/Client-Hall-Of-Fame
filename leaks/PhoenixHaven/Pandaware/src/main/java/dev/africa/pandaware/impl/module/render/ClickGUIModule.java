package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.ColorSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@Getter
@ModuleInfo(name = "Click GUI", description = "STOP LOOKING AT FEMBOYS FAGGOT", category = Category.VISUAL, key = Keyboard.KEY_RSHIFT)
public class ClickGUIModule extends Module {
    private final BooleanSetting showCummyMen = new BooleanSetting("Show Femboys", true);
    private final EnumSetting<FemboyMode> cummyMode = new EnumSetting<>("Femboy Mode", FemboyMode.GREEK,
            this.showCummyMen::getValue);
    private final BooleanSetting allowNSFW = new BooleanSetting("Allow NSFW (18+) pictures", false,
            this.showCummyMen::getValue);
    private final ColorSetting clickColor = new ColorSetting("Click Color", Color.WHITE);

    public ClickGUIModule() {
        this.registerSettings(
                this.showCummyMen,
                this.cummyMode,
                this.allowNSFW,
                this.clickColor
        );
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Client.getInstance().getClickGUI());

        this.toggle(false);
    }

    @AllArgsConstructor
    public enum FemboyMode {
        ASTOLFO("Astolfo"),
        ASTOLFO2("Astolfo 2"),
        ASTOLFO3("Astolfo 3"),
        ASTOLFO4("Astolfo 4"),
        ASTOLFO5("Astolfo 5"),
        NSFWASTOLFO("Astolfo (18+)"),
        JOHNNYSINS("Johnny Sins (18+)"),
        FELIX("Felix"),
        FELIX2("Felix 2"),
        HIDERI("Hideri"),
        SAIKA("Saika"),
        VENTI("Venti"),
        GREEK("Greek"),
        PANDA("APandaWithAKnife");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
