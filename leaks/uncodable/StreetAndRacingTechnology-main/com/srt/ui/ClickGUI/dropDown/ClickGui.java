package com.srt.ui.ClickGUI.dropDown;

import java.io.IOException;
import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.srt.SRT;
import com.srt.module.ModuleBase;
import com.srt.module.ModuleBase.Category;
import com.srt.ui.ClickGUI.dropDown.impl.Panel;

import net.minecraft.client.gui.GuiScreen;

public class ClickGui extends GuiScreen {
	
	public ArrayList<Panel> panels = new ArrayList<Panel>();
	
	public ClickGui() {
		SRT.instance.moduleManager.sortModules();
		int count = 0;
		
		for(Category category : ModuleBase.Category.values()) {
			panels.add(new Panel(5 + (count * 105),category,this));
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
		for(Panel p : panels) {
			p.drawScreen(mouseX, mouseY, partialTicks);
		}
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
		SRT.instance.moduleManager.getModuleByName("ClickGUI").toggle();
	}
	
}
