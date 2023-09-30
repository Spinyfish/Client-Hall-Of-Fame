package me.r.touchgrass.module.modules.gui;

import me.r.touchgrass.file.files.ClickGuiConfig;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.ui.clickgui.ClickGui;
import org.lwjgl.input.Keyboard;
import me.r.touchgrass.settings.Setting;

import java.util.ArrayList;

/**
 * Created by r on 03/02/2021
 */

@Info(name = "ClickGUI", description = "The click gui", category = Category.Gui, keybind = Keyboard.KEY_LSHIFT)
public class ClickGUI extends Module {

    public ClickGui clickgui;

    public ClickGUI() {
        ArrayList<String> font = new ArrayList<>();
        font.add("TTF");
        font.add("Minecraft");

        addSetting(new Setting("Font Type", this, "TTF", font));
        addSetting(new Setting("Blur", this, true));
        addSetting(new Setting("Tooltip", this, true));
        addSetting(new Setting("Particles", this, true));

        addSetting(new Setting("Red", this, 19, 0, 255, true));
        addSetting(new Setting("Blue", this, 109, 0, 255, true));
        addSetting(new Setting("Green", this, 21, 0, 255, true));
        addSetting(new Setting("Alpha", this, 220, 0, 255, true));

   /*
        this exists to i dont have to remember how to add options lol.

   ArrayList<String> options = new ArrayList<>();
        options.add("DefaultOption");
        options.add("Option2");
        options.add("Option3");
        touchgrass.getInstance().settingsManager.rSetting(new Setting("OptionSelector", this, "DefaultOption", options));
        touchgrass.getInstance().settingsManager.rSetting(new Setting("BooleanOption", this, false));
        touchgrass.getInstance().settingsManager.rSetting(new Setting("SliderOptionInt", this, 255, 0, 255, true));
        touchgrass.getInstance().settingsManager.rSetting(new Setting("SliderOptionDouble", this, 10, 0, 20, false));*/
    }

    @Override
    public void onEnable() {
        if(this.clickgui == null) {
            this.clickgui = new ClickGui();
        }
        ClickGuiConfig clickGuiConfig = new ClickGuiConfig();
        clickGuiConfig.loadConfig();
        mc.displayGuiScreen(this.clickgui);
        toggle();
        super.onEnable();
    }
}
