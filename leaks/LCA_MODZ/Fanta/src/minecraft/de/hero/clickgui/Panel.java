package de.hero.clickgui;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.utils.RenderUtil;
import de.hero.clickgui.elements.ModuleButton;
import de.hero.clickgui.util.ColorUtil;
import de.hero.clickgui.util.FontUtil;
import de.hero.clickgui.util.McOutlineHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Made by HeroCode it's free to use but you have to credit me
 *
 * @author HeroCode
 */
public class Panel {
	public String title;
	public double x;
	public double y;
	private double x2;
	private double y2;
	public double width;
	public double height;
	public boolean dragging;
	public boolean extended;
	public boolean visible;
	public ArrayList<ModuleButton> Elements = new ArrayList<>();
	public ClickGUI clickgui;

	/*
	 * Konstrukor
	 */
	public Panel(String ititle, double ix, double iy, double iwidth, double iheight, boolean iextended,
			ClickGUI parent) {
		this.title = ititle;
		this.x = ix;
		this.y = iy;
		this.width = iwidth;
		this.height = iheight;
		this.extended = iextended;
		this.dragging = false;
		this.visible = true;
		this.clickgui = parent;
		setup();
	}

	/*
	 * Wird in ClickGUI berschrieben, sodass auch ModuleButtons hinzugefgt werden
	 * knnen :3
	 */
	public void setup() {
	}

	/*
	 * Rendern des Elements.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (!this.visible)
			return;

		if (this.dragging) {
			x = x2 + mouseX;
			y = y2 + mouseY;
		}

		Color temp = ColorUtil.getClickGUIColor().darker();
		double mcOutlineEnd = y+height;

		//int outlineColor = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 170).getRGB();
		
		//Gui.drawRect2(x, y, x + width, y + height, extended ? new Color(20, 200, 30).getRGB() : new Color(45, 45, 45).getRGB());// 0xff121212
		
		//RenderUtil.drawRoundedRect2(x - 2, y, width + 4, height, 4, extended ? Color.red : new Color(45, 45, 45));
		
		
//		McOutlineHelper.start();
//		RenderUtil.drawRoundedRect2(x - 2, y, width + 4, height, 4, Color.red);
		//Gui.drawRect2(x, y, x + width, y + height,new Color(20,255,100, 255).getRGB());
//		McOutlineHelper.stop();
		Gui.drawRect2(x, y, x + width, y + height,new Color(0,255,0, 255).getRGB());
		// Gui.drawRect2(x - 2, y, x, y + height, outlineColor);
		FontUtil.drawString(title, x + 2, y + height / 2 - FontUtil.getFontHeight() / 2, 0xffefefef);

		FontUtil.drawString(extended ? "Ʌ" : "V", x + width - 10, y + height / 2 - FontUtil.getFontHeight() / 2,
				0xffefefef);

		if (this.extended && !Elements.isEmpty()) {
			double startY = y + height;
			int epanelcolor = 0xff232323;
			for (ModuleButton et : Elements) {
				// Gui.drawRect2(x - 2, startY, x + width, startY + et.height + 1,
				// outlineColor);

				
			//	Client.blurHelper.blur2((float)x, (float)startY - 1f, (float)x + (float)width , (float)startY + (float)et.height + 1 + 4f, 10);
			Gui.drawRect2(x, startY, x + width, startY + et.height + 1, new Color(38, 38, 38, 210).getRGB());
				et.x = x;
				et.y = startY;
				et.width = width;
				et.drawScreen(mouseX, mouseY, partialTicks);
				startY += et.height + (et.extended ? et.additionalHeight : 0) + 1;
			}
			mcOutlineEnd = startY;
		}
		
//		McOutlineHelper.start();
//		RenderUtil.drawRoundedRect2(x - 2, y, width + 4, height, 4, Color.red);
//		Gui.drawRect2(x, y + height, x + width, mcOutlineEnd, Color.red.getRGB());
//		McOutlineHelper.stop();
//		McOutlineHelper.render();
	}

	/*
	 * Zum Bewegen und Extenden des PanelsS usw.
	 */
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!this.visible) {
			return false;
		}
		if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
			x2 = this.x - mouseX;
			y2 = this.y - mouseY;
			dragging = true;
			return true;
		} else if (mouseButton == 1 && isHovered(mouseX, mouseY)) {
			extended = !extended;
			return true;
		} else if (extended) {
			for (ModuleButton et : Elements) {
				if (et.mouseClicked(mouseX, mouseY, mouseButton)) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Damit das Panel auch losgelassen werden kann
	 */
	public void mouseReleased(int mouseX, int mouseY, int state) {
		if (!this.visible) {
			return;
		}
		if (state == 0) {
			this.dragging = false;
		}
	}

	/*
	 * HoverCheck
	 */
	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
}
