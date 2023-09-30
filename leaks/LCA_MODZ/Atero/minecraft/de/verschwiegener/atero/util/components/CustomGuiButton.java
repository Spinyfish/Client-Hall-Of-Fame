package de.verschwiegener.atero.util.components;

import java.awt.Color;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.ui.guiingame.CustomGUIIngame;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class CustomGuiButton extends GuiButton {
    
    boolean light;
    Font font;

    public CustomGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
	super(buttonId, x, y, widthIn, heightIn, buttonText);
	this.enabled = true;
	this.visible = true;
	this.id = buttonId;
	this.xPosition = x;
	this.yPosition = y;
	this.width = widthIn;
	this.height = heightIn;
	this.displayString = buttonText;
	font = Management.instance.font;
    }
    public CustomGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, boolean light) {
	super(buttonId, x, y, widthIn, heightIn, buttonText);
	this.enabled = true;
	this.visible = true;
	this.id = buttonId;
	this.xPosition = x;
	this.yPosition = y;
	this.width = widthIn;
	this.height = heightIn;
	this.displayString = buttonText;
	this.light = light;
	font = Management.instance.font;
    }

    public CustomGuiButton(int buttonId, int x, int y, String buttonText) {
	this(buttonId, x, y, 200, 20, buttonText);
    }
    public void setLight(boolean light) {
	this.light = light;
    }
    public CustomGuiButton setFont(Font font) {
	this.font = font;
	return this;
    }
    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
	if (this.visible) {
	    //Fontrenderer fontRenderer = Management.instance.fontmgr.getFontByName("ArrayListFont");
	    if (!light) {
		RenderUtil.fillRect(xPosition, yPosition, width, height, Management.instance.colorGray);
		font.drawString(this.displayString, xPosition + width / 2 - font.getStringWidth2(displayString),
			(this.yPosition) + (font.getBaseStringHeight() / 4),
			Color.WHITE.getRGB());
		RenderUtil.fillRect(this.xPosition, this.yPosition + this.height - 1, this.width, 1, Management.instance.colorBlue);
	    } else {
		font.drawString(this.displayString,
			((this.xPosition)),
			(this.yPosition) + (font.getBaseStringHeight() / 2),
			Color.WHITE.getRGB());
	    }
	}
    }
}
