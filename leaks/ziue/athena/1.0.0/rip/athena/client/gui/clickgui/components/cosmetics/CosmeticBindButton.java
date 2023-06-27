package rip.athena.client.gui.clickgui.components.cosmetics;

import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import rip.athena.client.utils.render.*;

public class CosmeticBindButton extends CosmeticGenericButton
{
    private String type;
    private boolean binding;
    
    public CosmeticBindButton(final int x, final int y, final int width, final int height) {
        super("", x, y, width, height, true);
        this.type = "";
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(43, 42, 48, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(32, 31, 36, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(57, 56, 64, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(74, 74, 75, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(101, 101, 103, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(88, 88, 90, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(118, 118, 120, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
    }
    
    @Override
    public void onPreSort() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = this.active ? ButtonState.ACTIVE : ButtonState.NORMAL;
        if (!this.disabled) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height + 1) {
                state = ButtonState.HOVER;
                if (this.mouseDown) {
                    this.active = !this.active;
                    this.onBind();
                }
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        this.lastState = state;
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        if (this.filledBackground) {}
        DrawUtils.drawRoundedRect(x, y, x + width, y + height, 4.0f, new Color(50, 50, 50, 255).getRGB());
        DrawUtils.drawRoundedRect(x + 1, y + 1, x + width - 1, y + height - 1, 4.0f, new Color(35, 35, 35, 255).getRGB());
        String text = this.text;
        if (this.active) {
            text = "NOW CLICK AN EMOTE.";
        }
        else if (this.type.length() > 0) {
            text = this.type;
        }
        else {
            text = "EMPTY";
        }
        this.drawText(text, x + (width / 2 - this.getStringWidth(text) / 2), y + (height / 2 - this.getStringHeight(text) / 2), textColor);
        this.mouseDown = false;
    }
    
    private void onBind() {
        this.onAction();
    }
    
    public void setBind(final String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
}
