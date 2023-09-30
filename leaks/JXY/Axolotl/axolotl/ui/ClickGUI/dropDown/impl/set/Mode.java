package axolotl.ui.ClickGUI.dropDown.impl.set;

import java.awt.Color;
import java.util.List;

import axolotl.cheats.settings.ModeSetting;
import font.CFontRenderer;
import axolotl.ui.ClickGUI.dropDown.ClickGui;
import axolotl.ui.ClickGUI.dropDown.impl.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Mode extends SetComp {

	private boolean dragging = false;
	private double x;
	private double y;
	private static int height = 12;
	private boolean hovered;
	private ModeSetting set;
	private boolean isOpened = false;
	private Button b;

	public Mode(ModeSetting s, Button b) {
		super(s, b, height);
		this.set = s;
		this.b = b;
	}

	@Override
	public int drawScreen(int mouseX, int mouseY, double x, double y) {
		this.hovered = this.isHovered(mouseX, mouseY);
		this.x = x;
		this.y = y;
		height = 13;
		CFontRenderer font = Minecraft.getMinecraft().customFont;
		String name = this.set.name + "";

		Gui.drawRect(x, y, x + this.parent.getWidth(), y + height, ClickGui.getSecondaryColor().getRGB());
		Gui.drawRect(this.x + 1, this.y + 1, this.x + this.parent.getWidth() - 1, this.y + this.height - 1, ClickGui.getSecondaryColor().brighter().getRGB());

		font.drawString(name, (this.x + 3), (y + (font.getHeight() / 2) - 2), new Color(255,255,255).darker().getRGB());
		font.drawString(this.set.getSpecificValue(), (this.x + 100) - font.getStringWidth(this.set.getSpecificValue()) - 3, (y + (font.getHeight() / 2) - 2), -1);
		
		return this.height;
	}

	@Override
	public void mouseClicked(int x, int y, int button) {
		if ((button == 0 || button == 1) && this.hovered) {
			this.b.removeSettings(this.set.getSettingCluster(this.set.getMode()).getSettings());
			List<String> options = this.set.getModes();
			int index = options.indexOf(this.set.getSpecificValue());
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
			this.set.setValue(index);
			this.b.createSettings(this.set.getSettingCluster(this.set.getMode()).getSettings());
		}

	}

	private boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + this.parent.getWidth() && mouseY >= y && mouseY <= y + height;
	}
}
