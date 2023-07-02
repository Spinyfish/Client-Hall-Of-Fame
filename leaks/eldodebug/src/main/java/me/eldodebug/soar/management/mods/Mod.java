package me.eldodebug.soar.management.mods;

import java.awt.Color;
import java.util.ArrayList;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.HUDMod;
import me.eldodebug.soar.management.settings.Setting;
import me.eldodebug.soar.utils.TimerUtils;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.render.OutlineUtils;
import me.eldodebug.soar.utils.render.RoundedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Mod {

	public Minecraft mc = Minecraft.getMinecraft();
	public FontRenderer fr = mc.fontRendererObj;
	
	private String name;
	private String description;

	private int x, y, draggingX, draggingY;
	
	private int width, height;

	private boolean toggled;

	private boolean dragging, hide;

	private ModCategory category;

	public SimpleAnimation buttonAnimation = new SimpleAnimation(0.0F);
	public SimpleAnimation buttonOpacityAnimation = new SimpleAnimation(0.0F);
	public TimerUtils selectTimer = new TimerUtils();
	public SimpleAnimation selectAnimation = new SimpleAnimation(0.0F);
	public SimpleAnimation editOpacityAnimation = new SimpleAnimation(0.0F);
	
	public Mod(String name, String description, ModCategory category) {
		this.name = name;
		this.description = description;
		this.toggled = false;
		this.x = 100;
		this.y = 100;
		this.width = 100;
		this.height = 100;
		this.category = category;
		setup();
	}
	
	public void setup() {}
	
	public void onEnable() {
		Soar.instance.eventManager.register(this);
	}
	
	public void onDisable() {
		Soar.instance.eventManager.unregister(this);
	}
	
	public void toggle() {
		toggled = !toggled;
		
		if(toggled) {
			onEnable();
		}else {
			onDisable();
		}
	}
	
	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		
		if(toggled) {
			onEnable();
		}else {
			onDisable();
		}
	}
	
	public void addBooleanSetting(String name, Mod mod, boolean toggle) {
		Soar.instance.settingsManager.addSetting(new Setting(name, mod, toggle));
	}
	
	public void addModeSetting(String name, Mod mod, String defaultMode, ArrayList<String> options) {
		Soar.instance.settingsManager.addSetting(new Setting(name, mod, defaultMode, options));
	}
	
	public void addSliderSetting(String name, Mod mod, double defaultValue, double minValue, double maxValue, boolean intValue) {
		Soar.instance.settingsManager.addSetting(new Setting(name, mod, defaultValue, minValue, maxValue, intValue));
	}
	
	public void drawBackground(float x, float y, float width, float height) {
		int opacity = (int) (Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Opacity").getValDouble() * 255);
		int radius = Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Radius").getValInt();
		String mode = Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Design").getValString();
		
		if(Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Background").getValBoolean()) {
			switch(mode) {
				case "Color":
					RoundedUtils.drawGradientRound(x, y, width, height, radius, ColorUtils.getClientColor(0, opacity), ColorUtils.getClientColor(90, opacity), ColorUtils.getClientColor(180, opacity), ColorUtils.getClientColor(270, opacity));
					break;
				case "Rainbow":
					RoundedUtils.drawGradientRound(x, y, width, height, radius, ColorUtils.rainbow(0, 25, opacity), ColorUtils.rainbow(90, 25, opacity), ColorUtils.rainbow(180, 25, opacity), ColorUtils.rainbow(270, 25,opacity));
					break;
				case "Glass1":
					RoundedUtils.drawGradientRound(x, y, width, height, radius, new Color(0.0F, 0.0F, 0.0F, 0.35F), new Color(0.0F, 0.0F, 0.0F, 0.35F), new Color(0.0F, 0.0F, 0.0F, 0.35F), new Color(0.0F, 0.0F, 0.0F, 0.35F));
					break;
				case "Glass2":
					RoundedUtils.drawGradientRound(x, y, width, height, radius, new Color(0.0F, 0.0F, 0.0F, 0.35F), new Color(0.0F, 0.0F, 0.0F, 0.35F), new Color(0.0F, 0.0F, 0.0F, 0.35F), new Color(0.0F, 0.0F, 0.0F, 0.35F));
					break;
				case "Clear1":
					break;
				case "Clear2":
					break;
				case "Theme1":
					RoundedUtils.drawGradientRound(x, y, width, height, radius, ColorUtils.getBackgroundColor(4, opacity), ColorUtils.getBackgroundColor(4, opacity), ColorUtils.getBackgroundColor(4, opacity), ColorUtils.getBackgroundColor(4, opacity));
					break;
				case "Theme2":
					RoundedUtils.drawGradientRound(x, y, width, height, radius, ColorUtils.getBackgroundColor(4, opacity), ColorUtils.getBackgroundColor(4, opacity), ColorUtils.getBackgroundColor(4, opacity), ColorUtils.getBackgroundColor(4, opacity));
					break;
				case "Outline1":
					RoundedUtils.drawGradientRound(x, y, width, height, radius, new Color(0.0F, 0.0F, 0.0F, 0.35F), new Color(0.0F, 0.0F, 0.0F, 0.35F), new Color(0.0F, 0.0F, 0.0F, 0.35F), new Color(0.0F, 0.0F, 0.0F, 0.35F));
					OutlineUtils.drawGradientOutline(x - 1F, y - 1F, width + 2, height + 2, 4, radius * 2, ColorUtils.getClientColor(0).getRGB(), ColorUtils.getClientColor(90).getRGB(), ColorUtils.getClientColor(180).getRGB(), ColorUtils.getClientColor(270).getRGB());
					break;
				case "Outline2":
					OutlineUtils.drawGradientOutline(x - 1F, y - 1F, width + 2, height + 2, 4, radius * 2, ColorUtils.getClientColor(0).getRGB(), ColorUtils.getClientColor(90).getRGB(), ColorUtils.getClientColor(180).getRGB(), ColorUtils.getClientColor(270).getRGB());
					break;
				case "Outline3":
					RoundedUtils.drawGradientRound(x, y, width, height, radius, new Color(0.0F, 0.0F, 0.0F, 0.35F), new Color(0.0F, 0.0F, 0.0F, 0.35F), new Color(0.0F, 0.0F, 0.0F, 0.35F), new Color(0.0F, 0.0F, 0.0F, 0.35F));
					OutlineUtils.drawGradientOutline(x - 1F, y - 1F, width + 2, height + 2, 4, radius * 2, ColorUtils.getBackgroundColor(4, opacity).getRGB(), ColorUtils.getBackgroundColor(4, opacity).getRGB(), ColorUtils.getBackgroundColor(3, opacity).getRGB(), ColorUtils.getBackgroundColor(4, opacity).getRGB());
					break;
				case "Outline4":
					OutlineUtils.drawGradientOutline(x - 1F, y - 1F, width + 2, height + 2, 4, radius * 2, ColorUtils.getBackgroundColor(4, opacity).getRGB(), ColorUtils.getBackgroundColor(4, opacity).getRGB(), ColorUtils.getBackgroundColor(3, opacity).getRGB(), ColorUtils.getBackgroundColor(4, opacity).getRGB());
					break;
				case "Classic":
					RoundedUtils.drawGradientRound(x, y, width, height, radius, new Color(52, 52, 52), new Color(52, 52, 52), new Color(52, 52, 52), new Color(52, 52, 52));
					OutlineUtils.drawGradientOutline(x - 1F, y - 1F, width + 2, height + 2, 4, radius * 2, new Color(92, 92, 92).getRGB(), new Color(92, 92, 92).getRGB(), new Color(92, 92, 92).getRGB(), new Color(92, 92, 92).getRGB());
					break;
				case "Halloween":
					RoundedUtils.drawRound(x, y, width, height, radius, ColorUtils.getHalloweenColor().getLightOrange());
					OutlineUtils.drawOutline(x - 1F, y - 1F, width + 2F, height + 2F, 4, radius * 2, ColorUtils.getHalloweenColor().getPurple().getRGB());
					break;
				case "Christmas":
					RoundedUtils.drawRound(x, y, width, height, radius, ColorUtils.getChristmasColor().getRed());
					OutlineUtils.drawOutline(x - 1F, y - 1F, width + 2F, height + 2F, 4, radius * 2, ColorUtils.getChristmasColor().getGreen().getRGB());
					break;
			}
		}
	}
	
	public void drawShadow(float x, float y, float width, float height) {
		int radius = Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Radius").getValInt();
		String mode = Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Design").getValString();
		
		if(Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Shadow").getValBoolean()) {
			
			if(mode.equals("Outline1") || mode.equals("Outline2") || mode.equals("Outline3") || mode.equals("Outline4") || mode.equals("Classic") || mode.equals("Halloween") || mode.equals("Christmas")) {
				RoundedUtils.drawRound(x - 1F, y - 1F, width + 2F, height + 2F, radius, new Color(0, 0, 0));
			}else {
				RoundedUtils.drawRound(x, y, width, height, radius, new Color(0, 0, 0));
			}
		}
	}
	
	public Color getFontColor() {
		String mode = Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Design").getValString();
		
		if(mode.equals("Theme1")) {
			return ColorUtils.getFontColor(2);
		}
		
		if(mode.equals("Theme2")) {
			return ColorUtils.getClientColor(0);
		}
		
		if(mode.equals("Glass2")) {
			return ColorUtils.getClientColor(0);
		}
		
		if(mode.equals("Clear2")) {
			return ColorUtils.getClientColor(0);
		}
		
		if(mode.equals("Outline3")) {
			return ColorUtils.getClientColor(0);
		}
		
		if(mode.equals("Halloween")) {
			return ColorUtils.getHalloweenColor().getBlack();
		}
		
		return new Color(255, 255, 255);
	}
	
	public void setWidth(int width) {
		this.width = this.getX() + width;
	}
	
	public void setHeight(int height) {
		this.height = this.getY() + height;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getDraggingX() {
		return draggingX;
	}

	public void setDraggingX(int draggingX) {
		this.draggingX = draggingX;
	}

	public int getDraggingY() {
		return draggingY;
	}

	public void setDraggingY(int draggingY) {
		this.draggingY = draggingY;
	}

	public boolean isDragging() {
		return dragging;
	}

	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public ModCategory getCategory() {
		return category;
	}

	public void setCategory(ModCategory category) {
		this.category = category;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isToggled() {
		return toggled;
	}

	public String getDescription() {
		return description;
	}
}
