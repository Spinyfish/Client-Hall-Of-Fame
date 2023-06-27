package rip.athena.client.gui.framework.components;

import rip.athena.client.utils.animations.simple.*;
import java.text.*;
import java.math.*;
import java.awt.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;
import org.lwjgl.input.*;

public class MenuSlider extends MenuComponent
{
    protected boolean isFloat;
    protected SimpleAnimation valueAnim;
    protected float value;
    protected float minValue;
    protected float maxValue;
    protected int minOffset;
    protected DecimalFormat format;
    protected boolean wantToDrag;
    protected boolean mouseDragging;
    protected boolean mouseDown;
    protected ButtonState lastState;
    
    public MenuSlider(final int startValue, final int minValue, final int maxValue, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.isFloat = false;
        this.minOffset = 2;
        this.wantToDrag = false;
        this.mouseDragging = false;
        this.mouseDown = false;
        this.lastState = ButtonState.NORMAL;
        this.init((float)startValue, (float)minValue, (float)maxValue, 1);
    }
    
    public MenuSlider(final float startValue, final float minValue, final float maxValue, final int precision, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.isFloat = false;
        this.minOffset = 2;
        this.wantToDrag = false;
        this.mouseDragging = false;
        this.mouseDown = false;
        this.lastState = ButtonState.NORMAL;
        this.isFloat = true;
        this.init(startValue, minValue, maxValue, precision);
    }
    
    private void initPrecision(final int precision) {
        (this.format = new DecimalFormat()).setMaximumFractionDigits(precision);
        this.format.setRoundingMode(RoundingMode.HALF_DOWN);
    }
    
    private void init(final float startValue, final float minValue, final float maxValue, final int precision) {
        this.value = startValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        if (minValue > this.value) {
            this.value = minValue;
        }
        if (this.value > maxValue) {
            this.value = maxValue;
        }
        if (this.minValue > this.maxValue) {
            this.maxValue = this.minValue;
        }
        this.initPrecision((precision < 1) ? 1 : precision);
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(65, 65, 65, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(50, 50, 50, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(65, 65, 65, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.POPUP, new Color(50, 85, 139, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(20, 20, 20, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(20, 20, 20, 255));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(83, 124, 189, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(200, 200, 200, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(225, 225, 225, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.POPUP, new Color(81, 108, 255, 255));
    }
    
    @Override
    public void onMouseClick(final int button) {
        if (button == 0) {
            this.mouseDown = true;
        }
    }
    
    @Override
    public void onMouseClickMove(final int button) {
        if (button == 0) {
            this.mouseDragging = true;
        }
    }
    
    @Override
    public boolean passesThrough() {
        if (this.disabled || this.parent == null) {
            return true;
        }
        if (this.mouseDown) {
            final int x = this.getRenderX();
            final int y = this.getRenderY();
            final int mouseX = this.parent.getMouseX();
            final int mouseY = this.parent.getMouseY();
            if (mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height) {
                return false;
            }
        }
        return !this.wantToDrag;
    }
    
    @Override
    public void onPreSort() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = this.width;
        final int height = this.height;
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = ButtonState.NORMAL;
        if (!this.disabled) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                state = ButtonState.HOVER;
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        if (this.wantToDrag) {
            this.setPriority(MenuPriority.HIGHEST);
        }
        else if (state == ButtonState.HOVER || state == ButtonState.HOVERACTIVE) {
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
        final int width = this.width;
        final int height = this.height;
        final int mouseX = this.parent.getMouseX();
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        DrawImpl.drawRect(x + 1, y + 1, width - 1, height - 1, backgroundColor);
        this.drawHorizontalLine(x, y, width + 1, 1, lineColor);
        this.drawVerticalLine(x, y + 1, height - 1, 1, lineColor);
        this.drawHorizontalLine(x, y + height, width + 1, 1, lineColor);
        this.drawVerticalLine(x + width, y + 1, height - 1, 1, lineColor);
        String data = "";
        if (this.isFloat) {
            data = this.getValue() + "/" + this.getMaxValue();
        }
        else {
            data = this.getIntValue() + "/" + Math.round(this.getMaxValue());
        }
        final float diff = this.maxValue - this.minValue;
        int linePos = x + Math.round(width * (this.value - this.minValue) / diff);
        if (linePos + 1 >= x + width) {
            linePos -= this.minOffset;
        }
        else if (linePos - 1 <= x) {
            linePos += this.minOffset;
        }
        this.drawText(data, x + width / 2 - this.getStringWidth(data) / 2, y + height / 2 - this.getStringHeight(data) / 2, textColor);
        this.drawVerticalLine(linePos - 1, y + 1, height - 1, 1, this.getColor(DrawType.LINE, ButtonState.ACTIVE));
        this.drawVerticalLine(linePos, y + 1, height - 1, 1, this.getColor(DrawType.TEXT, ButtonState.POPUP));
        this.drawVerticalLine(linePos + 1, y + 1, height - 1, 1, this.getColor(DrawType.LINE, ButtonState.ACTIVE));
        if (this.wantToDrag || (this.mouseDown && this.lastState == ButtonState.HOVER)) {
            if (this.mouseDown) {
                this.wantToDrag = true;
            }
            float wantedValue = this.minValue + (mouseX - this.minOffset - x) * diff / (width - this.minOffset * 2);
            if (wantedValue > this.maxValue) {
                wantedValue = this.maxValue;
            }
            else if (this.minValue > wantedValue) {
                wantedValue = this.minValue;
            }
            final float oldValue = this.value;
            this.value = wantedValue;
            if (oldValue != this.value) {
                this.onAction();
            }
        }
        if (this.wantToDrag) {
            this.mouseDragging = Mouse.isButtonDown(0);
            this.wantToDrag = this.mouseDragging;
        }
        this.mouseDragging = false;
        this.mouseDown = false;
    }
    
    public int getIntValue() {
        return Math.round(this.value);
    }
    
    public float getValue() {
        try {
            return Float.valueOf(this.format.format(this.value));
        }
        catch (NumberFormatException e) {
            return Math.round(this.value * 10.0f) / 10.0f;
        }
    }
    
    public void setValue(final float value) {
        this.value = value;
        this.onAction();
    }
    
    public float getMinValue() {
        return this.minValue;
    }
    
    public void setMinValue(final float minValue) {
        this.minValue = minValue;
    }
    
    public float getMaxValue() {
        return this.maxValue;
    }
    
    public void setMaxValue(final float maxValue) {
        this.maxValue = maxValue;
    }
    
    public void onAction() {
    }
}
