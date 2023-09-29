package de.fanta.clickgui.astolfo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import de.fanta.Client;
import de.fanta.clickgui.astolfo.objects.GuiBooleanChanger;
import de.fanta.clickgui.astolfo.objects.GuiChanger;
import de.fanta.clickgui.astolfo.objects.GuiColorChanger;
import de.fanta.clickgui.astolfo.objects.GuiNumberChanger;
import de.fanta.clickgui.astolfo.objects.GuiStringSelector;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class ClickGuiModulePane extends Gui{

	public int x, y;
	public int width = 125;
	public int height = 20;
	public int baseHeight = 20;
	private boolean extended;
	private Module module;

	private ClickGuiScreen screen;
	private ClickGuiPane paneOn;
	
	private Color BACKGROUND_COLOR = Color.decode("#1A1A1A"), BACKGROUND_COLOR2 = Color.decode("#252525");

	public List<GuiChanger> changers = new ArrayList<GuiChanger>();
	private boolean init = false;

	public ClickGuiModulePane(Module module, int x, int y, ClickGuiScreen screen, ClickGuiPane paneOn) {
		this.x = x;
		this.y = y;
		this.module = module;
		this.screen = screen;
	}

	public void draw(float mouseX, float mouseY) {
		if (init == false) {
			int yAdd = 0;
			for (Setting setting : this.module.getSettings()) {
				GuiChanger curr = null;
				switch (setting.getSetting().getClass().getSimpleName()) {
				case "CheckBox":
					changers.add(curr = new GuiBooleanChanger(x+width+1, y+changersAdd(), setting, this.module.getType()));
					break;
				case "Slider":
					changers.add(curr = new GuiNumberChanger(x, y+changersAdd()+18, setting, this.module.getType()));
					break;
				case "DropdownBox":
					changers.add(curr = new GuiStringSelector(x+width+1, y+changersAdd(), setting, this.module.getType()));
					break;
				case "ColorValue":
					changers.add(curr = new GuiColorChanger(x+width+1, y+changersAdd(), setting, this.module.getType()));
					break;
				default:
					break;
				}
				if(curr != null) yAdd += curr.height;
			}
			init = true;
		}
		if(!extended) {
			this.height = baseHeight;
		}else {
			this.height = baseHeight+changersAdd();
		}
		
		Color COLOR_ENABLED = new Color(module.getType().getColor());
		Color COLOR_DISABLED = BACKGROUND_COLOR2;
		update();
		drawRect(x, y, x + width, y + baseHeight,
				isHovered(mouseX, mouseY) ? BACKGROUND_COLOR.getRGB()
						: BACKGROUND_COLOR.getRGB());
		drawRect(x+2.25F, y-2.5F, x + width-1.75F, y + baseHeight - 2.5F,
				extended ? (COLOR_DISABLED.getRGB()) : !module.isState() ? (isHovered(mouseX, mouseY) ? ColorUtils.getMulitpliedColor(COLOR_DISABLED, 1.5) : COLOR_DISABLED.getRGB()) : (isHovered(mouseX, mouseY) ? ColorUtils.getMulitpliedColor(COLOR_ENABLED, 0.75) : COLOR_ENABLED.getRGB()));
		
		
		int stringWidth = Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(module.getName())+ 5;
		
		Client.INSTANCE.unicodeBasicFontRenderer.drawString(module.getName(), x + width-stringWidth, y ,
				extended ? (module.isState() ? COLOR_ENABLED.getRGB() : Color.white.getRGB()) : Color.white.getRGB());
		if (extended) {
			for (GuiChanger changer : this.changers) {
				changer.extended = true;
				changer.draw(mouseX, mouseY);
			}
		}else {
			for (GuiChanger changer : this.changers) {
				changer.extended = false;
			}
		}
	}

	public void update() {
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (isHovered(mouseX, mouseY)) {
			if (mouseButton == 1) {
				if(module.getSettings().size() > 0) extended = !extended;
			}
			if (mouseButton == 0) {
				this.module.toggle();
			}
		}
		for(GuiChanger changer : this.changers) {
			changer.mouseClicked(mouseX, mouseY, mouseButton);
		}
		return false;
	}

	public void mouseDragged(int mouseX, int mouseY) {
		if (Mouse.isButtonDown(0)) {
			int yAdd = this.y;
			GuiChanger prev = null;
			for (GuiChanger changer : this.changers) {
				changer.x = x;
				changer.y = yAdd+18;
				prev = changer;
				yAdd += prev.height;
			}
		}
	}

	public void mouseReleased() {

	}

	public void onGuiClosed() {
		for(GuiChanger changer : this.changers) {
			if(changer.value != null) changer.setting.getSetting().setValue(changer.value);
		}
	}
	
	public boolean isHovered(float mouseX, float mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y-2.5 && mouseY <= y + baseHeight-2.5;
	}

	public int getHeight() {
		return height;
	}
	
	public int changersAdd() {
		int add = 0;
		try {
			for(GuiChanger changer : this.changers) {
				add += changer.height;
			}			
		} catch (Exception e) {
		}
		return add;
	}
	
}
