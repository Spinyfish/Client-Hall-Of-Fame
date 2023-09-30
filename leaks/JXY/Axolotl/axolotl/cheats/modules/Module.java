package axolotl.cheats.modules;

import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Objects;

import axolotl.Axolotl;
import axolotl.cheats.events.Event;
import axolotl.cheats.settings.KeybindSetting;
import axolotl.cheats.settings.Setting;
import axolotl.cheats.settings.Settings;
import net.minecraft.client.Minecraft;

public class Module {

	public String name;
	public boolean toggled = false, toggleMessages;
	public Category category;
	public Minecraft mc = Minecraft.getMinecraft();
	public Setting specialSetting;

	public void createModule(String name, Category category, boolean toggleMessages, int code) {
		this.name = name;
		this.category = category;
		this.toggleMessages = toggleMessages;
		this.keybindSetting = new KeybindSetting(code);
		this.settings.addSetting(keybindSetting);
	}

    public Module(String name, Category category, boolean toggleMessages) {
		createModule(name, category, toggleMessages, 0);
	}

	public Module(String name, int code, Category category, boolean toggleMessages) {
		createModule(name, category, toggleMessages, code);
	}
	
	public Module(String name, int code, Category category, boolean toggleMessages, boolean toggled) {
		createModule(name, category, toggleMessages, code);
		this.toggled = toggled;
	}
	
	public Settings settings = new Settings();
	public KeybindSetting keybindSetting;
	
	public void addSettings(Setting... settings) {
		for(Setting s : settings) {
			this.settings.addSetting(s);
		}
	}

	public void setSpecialSetting(Setting s) {
		specialSetting = s;
		if(!settings.getSettings().contains(s)) {
			addSettings(s);
		}
	}
	
	public void toggle() {
		if(!Axolotl.INSTANCE.clientOn)return;
		toggled = !toggled;
		if(toggleMessages)
			Axolotl.INSTANCE.sendMessage((toggled ? "Enabled " : "Disabled ") + name);
		if(toggled) {
			onEnable();
		} else {
			mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
			mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
			mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
			mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
			if(mc.thePlayer != null && mc.theWorld != null) {
				mc.timer.timerSpeed = 1f;
				mc.thePlayer.jumpMovementFactor = 0.02f;
				mc.thePlayer.speedInAir = 0.02f;
				mc.thePlayer.stepHeight = 0.5F;
			}
			onDisable();
		}
	}
	public boolean isToggled() {
		return toggled;
	}
	
	public void hClip(double amount) {
		double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);
		double x = amount * -Math.sin(playerYaw);
		double z = amount * Math.cos(playerYaw);
		mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
	}
	
	public void vClip(double d) {
		mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ);
	}
	
	public void onEnable() { }
	
	public void onDisable() { }
	
	public boolean isMoving() {
		return ((
				Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())
				!=
				Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())
			   )
				||
			   (
				Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())
				!=
				Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())
			   )) && !mc.thePlayer.isDead && mc.currentScreen == null;
	}

	public Setting getSpecialSetting() {
		return specialSetting;
	}

	protected String ip;

	protected boolean ipToggle() {
		if (ip == null && mc.getCurrentServerData() != null) ip = mc.getCurrentServerData().serverIP;
		if (mc.getCurrentServerData() != null && !Objects.equals(mc.getCurrentServerData().serverIP, ip)) {
			ip = mc.getCurrentServerData().serverIP;
			this.toggle();
			return true;
		}
		return false;
	}

	public enum Category {
		
		COMBAT("Combat", new Color(0x1B1C17)),
		MOVEMENT("Movement", new Color(0x1B1C17)),
		PLAYER("Player", new Color(0x1B1C17)),
		RENDER("Render", new Color(0x1B1C17)),
		WORLD("World", new Color(0x1B1C17)),
		CHAT("Chat", new Color(0x1B1C17));
		
		public String name;
		public Color color;
		
		Category(String name, Color color) {
			this.name = name;
			this.color = color;
		}
		
	}

	public void onEvent(Event event) { }

	public void onKeyPress(int key) { }
	
}
