/**
 * @project Myth
 * @author CodeMan
 * @at 29.10.22, 16:16
 */
package dev.myth.ui;

import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class BetterGuiButton extends GuiButton {
    private final Runnable clickCallback;

    public BetterGuiButton(int buttonId, int x, int y, String buttonText, Runnable clickCallback) {
        this(buttonId, x, y, 200, 20, buttonText, clickCallback);
    }

    public BetterGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, Runnable clickCallback) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.clickCallback = clickCallback;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            Color color = new Color(0, 0, 0, hovered ? 150 : 120);
            RenderUtil.drawRoundedRect(this.xPosition, this.yPosition, this.width, this.height, this.height / 4f, color.getRGB(), 0, 0);
            this.mouseDragged(mc, mouseX, mouseY);
            GlStateManager.disableBlend();
            FontLoaders.SFUI_20.drawCenteredString(this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, this.hovered ? Color.MAGENTA.darker().getRGB() : 14737632);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            clickCallback.run();
            return true;
        }
        return false;
    }
}
