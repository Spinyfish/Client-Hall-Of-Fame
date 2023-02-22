package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsButton;

public class GuiButtonRealmsProxy extends GuiButton {
    private final RealmsButton realmsButton;

    public GuiButtonRealmsProxy(final RealmsButton realmsButtonIn, final int buttonId, final int x, final int y, final String text) {
        super(buttonId, x, y, text);
        this.realmsButton = realmsButtonIn;
    }

    public GuiButtonRealmsProxy(final RealmsButton realmsButtonIn, final int buttonId, final int x, final int y, final String text, final int widthIn, final int heightIn) {
        super(buttonId, x, y, widthIn, heightIn, text);
        this.realmsButton = realmsButtonIn;
    }

    public int getId() {
        return super.id;
    }

    public boolean getEnabled() {
        return super.enabled;
    }

    public void setEnabled(final boolean isEnabled) {
        super.enabled = isEnabled;
    }

    public void setText(final String text) {
        super.displayString = text;
    }

    public int getButtonWidth() {
        return super.getButtonWidth();
    }

    public int getPositionY() {
        return super.yPosition;
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.realmsButton.clicked(mouseX, mouseY);
        }

        return super.mousePressed(mc, mouseX, mouseY);
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(final int mouseX, final int mouseY) {
        this.realmsButton.released(mouseX, mouseY);
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    public void mouseDragged(final Minecraft mc, final int mouseX, final int mouseY) {
        this.realmsButton.renderBg(mouseX, mouseY);
    }

    public RealmsButton getRealmsButton() {
        return this.realmsButton;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    public int getHoverState(final boolean mouseOver) {
        return this.realmsButton.getYImage(mouseOver);
    }

    public int func_154312_c(final boolean p_154312_1_) {
        return super.getHoverState(p_154312_1_);
    }

    public int func_175232_g() {
        return this.height;
    }
}
