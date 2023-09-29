package de.verschwiegener.atero.module.modules.combat;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;

public class Target extends Module{

	public Target() {
		super("Target", "Target", Keyboard.KEY_NONE, Category.Combat);
	}
	@Override
	public void setup() {
		super.setup();
		ArrayList<SettingsItem> items = new ArrayList<>();
		items.add(new SettingsItem("Player", true, "Death"));
		items.add(new SettingsItem("Death", false, ""));
		items.add(new SettingsItem("Animals", false, ""));
		items.add(new SettingsItem("Mobs", false, ""));
		items.add(new SettingsItem("Villager", false, ""));
		items.add(new SettingsItem("Teams", false, ""));
		items.add(new SettingsItem("Invisible", false, ""));
		Management.instance.settingsmgr.addSetting(new Setting(this, items));
	}

}
