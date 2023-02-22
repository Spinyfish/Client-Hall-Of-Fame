package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiSlider extends GuiButton {
    private float sliderPosition = 1.0F;
    public boolean isMouseDown;
    private final String name;
    private final float min;
    private final float max;
    private final GuiPageButtonList.GuiResponder responder;
    private final GuiSlider.FormatHelper formatHelper;

    public GuiSlider(final GuiPageButtonList.GuiResponder guiResponder, final int idIn, final int x, final int y, final String name, final float min, final float max, final float defaultValue, final GuiSlider.FormatHelper formatter) {
        super(idIn, x, y, 150, 20, "");
        this.name = name;
        this.min = min;
        this.max = max;
        this.sliderPosition = (defaultValue - min) / (max - min);
        this.formatHelper = formatter;
        this.responder = guiResponder;
        this.displayString = this.getDisplayString();
    }

    public float func_175220_c() {
        return this.min + (this.max - this.min) * this.sliderPosition;
    }

    public void func_175218_a(final float p_175218_1_, final boolean p_175218_2_) {
        this.sliderPosition = (p_175218_1_ - this.min) / (this.max - this.min);
        this.displayString = this.getDisplayString();

        if (p_175218_2_) {
            this.responder.onTick(this.id, this.func_175220_c());
        }
    }

    public float func_175217_d() {
        return this.sliderPosition;
    }

    private String getDisplayString() {
        return this.formatHelper == null ? I18n.format(this.name) + ": " + this.func_175220_c() : this.formatHelper.getText(this.id, I18n.format(this.name), this.func_175220_c());
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(final boolean mouseOver) {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(final Minecraft mc, final int mouseX, final int mouseY) {
        if (this.visible) {
            if (this.isMouseDown) {
                this.sliderPosition = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);

                if (this.sliderPosition < 0.0F) {
                    this.sliderPosition = 0.0F;
                }

                if (this.sliderPosition > 1.0F) {
                    this.sliderPosition = 1.0F;
                }

                this.displayString = this.getDisplayString();
                this.responder.onTick(this.id, this.func_175220_c());
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int) (this.sliderPosition * (float) (this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int) (this.sliderPosition * (float) (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    public void func_175219_a(final float p_175219_1_) {
        this.sliderPosition = p_175219_1_;
        this.displayString = this.getDisplayString();
        this.responder.onTick(this.id, this.func_175220_c());
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderPosition = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);

            if (this.sliderPosition < 0.0F) {
                this.sliderPosition = 0.0F;
            }

            if (this.sliderPosition > 1.0F) {
                this.sliderPosition = 1.0F;
            }

            this.displayString = this.getDisplayString();
            this.responder.onTick(this.id, this.func_175220_c());
            this.isMouseDown = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(final int mouseX, final int mouseY) {
        this.isMouseDown = false;
    }

    public interface FormatHelper {
        String getText(int id, String name, float value);
    }
}
