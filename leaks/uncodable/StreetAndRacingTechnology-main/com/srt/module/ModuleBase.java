package com.srt.module;

import java.util.ArrayList;
import java.util.Arrays;

import com.srt.events.Event;
import com.srt.settings.Setting;

import net.minecraft.client.Minecraft;

public class ModuleBase {

	private String name, displayName;
	private String suffix = "";
	private int key;
	private Category category;
	private boolean toggled;
	public static Minecraft mc = Minecraft.getMinecraft();
	private ArrayList<Setting> settings = new ArrayList<>();


	public ArrayList<Setting> getSettings() {
		return settings;
	}
	public void addSettings(Setting... settings) {
		this.settings.addAll(Arrays.asList(settings));
	}
	public ModuleBase(String name, int key, Category category) {
		displayName = this.name = name;
		this.key = key;
		this.category = category;
		this.toggled = false;
	}
	public void onEnable() {}
	
	public void onDisable() { }
	
	public void onEvent(Event e) { }
	
	public void onSkipEvent(Event e) { } //Non Toggled Event
	
	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public boolean isToggled() {
		return toggled;
	}

	public void toggle() {
		this.toggled = !toggled;
		if(!this.toggled)
			onDisable();
		else
			onEnable();
	}
	
	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public void setDisplayName(String name) {
		displayName = name;
	}
	
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public enum Category {
		COMBAT("Combat"),
		MOVEMENT("Movement"),
		FUNNY("Funny"),
		PLAYER("Player"),
		VISUALS("Visuals"),
		EXPLOIT("Exploit"),
		SECRETS("Secrets");

		public String name;
		
		Category(String cName) {
			this.name = cName;
		}
	}
}
