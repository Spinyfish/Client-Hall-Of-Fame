package rip.athena.client.gui.framework.components;

import rip.athena.client.gui.framework.*;
import rip.athena.client.cosmetics.cape.*;
import rip.athena.client.theme.impl.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.gui.framework.draw.*;

public class MenuButton extends MenuComponent
{
    protected String text;
    protected int minOffset;
    protected boolean mouseDown;
    protected boolean active;
    protected Cape cape;
    protected AccentTheme theme;
    protected PrimaryTheme primaryTheme;
    protected ButtonState lastState;
    
    public MenuButton(final String text, final int x, final int y) {
        super(x, y, -1, -1);
        this.minOffset = 2;
        this.mouseDown = false;
        this.active = false;
        this.lastState = ButtonState.NORMAL;
        this.text = text;
    }
    
    public MenuButton(final String text, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.minOffset = 2;
        this.mouseDown = false;
        this.active = false;
        this.lastState = ButtonState.NORMAL;
        this.text = text;
    }
    
    public MenuButton(final AccentTheme theme, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.minOffset = 2;
        this.mouseDown = false;
        this.active = false;
        this.lastState = ButtonState.NORMAL;
        this.theme = theme;
    }
    
    public MenuButton(final Cape cape, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.minOffset = 2;
        this.mouseDown = false;
        this.active = false;
        this.lastState = ButtonState.NORMAL;
        this.cape = cape;
    }
    
    public MenuButton(final PrimaryTheme theme, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.minOffset = 2;
        this.mouseDown = false;
        this.active = false;
        this.lastState = ButtonState.NORMAL;
        this.primaryTheme = theme;
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(65, 65, 65, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(50, 50, 50, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(75, 75, 75, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(65, 65, 65, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(50, 50, 50, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(65, 65, 65, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(200, 200, 200, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(225, 225, 225, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
    }
    
    @Override
    public void onMouseClick(final int button) {
        if (button == 0) {
            this.mouseDown = true;
        }
    }
    
    @Override
    public boolean passesThrough() {
        if (this.disabled || this.parent == null) {
            return true;
        }
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        return !this.mouseDown || mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height + 1;
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
                    this.onAction();
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
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        DrawImpl.drawRect(x + 1, y + 1, width - 1, height - 1, backgroundColor);
        this.drawHorizontalLine(x, y, width + 1, 1, lineColor);
        this.drawVerticalLine(x, y + 1, height - 1, 1, lineColor);
        this.drawHorizontalLine(x, y + height, width + 1, 1, lineColor);
        this.drawVerticalLine(x + width, y + 1, height - 1, 1, lineColor);
        this.drawText(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + (height / 2 - this.getStringHeight(this.text) / 2), textColor);
        this.mouseDown = false;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public void onAction() {
    }
}
