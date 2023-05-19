package club.marsh.bloom.api.module;

import java.util.ArrayList;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.value.ModeValue;
import club.marsh.bloom.impl.ui.notification.NotificationType;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public abstract class Module {
	public boolean toggled = false;
	public int keybind;
	public Category category;
	public static Minecraft mc = Minecraft.getMinecraft();
	private String name;
	public ModeValue mode;
	@Getter@Setter
	public boolean expanded = false;
	ArrayList<Mode> modelist = new ArrayList<>();
	public boolean isToggled() {
		return toggled;
	}
	
	public void addModes(Mode... modes) {
		for (Mode mode : modes) {
			modelist.add(mode);
		}
	}
	
	public void autoSetName(ModeValue mode) {
		this.mode = mode;
	}
	
	public void addModesToModule() {};
	
	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		if (toggled) {
			Bloom.INSTANCE.eventBus.register(this); 
			onEnable();
			for (Mode mode : modelist) {
				//if (mode.canBeUsed()) { 
				Bloom.INSTANCE.eventBus.register(mode); 
				mode.onEnable();
				//}
			}
		} else {
			Bloom.INSTANCE.eventBus.unregister(this); 
			onDisable();
			for (Mode mode : modelist) {
				try {
					Bloom.INSTANCE.eventBus.unregister(mode); 
					mode.onDisable();
				} catch (Exception ignored) {};
			}
		}
		Bloom.INSTANCE.notificationPublisher.publish("Module Toggled!", name + " has been toggled " + (toggled ? "on" : "off"),2000L, NotificationType.INFO);
		//addMessage(name + " has been toggled to: " + toggled);

	}
	public int getKeybind() {
		return keybind;
	}
	public void addMessage(String message) {
		 Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\247dBloom \2479>>\247a " + message));
	}
	public void setKeybind(int keybind) {
		this.keybind = keybind;
	}
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return name + (mode != null ? " \2477[" + mode.getMode() + "]" : "");
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
    public Module(String name, int key, Category c) {
    	this.name = name; this.keybind = key; this.category = c;
    }
}
