package rip.athena.client.gui.framework.components;

import java.awt.*;
import rip.athena.client.gui.framework.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import rip.athena.client.gui.framework.draw.*;

public class MenuLabel extends MenuComponent
{
    protected String text;
    protected String tooltip;
    protected ButtonState lastState;
    protected int minOffset;
    protected boolean mouseDown;
    
    public MenuLabel(final String text, final String tooltip, final int x, final int y) {
        super(x, y, 0, 0);
        this.lastState = ButtonState.NORMAL;
        this.minOffset = 2;
        this.mouseDown = false;
        this.text = text;
        this.tooltip = tooltip;
    }
    
    public MenuLabel(final String text, final int x, final int y) {
        super(x, y, 0, 0);
        this.lastState = ButtonState.NORMAL;
        this.minOffset = 2;
        this.mouseDown = false;
        this.text = text;
        this.tooltip = "";
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.POPUP, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(100, 120, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(200, 200, 200, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(225, 225, 225, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.POPUP, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(200, 200, 200, 255));
    }
    
    @Override
    public boolean passesThrough() {
        if (this.disabled) {
            return true;
        }
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        return !this.mouseDown || mouseX < x || mouseX > x + this.width || mouseY < y || mouseY > y + this.height + 1;
    }
    
    @Override
    public void onMouseClick(final int button) {
        if (button == 0) {
            this.mouseDown = true;
        }
    }
    
    @Override
    public void onPreSort() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = ButtonState.NORMAL;
        if (!this.disabled) {
            if (mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height) {
                state = ButtonState.HOVER;
                if (this.mouseDown) {
                    this.onAction();
                }
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        if (state == ButtonState.HOVER || state == ButtonState.HOVERACTIVE) {
            this.setPriority(MenuPriority.HIGHEST);
        }
        else {
            this.setPriority(MenuPriority.LOW);
        }
        this.lastState = state;
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        this.drawText(this.text, x, y, this.getColor(DrawType.TEXT, this.lastState));
        this.drawTooltip();
        this.mouseDown = false;
    }
    
    public void drawTooltip() {
        if (this.tooltip.length() > 0 && (this.lastState == ButtonState.HOVER || this.lastState == ButtonState.HOVERACTIVE)) {
            final int tipWidth = this.getStringWidth(this.tooltip) + this.minOffset * 2;
            final int tipHeight = this.getStringHeight(this.tooltip) + this.minOffset * 2;
            final int lineColor = this.getColor(DrawType.LINE, ButtonState.POPUP);
            int mouseX = this.parent.getMouseX();
            final int mouseY = this.parent.getMouseY() - tipHeight;
            final ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            if (mouseX + tipWidth >= res.getScaledWidth()) {
                mouseX -= tipWidth;
            }
            DrawImpl.drawRect(mouseX, mouseY, tipWidth, tipHeight, this.getColor(DrawType.BACKGROUND, ButtonState.POPUP));
            this.drawHorizontalLine(mouseX, mouseY, tipWidth + 1, 1, lineColor);
            this.drawVerticalLine(mouseX, mouseY + 1, tipHeight - 1, 1, lineColor);
            this.drawHorizontalLine(mouseX, mouseY + tipHeight, tipWidth + 1, 1, lineColor);
            this.drawVerticalLine(mouseX + tipWidth, mouseY + 1, tipHeight - 1, 1, lineColor);
            this.drawText(this.tooltip, mouseX + this.minOffset, mouseY + this.minOffset, this.getColor(DrawType.TEXT, ButtonState.POPUP));
        }
    }
    
    @Override
    public int getHeight() {
        return this.getStringHeight(this.text);
    }
    
    @Override
    public int getWidth() {
        return this.getStringWidth(this.text);
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public String getTooltip() {
        return this.tooltip;
    }
    
    public void setTooltip(final String tooltip) {
        this.tooltip = tooltip;
    }
    
    public void onAction() {
    }
}
