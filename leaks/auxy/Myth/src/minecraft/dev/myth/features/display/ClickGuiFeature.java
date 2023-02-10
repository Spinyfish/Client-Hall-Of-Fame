/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 19:53
 */
package dev.myth.features.display;

import dev.myth.api.feature.Feature;
import dev.myth.settings.ColorSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.ListSetting;
import dev.myth.settings.NumberSetting;
import dev.myth.ui.clickgui.blubgui.BlubUI;
import dev.myth.ui.clickgui.dropdowngui.ClickGui;
import dev.myth.ui.clickgui.skeetgui.SkeetGui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@Feature.Info(
        name = "ClickGui",
        description = "Displays the ClickGUi",
        category = Feature.Category.DISPLAY,
        keyBind = Keyboard.KEY_RSHIFT
)
public class ClickGuiFeature extends Feature {

    public final EnumSetting<ClickGuiMode> clickGuiModeEnumSetting = new EnumSetting<>("Mode", ClickGuiMode.DROPDOWN);
    public final EnumSetting<EnabledButton> enabledButton = new EnumSetting<>("Enabled Button", EnabledButton.FULL).addDependency(() -> clickGuiModeEnumSetting.is(ClickGuiMode.DROPDOWN));
    public final ListSetting<ShaderAddons> shaderSettings = new ListSetting<>("Shader Addons", ShaderAddons.GLOW).addDependency(() -> clickGuiModeEnumSetting.is(ClickGuiMode.DROPDOWN));
    public final ListSetting<BackgroundAddons> backgroundSettings = new ListSetting<>("Background Addons", BackgroundAddons.GARDIENT).addDependency(() -> clickGuiModeEnumSetting.is(ClickGuiMode.DROPDOWN));
    public final EnumSetting<AnimeAddons> anime = new EnumSetting<>("Anime", AnimeAddons.KIRIGAYA).addDependency(() -> backgroundSettings.isEnabled("Anime")).addDependency(() -> clickGuiModeEnumSetting.is(ClickGuiMode.DROPDOWN));
    public final NumberSetting animeX = new NumberSetting("Anime X", 577, -1000, 1000, 10).addDependency(() -> backgroundSettings.isEnabled("Anime")).addDependency(() -> clickGuiModeEnumSetting.is(ClickGuiMode.DROPDOWN) );
    public final ColorSetting gardientColor = new ColorSetting("Gardient Color", Color.RED).addDependency(() -> backgroundSettings.isEnabled("Gardient")).addDependency(() -> clickGuiModeEnumSetting.is(ClickGuiMode.DROPDOWN));
    public final ColorSetting colorSetting = new ColorSetting("ClickGui Color", Color.CYAN);

    private ClickGui dropdown;
    private SkeetGui skeetGui;

    private BlubUI blubUI;

    @Override
    public void onEnable() {
        super.onEnable();
        switch (clickGuiModeEnumSetting.getValue()) {
            case DROPDOWN:
                if (dropdown == null)
                    dropdown = new ClickGui();
                MC.displayGuiScreen(dropdown);
                break;
            case SKEET:
                if (skeetGui == null)
                    skeetGui = new SkeetGui();
                MC.displayGuiScreen(skeetGui);
                break;
            case BLUB:
                if (blubUI == null)
                    blubUI = new BlubUI();
                MC.displayGuiScreen(blubUI);
                break;



        }


        toggle();
    }

    public enum ClickGuiMode {
        DROPDOWN("Dropdown"),
        SKEET("Skeet"),
        BLUB("Blub");

        private final String name;

        ClickGuiMode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum AnimeAddons {
        REM("Rem"),
        REM2("Rem2"),
        ASNA("Asna"),
        SCHOOLGIRL("SchoolGirl"),
        KIRIGAYA("Kirigaya"),
        MIKU("Miku"),
        SHIINAMASHIRO("Shiina Mashiro"),
        AKENO("Akeno"),
        MISAKA("Misaka");

        private final String name;

        AnimeAddons(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum EnabledButton {
        FULL("Full"),
        TEXT("Text");

        private final String name;

        EnabledButton(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum ShaderAddons {
        GLOW("Glow"),
        TEST("Test");

        private final String name;

        ShaderAddons(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum BackgroundAddons {
        GARDIENT("Gardient"),
        ANIME("Anime");

        private final String name;

        BackgroundAddons(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
