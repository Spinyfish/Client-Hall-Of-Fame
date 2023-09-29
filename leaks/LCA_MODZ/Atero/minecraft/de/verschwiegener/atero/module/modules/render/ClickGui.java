package de.verschwiegener.atero.module.modules.render;

import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.ui.clickgui.ClickGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;

public class ClickGui extends Module {

    public ClickGui() {
	super("ClickGui", "ClickGui", Keyboard.KEY_RSHIFT, Category.Render);
    }

    @Override
    public void onEnable() {
	super.onEnable();
	Minecraft.getMinecraft().displayGuiScreen(Management.instance.clickgui);
	toggle(false);
    }




    @Override
    public void setup() {

        final ArrayList<SettingsItem> items = new ArrayList<>();
        ArrayList<String> modes = new ArrayList<>();
        modes.add("AAC");
        items.add(new SettingsItem("TEST", Color.RED, ""));
        items.add(new SettingsItem("TEST22", Color.RED, ""));
        Management.instance.settingsmgr.addSetting(new Setting(this, items));
    }

}
