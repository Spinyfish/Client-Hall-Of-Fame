package net.minecraft.client.gui;

import rip.athena.client.utils.animations.simple.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import java.awt.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.audio.*;

public class GuiButton extends Gui
{
    private SimpleAnimation lineHoverAnimation;
    private SimpleAnimation rectHoverAnimation;
    protected static final ResourceLocation buttonTextures;
    int opacity;
    protected int width;
    protected int height;
    public int xPosition;
    public int yPosition;
    public String displayString;
    public int id;
    public boolean enabled;
    public boolean visible;
    protected boolean hovered;
    
    public GuiButton(final int buttonId, final int x, final int y, final String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }
    
    public GuiButton(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
        this.lineHoverAnimation = new SimpleAnimation(0.0f);
        this.rectHoverAnimation = new SimpleAnimation(100.0f);
        this.width = 200;
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
    
    protected int getHoverState(final boolean mouseOver) {
        int i = 1;
        if (!this.enabled) {
            i = 0;
        }
        else if (mouseOver) {
            i = 2;
        }
        return i;
    }
    
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(GuiButton.buttonTextures);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.mouseDragged(mc, mouseX, mouseY);
            if (this.hovered) {
                this.opacity += 25;
                if (this.opacity > 150) {
                    this.opacity = 150;
                }
            }
            else {
                this.opacity -= 25;
                if (this.opacity < 100) {
                    this.opacity = 100;
                }
            }
            Color rectCol;
            if (!this.enabled) {
                rectCol = new Color(10, 10, 10, 120);
            }
            else {
                rectCol = new Color(150, 150, 150, this.opacity);
            }
            RoundedUtils.drawRound((float)(this.xPosition + 1), (float)(this.yPosition + 1), (float)(this.width - 1), (float)(this.height - 2), 4.0f, rectCol);
            RoundedUtils.drawRoundedGradientOutlineCorner((float)this.xPosition, (float)(this.yPosition + 1), (float)(this.width + this.xPosition), (float)(this.height + this.yPosition - 1), 1.0f, 6.0f, ColorUtil.getClientColor(0, 255).getRGB(), ColorUtil.getClientColor(90, 255).getRGB(), ColorUtil.getClientColor(180, 255).getRGB(), ColorUtil.getClientColor(270, 255).getRGB());
            if (this.hovered && this.enabled) {
                if (this.lineHoverAnimation.getValue() <= this.width / 2.0f - 2.0f) {
                    this.lineHoverAnimation.setAnimation(this.width / 2.0f - 2.0f, 10.0);
                    Gui.drawHorizontalLine(this.xPosition + this.width / 2, (int)(this.xPosition + this.width / 2 - this.lineHoverAnimation.getValue()), this.yPosition + this.height - 1, -1);
                    Gui.drawHorizontalLine(this.xPosition + this.width / 2, (int)(this.xPosition + this.width / 2 + this.lineHoverAnimation.getValue()), this.yPosition + this.height - 1, -1);
                }
                else if (this.lineHoverAnimation.getValue() >= this.width / 2.0f) {
                    Gui.drawHorizontalLine(this.xPosition + this.width / 2, (int)(this.xPosition + this.width / 2 - this.lineHoverAnimation.getValue()), this.yPosition + this.height - 1, -1);
                    Gui.drawHorizontalLine(this.xPosition + this.width / 2, (int)(this.xPosition + this.width / 2 + this.lineHoverAnimation.getValue()), this.yPosition + this.height - 1, -1);
                }
            }
            else if (!this.hovered) {
                this.lineHoverAnimation.setAnimation(0.0f, 4.0);
                if (this.lineHoverAnimation.getValue() > 0.0f && this.lineHoverAnimation.getValue() > 2.0f) {
                    Gui.drawHorizontalLine(this.xPosition + this.width / 2, (int)(this.xPosition + this.width / 2 - this.lineHoverAnimation.getValue() - 1.0f), this.yPosition + this.height - 1, -1);
                    Gui.drawHorizontalLine(this.xPosition + this.width / 2, (int)(this.xPosition + this.width / 2 + this.lineHoverAnimation.getValue() + 1.0f), this.yPosition + this.height - 1, -1);
                }
            }
            FontManager.getProductSansBold(19).drawCenteredString(this.displayString.toUpperCase(), this.xPosition + (float)(this.width / 2), this.enabled ? ((double)(this.yPosition + (float)(this.height / 2) - 3.0f)) : ((double)(this.yPosition + (float)(this.height / 2) - 6.0f)), -1);
            if (!this.enabled) {
                FontManager.getProductSansBold(10).drawCenteredString("DISABLED", this.xPosition + (float)(this.width / 2), this.yPosition + (float)(this.height / 2) + 4.0f, -1);
            }
        }
    }
    
    protected void mouseDragged(final Minecraft mc, final int mouseX, final int mouseY) {
    }
    
    public void mouseReleased(final int mouseX, final int mouseY) {
    }
    
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }
    
    public boolean isMouseOver() {
        return this.hovered;
    }
    
    public void drawButtonForegroundLayer(final int mouseX, final int mouseY) {
    }
    
    public void playPressSound(final SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
    }
    
    public int getButtonWidth() {
        return this.width;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
    
    static {
        buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    }
}
