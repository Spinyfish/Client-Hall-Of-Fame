package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonLanguage extends GuiButton {
    public GuiButtonLanguage(final int buttonID, final int xPos, final int yPos) {
        super(buttonID, xPos, yPos, 20, 20, "");
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(GuiButton.buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            final boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i = 106;

            if (flag) {
                i += this.height;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, i, this.width, this.height);
        }
    }
}
