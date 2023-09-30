package de.fanta.clickgui.intellij;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;

public class ClickGuiScreen extends GuiScreen{
	
	private GuiScreen parent;
	private ClickGuiMainPane mainPane;
	
	public ClickGuiScreen(GuiScreen parent) {
		this.parent = parent;
	}
	
	@Override
	public void initGui() {
		this.mainPane = new ClickGuiMainPane(this);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.mainPane.draw(mouseX, mouseY);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		this.mainPane.keyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	public void onGuiClosed() {
		this.mainPane.onClose();
	}
	
	@Override
	public void updateScreen() {
		this.mainPane.update();
	}
	
}
