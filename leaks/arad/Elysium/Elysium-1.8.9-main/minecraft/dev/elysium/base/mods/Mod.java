package dev.elysium.base.mods;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dev.elysium.base.events.EventBus;
import dev.elysium.base.mods.settings.*;
import dev.elysium.client.Elysium;
import net.minecraft.client.Minecraft;

public class Mod {
	//clickgui shit
	public int tTimer = 0;
	public int hoverTimer = 0;
	public int descTimer = 0;

	public boolean wasToggledInSwipe = false;
	public String description;
	public String name;
	public KeybindSetting keyCode;
	public Category category;
	public boolean toggled = false;

	protected static Minecraft mc = Minecraft.getMinecraft();
	public List<Setting> settings = new ArrayList<Setting>();
    public boolean show;
	public boolean binding;

	public int getSettingsI() {
		int i = 0;
		for(Setting s : settings) {
			if(s instanceof NumberSetting) {
				i += 2;
			} else if(s instanceof BooleanSetting) {
				i++;
			} else if(s instanceof ModeSetting) {
				i++;
			} else if(s instanceof ColorSetting) {
				//Bruh 2 lazy
			}
		}

		return i;
	}

	public int getKey() {
		return keyCode.getKeyCode();
	}

    public Mod(String name, String description, Category cat) {
		this.name = name;
		this.category = cat;
		this.description = description;
		keyCode = new KeybindSetting(0, this);
		settings.sort(Comparator.comparing(setting -> setting instanceof KeybindSetting).reversed());
	}

	public void toggle() {
		toggled = !toggled;
		if (toggled) {
			if(mc.theWorld != null)
				onEnable();
			EventBus.register(this);
		} else {
			if(mc.theWorld != null)
				onDisable();
			EventBus.unregister(this);
		}
	}
	
	public void onEnable() {

	}
	public void onDisable() {

	}
	
}
