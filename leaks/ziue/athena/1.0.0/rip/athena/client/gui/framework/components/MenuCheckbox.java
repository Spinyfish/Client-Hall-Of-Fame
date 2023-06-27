package rip.athena.client.gui.framework.components;

import java.awt.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import rip.athena.client.*;

public class MenuCheckbox extends MenuComponent
{
    protected String text;
    protected String tooltip;
    protected boolean checked;
    protected ButtonState lastState;
    protected boolean checkable;
    protected boolean wantsToBind;
    protected int textOffset;
    protected int minOffset;
    protected int optionWindowWidth;
    protected int optionWindowHeight;
    protected boolean mouseDown;
    protected Point startPos;
    
    public MenuCheckbox(final String text, final String tooltip, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.lastState = ButtonState.NORMAL;
        this.checkable = true;
        this.wantsToBind = false;
        this.textOffset = 4;
        this.minOffset = 2;
        this.optionWindowWidth = 75;
        this.optionWindowHeight = 36;
        this.mouseDown = false;
        this.startPos = null;
        this.text = text;
        this.tooltip = tooltip;
    }
    
    public MenuCheckbox(final String text, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.lastState = ButtonState.NORMAL;
        this.checkable = true;
        this.wantsToBind = false;
        this.textOffset = 4;
        this.minOffset = 2;
        this.optionWindowWidth = 75;
        this.optionWindowHeight = 36;
        this.mouseDown = false;
        this.startPos = null;
        this.text = text;
        this.tooltip = "";
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(81, 108, 255, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(50, 50, 50, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(100, 120, 255, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.POPUP, new Color(10, 10, 10, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(20, 20, 20, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(30, 30, 30, 255));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(100, 120, 255, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(200, 200, 200, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(225, 225, 225, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.POPUP, new Color(100, 100, 100, 255));
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
        if (this.disabled) {
            return true;
        }
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        if (this.startPos != null) {
            boolean passed = true;
            if (this.mouseDown && mouseX >= this.startPos.x && mouseX <= this.startPos.x + this.optionWindowWidth && mouseY >= this.startPos.y && mouseY <= this.startPos.y + this.optionWindowHeight) {
                passed = false;
            }
            return passed;
        }
        if (this.mouseDown) {
            final int x = this.getRenderX();
            final int y = this.getRenderY();
            final int width = this.getStringWidth(this.text) + this.width + this.textOffset;
            final int height = this.height;
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height + 1) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void onPreSort() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = this.getStringWidth(this.text) + this.width + this.textOffset;
        final int height = this.height;
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = this.checked ? ButtonState.ACTIVE : ButtonState.NORMAL;
        if (!this.disabled) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                state = (this.checked ? ButtonState.HOVERACTIVE : ButtonState.HOVER);
                if (this.tooltip.length() > 0) {
                    this.setPriority(MenuPriority.HIGHEST);
                }
                if (this.mouseDown && this.checkable) {
                    this.setChecked(!this.isChecked());
                }
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        if (!this.checkable) {
            state = ButtonState.DISABLED;
        }
        if (state == ButtonState.HOVER || state == ButtonState.HOVERACTIVE) {
            this.setPriority(MenuPriority.HIGH);
        }
        else {
            this.setPriority(MenuPriority.MEDIUM);
        }
        this.lastState = state;
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        DrawImpl.drawRect(x + 2, y + 2, this.width - 3, this.height - 3, backgroundColor);
        this.drawHorizontalLine(x, y, this.width + 1, 1, lineColor);
        this.drawVerticalLine(x, y + 1, this.height - 1, 1, lineColor);
        this.drawHorizontalLine(x, y + this.height, this.width + 1, 1, lineColor);
        this.drawVerticalLine(x + this.width, y + 1, this.height - 1, 1, lineColor);
        this.drawText(this.text, x + this.width + this.textOffset, y + 1, textColor);
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
            this.drawText(this.tooltip, mouseX + this.minOffset, mouseY + this.minOffset, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
    }
    
    public boolean isChecked() {
        return this.checked;
    }
    
    public void setChecked(final boolean checked) {
        this.checked = checked;
        this.onAction();
    }
    
    public boolean getRValue() {
        return this.isChecked();
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
    
    public boolean isCheckable() {
        return this.checkable;
    }
    
    public void setCheckable(final boolean checkable) {
        this.checkable = checkable;
    }
    
    public void onAction() {
    }
    
    public void onBindAction() {
    }
}
