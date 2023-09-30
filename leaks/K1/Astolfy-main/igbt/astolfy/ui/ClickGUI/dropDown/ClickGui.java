package igbt.astolfy.ui.ClickGUI.dropDown;

import java.io.IOException;
import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import igbt.astolfy.Astolfy;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.module.ModuleBase.Category;
import igbt.astolfy.ui.ClickGUI.dropDown.impl.Panel;
import igbt.astolfy.utils.TimerUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ClickGui extends GuiScreen {
	
	public ArrayList<Panel> panels = new ArrayList<Panel>();
	
	public ClickGui() {
		Astolfy.moduleManager.sortModules();
		int count = 0;
		Panel lastPanel = null;
		for(Category category : ModuleBase.Category.values()) {
			Panel panel = new Panel(lastPanel,5 + (count * 105),category,this);
			panels.add(panel);
			lastPanel = panel;
			count++;
		}
	}

	public static Color getMainColor() {
		return new Color(21,21,21);
	}
	
	public static Color getSecondaryColor() {
		return new Color(25,25,25);
	}
	
	public static Color getThirdColor() {
		return new Color(31,31,31);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Gui.drawRect(0, 0, mc.displayWidth / 2, mc.displayHeight / 2, 0x50000000);
		for(Panel p : panels) {
			p.drawScreen(mouseX, mouseY, partialTicks);
		}
		for(Panel p : panels) {
			p.drawFinish(mouseX, mouseY, partialTicks);
		}
        GlStateManager.color(1.5f, 1.5f, 1.5f,0.7f);
        Gui.drawImage(mc, this.width - 122, this.height - 120, 150,120, new ResourceLocation("Images/MainMenu/MainCon2.png"));
	}

	protected void keyTyped(char typedChat, int keyCode) {
		if(keyCode == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(null);
		} else {
			for(Panel p : panels) {
				p.keyTyped(typedChat, keyCode);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for(Panel p : panels) {
			p.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		for(Panel p : panels) {
			p.mouseReleased(mouseX, mouseY, state);
		}
	}
	
	@Override
	public void onGuiClosed() {
		Astolfy.moduleManager.getModuleByName("ClickGUI").toggle();
	}

	public void onGuiOpened() {
		for(Panel p : panels) {
			p.scale = 0;
			p.timer = new TimerUtils();
		}
	}
	
}
