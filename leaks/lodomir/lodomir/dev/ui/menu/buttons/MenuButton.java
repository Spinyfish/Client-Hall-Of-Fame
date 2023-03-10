/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.menu.buttons;

import java.awt.Color;
import lodomir.dev.November;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public final class MenuButton
extends GuiButton {
    private int color = 190;
    private double animation = 0.0;

    public MenuButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 160, 20, buttonText);
    }

    public MenuButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.width = 100;
        this.height = 20;
        this.enabled = true;
        this.visible = true;
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        int i = 1;
        if (!this.enabled) {
            i = 0;
        } else if (mouseOver) {
            i = 2;
        }
        return i;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            boolean bl = this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            if (this.hovered) {
                if (this.color < 255) {
                    this.color += 5;
                }
            } else if (this.color > 170) {
                this.color -= 5;
            }
            RenderUtils.drawRoundedRect(this.xPosition, this.yPosition, this.width, this.height, 10.0, new Color(10, 10, 10, 80).getRGB());
            November.INSTANCE.fm.getFont("SFUI BOLD 20").drawCenteredString(this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, new Color(this.color, this.color, this.color).getRGB());
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    @Override
    public boolean isMouseOver() {
        return this.hovered;
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
    }

    @Override
    public int getButtonWidth() {
        return this.width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }
}

