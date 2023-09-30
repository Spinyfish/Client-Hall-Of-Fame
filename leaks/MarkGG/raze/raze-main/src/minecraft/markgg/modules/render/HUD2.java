package markgg.modules.render;

import java.util.Comparator;

import org.lwjgl.input.Keyboard;

import markgg.modules.Module.Category;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import markgg.Client;
import markgg.events.Event;
import markgg.events.listeners.EventRenderGUI;
import markgg.modules.Module;

public class HUD2 extends Module{

	public BooleanSetting BPS = new BooleanSetting("BPS", this, true);
	public BooleanSetting FPS = new BooleanSetting("FPS", this, false);
	public BooleanSetting moduleList = new BooleanSetting("Arraylist", this, true);
	public BooleanSetting waterMark = new BooleanSetting("Watermark", this, true);
	public BooleanSetting coordinates = new BooleanSetting("Coordinates", this, true);
	public BooleanSetting keyStroke = new BooleanSetting("Keystrokes", this, false);
	public BooleanSetting notifs = new BooleanSetting("Notifications", this, true);
	public ModeSetting markMode = new ModeSetting("HUD Mode", this, "Sense", "Sense", "Classic");

	public HUD2() {
		super("HUD", "Heads up display", 0, Category.RENDER);
		addSettings(markMode, BPS, FPS, moduleList, waterMark, coordinates, keyStroke, notifs);
		toggled = true;
	}
}
