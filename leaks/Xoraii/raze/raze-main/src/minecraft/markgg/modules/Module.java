package markgg.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import markgg.Client;
import markgg.events.Event;
import markgg.settings.KeybindSetting;
import markgg.settings.Setting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import net.minecraft.client.Minecraft;

public class Module {
	public String name, description;
	public boolean toggled;
	public Category category;
	public KeybindSetting keyCode = new KeybindSetting(this,0);

	public Minecraft mc = Minecraft.getMinecraft();

	public boolean expanded;
	public int index;
	public List<Setting> settings = new ArrayList<Setting>();

	public Module(String name, String description, int key, Category c) {
		this.name = name;
		this.description = description;
		keyCode.code = key;
		this.category = c;
		this.addSettings(keyCode);
	}

	public void addSettings(Setting... settings) {
		this.settings.addAll(Arrays.asList(settings));
		this.settings.sort(Comparator.comparingInt(s -> s == keyCode ? 1 : 0));
	}
	
	public List<Setting> getSettings() {
        return settings;
    }

	public boolean isEnabled() {
		return toggled;
	}

	public int getKey() {
		return keyCode.code;
	}

	public void toggle() {
		toggled = !toggled;
		if (toggled) {
			onEnable();
		} else {
			onDisable();
		} 
	}

	public void onEnable() {
		NotificationManager.show(new Notification(NotificationType.ENABLE, getName() + " was enabled!", 1));
	}

	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
	}

	public void onEvent(Event e) {}
	
	public void onUpdate() {}
	
	public void onRender() {}

	public String getName() {
		return name;
	}

	public enum Category {
		COMBAT("Combat"),
		MOVEMENT("Movement"),
		RENDER("Render"),
		PLAYER("Player"),
		GHOST("Ghost"),
		MISC("Misc");

		public String name;

		public int moduleIndex;

		Category(String name) {
			this.name = name;
		}
	}
	
	public Category getCategory() {
		return category;
	}

	public void resetHandPosition() {
		if (!mc.thePlayer.isSwingInProgress) {
			mc.thePlayer.renderArmPitch = -0.0F;
			mc.thePlayer.renderArmYaw = -0.0F;
		} 
	}

}
