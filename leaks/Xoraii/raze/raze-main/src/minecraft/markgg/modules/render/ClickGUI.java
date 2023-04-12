package markgg.modules.render;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import markgg.Client;
import markgg.events.Event;
import markgg.events.listeners.EventRenderGUI;
import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.ui.GUIMethod;
import markgg.ui.GUIMethodLight;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public class ClickGUI extends Module{

	public int currentTab;
	public boolean expanded;
	public ModeSetting mode = new ModeSetting("Theme", this, "Dark", "Dark", "Light");
	public BooleanSetting darken = new BooleanSetting("Darken", this, true);
	
	public ClickGUI() {
		super("ClickGUI", "The ClickGUI", Keyboard.KEY_RSHIFT, Category.RENDER);
		addSettings(mode, darken);
	}
	
	public void onEnable() {
		NotificationManager.show(new Notification(NotificationType.ENABLE, getName() + " was enabled!", 1));
		switch (mode.getMode()) {
		case "Dark":
			mc.displayGuiScreen(new GUIMethod());
			break;
		case "Light":
			mc.displayGuiScreen(new GUIMethodLight());
			break;
		}
		toggle();
	}
}
