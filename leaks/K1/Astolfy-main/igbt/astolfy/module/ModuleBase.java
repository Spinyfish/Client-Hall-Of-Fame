package igbt.astolfy.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.awt.*;

import igbt.astolfy.Astolfy;
import igbt.astolfy.events.Event;
import igbt.astolfy.settings.Setting;
import igbt.astolfy.ui.ClickGUI.dropDown.ClickGui;
import igbt.astolfy.ui.Notifications.Notification;
import igbt.astolfy.ui.Notifications.NotificationType;
import net.minecraft.client.Minecraft;

public class ModuleBase {

	private String name;
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
		this.name = name;
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
		//if(!name.equalsIgnoreCase("ClickGUI"))
		//	Astolfy.notificationManager.showNotification(new Notification(NotificationType.INFORMATION, name, isToggled() ? "Enabled" : "Disabled", 1));
			
	}
	
	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	}
	
	public String getName() {
		return this.name;
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
		COMBAT("Combat", new Color(0xffff4a3d)),
		MOVEMENT("Movement", new Color(0xff6aff3d)),
		PLAYER("Player", new Color(0xff3dffdf)),
		VISUALS("Visuals", new Color(0xff543dff)),
		EXPLOIT("Exploit" , new Color(0xffff3def));

		public String name;
		public Color color;
		
		Category(String name, Color color) {
			this.name = name;
			this.color = color;
		}
	}
}
