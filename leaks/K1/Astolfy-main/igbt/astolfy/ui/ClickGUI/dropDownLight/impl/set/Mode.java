package igbt.astolfy.ui.ClickGUI.dropDownLight.impl.set;

import igbt.astolfy.font.CFontRenderer;
import igbt.astolfy.settings.settings.ModeSetting;
import igbt.astolfy.ui.ClickGUI.dropDownLight.ClickGui;
import igbt.astolfy.ui.ClickGUI.dropDownLight.impl.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Mode extends SetComp {

	private boolean dragging = false;
	private double x;
	private double y;
	private static int height = 12;
	private boolean hovered;
	private ModeSetting set;
	private boolean isOpened = false;

	public Mode(ModeSetting s, Button b) {
		super(s, b, height);
		this.set = s;
	}

	@Override
	public int drawScreen(int mouseX, int mouseY, double x, double y) {
		this.hovered = this.isHovered(mouseX, mouseY);
		this.x = x;
		this.y = y;
		height = 13;
		CFontRenderer font = Minecraft.getMinecraft().customFont;
		String name = this.set.getName() + "";

		Gui.drawRect(x, y, x + this.parent.getWidth(), y + height, ClickGui.getSecondaryColor().getRGB());
		Gui.drawRect(this.x + 1, this.y + 1, this.x + this.parent.getWidth() - 1, this.y + this.height - 1, ClickGui.getSecondaryColor().brighter().getRGB());

		font.drawString(name, (this.x + 3), (y + (font.getHeight() / 2) - 2), new Color(200,200,200).darker().getRGB());
		font.drawString(this.set.getCurrentValue(), (this.x + 100) - font.getStringWidth(this.set.getCurrentValue()) - 3, (y + (font.getHeight() / 2) - 2), 0);
		
		return this.height;
	}

	@Override
	public void mouseClicked(int x, int y, int button) {
		if ((button == 0 || button == 1) && this.hovered) {
				List<String> options = Arrays.asList(this.set.getValue());
				int index = options.indexOf(this.set.getCurrentValue());
				if (button == 0) {
					index++;
				} else if (button == 1) {
					index--;
				}
				if (index >= options.size()) {
					index = 0;
				} else if (index < 0) {
					index = options.size() - 1;
				}
				this.set.setCurrentValue(this.set.getValue()[(index)]);
			}

	}

	private boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + this.parent.getWidth() && mouseY >= y && mouseY <= y + height;
	}
}
