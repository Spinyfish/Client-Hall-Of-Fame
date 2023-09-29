package de.fanta.clickgui.astolfo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.google.gson.annotations.Expose;

import de.fanta.Client;
import de.fanta.clickgui.astolfo.objects.GuiChanger;
import de.fanta.gui.font.ClientFont;
import de.fanta.gui.font.GlyphPageFontRenderer;
import de.fanta.module.Module;
import de.fanta.module.Module.Type;
import de.fanta.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class ClickGuiPane extends Gui{


	@Expose
	public int x, y;
	private int width = 125, height = 20;
	@Expose
	public boolean extended;
	@Expose
	public Type type;

	private ClickGuiScreen screen;

	public List<ClickGuiModulePane> modulePanes = new ArrayList<ClickGuiModulePane>();

	private Color BACKGROUND_COLOR = Color.decode("#1A1A1A"), BACKGROUND_COLOR2 = Color.decode("#252525");

	public ClickGuiPane(Type type, int x, int y, ClickGuiScreen screen) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.screen = screen;
		int y2 = y;
		for (Module module : Client.INSTANCE.moduleManager.modules) {
			if (module.getType() == this.type) {
				ClickGuiModulePane modPane = new ClickGuiModulePane(module, x, y2, screen, this);
				modulePanes.add(modPane);
				y2 += modPane.getHeight();
			}
		}
	}

	private GlyphPageFontRenderer fontRenderer = ClientFont.font(20, "AstolfoClickgui-IconFont", true);
	private char show = 'g', hide = 'i';
	public void draw(float mouseX, float mouseY) {
		update();
		int height2 = 0;
		for(ClickGuiModulePane pane : this.modulePanes) {
			height2 += pane.getHeight();
		}
		drawRect(x, y, x+width, y+height, BACKGROUND_COLOR.getRGB());
		if (extended) {
			drawRect(x, y + height, x + width, y + height+height2, BACKGROUND_COLOR.getRGB());
		}
		int y2 = y + height;
		for (ClickGuiModulePane pane : this.modulePanes) {
			if (extended) {
				pane.draw(mouseX, mouseY);
			}
			pane.x = this.x;
			pane.y = y2;
			y2 += pane.getHeight();

			int yAdd = pane.y;
			GuiChanger prev = null;
			for (GuiChanger changer : pane.changers) {
				changer.x = pane.x;
				changer.y = yAdd+18;
				prev = changer;
				yAdd += prev.height;
			}
		}
//		drawRect(x, y, x + width, y + height, HOVER_COLOR.getRGB());
		if(!extended) {
			drawHollowRect(x-.7, y-.5, width+1.3, height+.5, .7, type.getColor());
		}else {
			drawHollowRect(x-.7, y-.5, width+1.3, height+height2, .7, type.getColor());
		}
		Client.INSTANCE.unicodeBasicFontRenderer.drawString( type.name(), x + 2 , y + 2.5F,
					Color.white.getRGB());
		fontRenderer.drawString(""+type.getIcon(), x+width-15, y+3.5F, type.getColor());
		fontRenderer.drawString(extended ? ""+show : ""+hide, x+width-27.5F, y+3.5F, extended ? type.getColor() : ColorUtils.getMulitpliedColor(BACKGROUND_COLOR2.getRGB(), 1.3));
	}

	public void update() {
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (isHovered(mouseX, mouseY)) {
			if (mouseButton == 1) {
				extended = !extended;
			}
		}
		if (extended) {
			for (ClickGuiModulePane pane : this.modulePanes) {
				pane.mouseClicked(mouseX, mouseY, mouseButton);
			}
		}
		return false;
	}

	public void mouseDragged(int mouseX, int mouseY) {
		if (screen.selectedPane == this) {
			this.x = mouseX - width / 2;
			this.y = mouseY - height / 2;
		}
		if (screen.selectedPane != this)
			screen.selectedPane.mouseDragged(mouseX, mouseY);
		int y2 = y + height;
		for (ClickGuiModulePane pane : this.modulePanes) {
			pane.x = this.x;
			pane.y = y2;
			y2 += pane.getHeight();

			if (Mouse.isButtonDown(0)) {
				int yAdd = pane.y;
				GuiChanger prev = null;
				for (GuiChanger changer : pane.changers) {
					changer.x = pane.x;
					changer.y = yAdd+18;
					prev = changer;
					yAdd += prev.height;
				}
			}
		}
	}

	public void mouseReleased() {

	}

	public void onGuiClosed() {
		for (ClickGuiModulePane pane : this.modulePanes) {
			pane.onGuiClosed();
		}
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	
}
