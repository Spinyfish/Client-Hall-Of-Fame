package dev.africa.pandaware.impl.ui.menu.button;

import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class CustomButton extends Gui {
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    /**
     * The x position of this control.
     */
    public double xPosition;

    private final boolean customFont;

    /**
     * The y position of this control.
     */
    public double yPosition;
    /**
     * The string displayed on this control.
     */
    public String displayString;
    public int id;
    /**
     * True if this control is enabled, false to disable.
     */
    public boolean enabled;
    /**
     * Hides the button completely if false.
     */
    public boolean visible;
    /**
     * Button width in pixels
     */
    protected double width;
    /**
     * Button height in pixels
     */
    protected double height;
    protected boolean hovered;
    /**
     * Draws this button to the screen.
     */
    private int red = 0, green = 0, blue = 0;

    public CustomButton(int buttonId, int x, int y, String buttonText, boolean customFont) {
        this(buttonId, x, y, 200, 20, buttonText, customFont);
    }

    public CustomButton(int buttonId, double x, double y, double widthIn, double heightIn, String buttonText, boolean customFont) {
        this.width = 200;
        this.height = 20;
        this.enabled = true;
        this.visible = true;
        this.customFont = customFont;
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
    }

    public CustomButton(int buttonId, double x, double y, double widthIn, double heightIn, boolean customFont) {
        this.width = 200;
        this.height = 20;
        this.enabled = true;
        this.visible = true;
        this.id = buttonId;
        this.xPosition = x;
        this.customFont = customFont;
        this.yPosition = y;
        this.width = widthIn;
        this.height = heightIn;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver) {
        int i = 1;

        if (!this.enabled) {
            i = 0;
        } else if (mouseOver) {
            i = 2;
        }

        return i;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            if (hovered && enabled) {
                if (red < 70 || green < 70 || blue < 70)
                    red = green = blue += 5;
            } else {
                if (red > 40 || green > 40 || blue > 40)
                    red = green = blue -= 5;
            }
            red = MathHelper.clamp_int(red, 40, 70);
            green = MathHelper.clamp_int(green, 40, 70);
            blue = MathHelper.clamp_int(blue, 40, 70);
            RenderUtils.drawBorderedRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 1, new Color(red, green, blue, 255).getRGB(), new Color(0, 0, 0, 255).getRGB());
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }
            if (customFont) {
                Fonts.getInstance().getProductSansMedium().drawCenteredStringWithShadow(this.displayString, (float) (this.xPosition + this.width / 2), (float) (this.yPosition + (this.height - 10) / 2), -1);
            } else {
                this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
            }
        }
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int mouseX, int mouseY) {
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    /**
     * Whether the mouse cursor is currently over the button.
     */
    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public double getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
