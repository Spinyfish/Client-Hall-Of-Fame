package juul.module;

import java.util.ArrayList;
import java.util.List;

import juul.inject.wrappers.MinecraftWrapper;
import juul.util.Utils;
import org.lwjgl.input.Keyboard;

import juul.Juul;
import juul.event.Event;
import juul.module.settings.Setting;
import juul.Constants;
import juul.util.image.Animation;

public class Module implements Utils {

	public Constants constants = Juul.INSTANCE.constants;
	public String name, description;
	public int keyBind;
	public boolean toggled;
	public Category category;
	public List<Setting> settings = new ArrayList<Setting>();
	public Animation animation = new Animation();

	protected MinecraftWrapper mc = Juul.INSTANCE.getWrapperManager().get(MinecraftWrapper.class);

	public Module(String name, String desc, int key, Category cat) {
		this.name = name;
		description = desc;
		keyBind = key;
		category = cat;
	}
	
	public void addSetting(Setting s) {
		settings.add(s);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
	}
	
	public double getNameLength() {
		return constants.getStringWidth(name);
	}
	
	public void enable() {
		if(toggled)
			return;
		
		onEnable();
		toggled = true;
	}
	
	public void disable() {
		if(!toggled)
			return;
		
		toggled = false;
		onDisable();
	}
	
	public void toggle() {
		if(toggled)
			disable();
		else enable();
	}
	
	public String getKeyCharacter() {
		switch(keyBind) {
			case 0:
				return "";
			case 41:
				return "`";
			case 2:
				return "!";
			case 3:
				return "@";
			case 4:
				return "#";
			case 5:
				return "$";
			case 6:
				return "%";
			case 7:
				return "^";
			case 8:
				return "&";
			case 9:
				return "*";
			case 10:
				return "(";
			case 11:
				return ")";
			case 12:
				return "-";
			case 13:
				return "=";
			case 14:
				return "←";
			case 26:
				return "[";
			case 27:
				return "]";
			case 43:
				return "\\";
			case 28:
				return "↵";
			case 54:
				return "⇧";
			case 42:
				return "⇧";
			case 39:
				return ";";
			case 40:
				return "'";
			case 51:
				return ",";
			case 52:
				return ".";
			case 53:
				return "/";
			default:
				return Keyboard.getKeyName(keyBind);
		}
	}
	
	public boolean isEnabled() {
		return toggled;
	}
	
	public enum Category {
		RENDER,
		COMBAT,
		MOVEMENT,
		PLAYER,
		WORLD;
		
		public Animation fade = new Animation();
	}
	
}
