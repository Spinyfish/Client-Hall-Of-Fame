package de.hero.example;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import de.fanta.Client;
import de.fanta.clickgui.astolfo.ClickGuiScreen;
import de.fanta.events.Event;
import de.fanta.events.listeners.PlayerMoveEvent;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.utils.ChatUtil;

public class GUI extends Module {

	public static GUI INSTANCE;

	public GUI() {
		super("GUI", Keyboard.KEY_RSHIFT, Type.Visual, Color.white);
		this.settings.add(
				new Setting("Modes", new DropdownBox("Mega", new String[] {"DropDown", "Skash", "Astolfo", "IntelliJ", "Mega"})));

	}

	@Override
	public void onEnable() {
		Client.INSTANCE.moduleManager.getModule("GUI").setState(false);
		switch (((DropdownBox) getSetting("Modes").getSetting()).curOption) {
		case "DropDown":
			mc.displayGuiScreen(Client.clickgui);
			break;
		case "Astolfo":
			mc.displayGuiScreen(new ClickGuiScreen());
			break;
		case "IntelliJ":
			mc.displayGuiScreen(new de.fanta.clickgui.intellij.ClickGuiScreen(null));
			break;
		default:
			break;
		}
		super.onEnable();
	}

	@Override
	public void onDisable() {
		Client.INSTANCE.moduleManager.getModule("GUI").setState(false);
		/**
		 * Einfach in der StartMethode clickgui = new ClickGUI(); ;)
		 */
		if (Client.INSTANCE.moduleManager.getModule("GUI").isState() && ((DropdownBox) Client.INSTANCE.moduleManager
				.getModule("GUI").getSetting("Modes").getSetting()).curOption.equalsIgnoreCase("DropDown")) {
			switch (((DropdownBox) getSetting("Modes").getSetting()).curOption) {
			case "DropDown":
				mc.displayGuiScreen(Client.clickgui);
				break;
			case "Astolfo":
				mc.displayGuiScreen(new ClickGuiScreen());
				break;
			case "IntelliJ":
				mc.displayGuiScreen(new de.fanta.clickgui.intellij.ClickGuiScreen(null));
				break;
			default:
				break;
			}
		}
		super.onDisable();
	}

	@Override
	public void onEvent(Event event) {

	}
}
