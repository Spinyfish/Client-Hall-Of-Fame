package axolotl.ui.ClickGUI.dropDown.impl.set;

import java.awt.Color;

import axolotl.cheats.settings.BooleanSetting;
import font.CFontRenderer;
import axolotl.ui.ClickGUI.dropDown.ClickGui;
import axolotl.ui.ClickGUI.dropDown.impl.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class Checkbox extends SetComp {

	private boolean dragging = false;
	private double x;
	private double y;
	private static int height = 15;
	private boolean hovered;
	private BooleanSetting set;
	
	public Checkbox(BooleanSetting s, Button b) {
		super(s, b,height);
		this.set = s;
	}
	
	@Override
	public int drawScreen(int mouseX, int mouseY, double x, double y) {
		this.hovered = this.isHovered(mouseX, mouseY);
		this.x = x;
		this.y = y;
		height = 12;
		CFontRenderer font = Minecraft.getMinecraft().customFont;

		//Gui.drawRect(x - 1, y, x + this.parent.getWidth() + 1, y + height + 1, ClickGui.getSecondaryColor().getRGB());
		Gui.drawRect(this.x, this.y, this.x + this.parent.getWidth(), this.y + this.height, ClickGui.getSecondaryColor().brighter().getRGB());
		String name = this.set.name;
		font.drawString(name, (this.x + 3), y + 1, -1);
		Gui.drawRect(this.x + this.parent.getWidth() - 11, this.y + 1 + 1, this.x + this.parent.getWidth() - 3, this.y + 8 + 2, ((BooleanSetting)this.set).getBValue() ? new Color(42, 199, 84).getRGB() : new Color(17, 17, 17).getRGB());
		GlStateManager.color(1, 1, 1);
		float x1 = ((BooleanSetting)this.set).getBValue() ? 5 : 1.5F;
		float x2 = ((BooleanSetting)this.set).getBValue() ? 3 : -0.5F;
		
		float x1Diff = x1 - this.lastX1;
		float x2Diff = x2 - this.lastX2;
		this.lastX1 += x1Diff / 4;
		this.lastX2 += x2Diff / 4;
		//RenderUtils.drawRoundedRect(this.x + lastX1 + 2, this.y + 5, this.x + 10 + lastX2, this.y + 11, new Color(0, 0, 0, 0).getRGB(), new Color(255, 255, 255).getRGB());
		
		return this.height;
	}
	
	private float lastX1 = 1.5F;
	private float lastX2 = -0.5F;
	
	private float red = 0.70588235294F;
	private float green = 0.70588235294F;
	private float blue = 0.70588235294F;
	
	@Override
	public void mouseClicked(int x, int y, int button) {
		if (button == 0 && this.hovered)  {
			this.set.setValue(!(((BooleanSetting)this.set)).getBValue());
		}
	}
	
	private boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + this.parent.getWidth() && mouseY > y && mouseY < y + height;
	}
}
