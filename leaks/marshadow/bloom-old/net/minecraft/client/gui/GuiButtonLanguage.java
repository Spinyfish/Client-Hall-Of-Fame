package net.minecraft.client.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonLanguage extends GuiButton
{
    public GuiButtonLanguage(int buttonID, int xPos, int yPos)
    {
        super(buttonID, xPos, yPos, 20, 20, "");
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(GuiButton.languageTexture);
            boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i = 0;
            this.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, (flag ? new Color(25,25,25,128) : new Color(50,50,50,128)).getRGB());
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawScaledCustomSizeModalRect(this.xPosition, this.yPosition, 0, 0, this.width, this.height, this.width, this.height, this.width, this.height);
        }
    }
}
