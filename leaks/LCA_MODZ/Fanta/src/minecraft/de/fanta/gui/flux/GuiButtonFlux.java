package de.fanta.gui.flux;

import java.awt.Color;

import de.fanta.gui.font.ClientFont;
import de.fanta.gui.font.GlyphPageFontRenderer;
import de.fanta.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonFlux extends GuiButton {

	GlyphPageFontRenderer fluxButton;
	
	public GuiButtonFlux(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		fluxButton = ClientFont.font(15, "Vision", true);
	}

	public GuiButtonFlux(int buttonId, int x, int y, String buttonText) {
		super(buttonId, x, y, buttonText);
		fluxButton = ClientFont.font(15, "Vision", true);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		int unhovered = Color.decode("#0FA292").getRGB();
		int hovered = Color.decode("#0C8275").getRGB();
		int blockedunhovered = Color.decode("#7D7D7D").getRGB();
		int blockedhovered = Color.decode("#646464").getRGB();
		this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width
				&& mouseY < this.yPosition + this.height;
		RenderUtil.drawRoundedRect(xPosition, yPosition, width, height, 5,
				enabled ? this.hovered ? hovered : unhovered : this.hovered ? blockedhovered : blockedunhovered);
		RenderUtil.glColor(Color.white.getRGB());
		fluxButton.drawCenteredString(displayString, xPosition+(width/2), yPosition+height/2-fluxButton.getFontHeight()/2, Color.white.getRGB(), false);
	}

	
	
}
