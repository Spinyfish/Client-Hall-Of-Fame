package v4n1ty.module.render;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import de.Hero.settings.Setting;
import v4n1ty.V4n1ty;
import v4n1ty.module.Category;
import v4n1ty.module.Module;

public class ClickGui extends Module {

    public ClickGui() {
        super("ClickGui", Keyboard.KEY_RSHIFT, Category.RENDER);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("New");
        options.add("JellyLike");
        V4n1ty.settingsManager.rSetting(new Setting("Design", this, "New", options));
        V4n1ty.settingsManager.rSetting(new Setting("Sound", this, false));
        V4n1ty.settingsManager.rSetting(new Setting("GuiRed", this, 207, 0, 255, true));
        V4n1ty.settingsManager.rSetting(new Setting("GuiGreen", this, 0, 0, 255, true));
        V4n1ty.settingsManager.rSetting(new Setting("GuiBlue", this, 255, 0, 255, true));
    }

    @Override
    public void onEnable() {
        super.onEnable();

        mc.displayGuiScreen(V4n1ty.clickGUI);
        toggle();
    }
}