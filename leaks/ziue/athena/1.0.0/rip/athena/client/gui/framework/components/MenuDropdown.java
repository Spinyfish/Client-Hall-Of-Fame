package rip.athena.client.gui.framework.components;

import java.util.*;
import java.awt.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;

public class MenuDropdown extends MenuComponent
{
    protected String[] values;
    protected boolean open;
    protected boolean mouseDown;
    protected int index;
    protected int textOffset;
    protected int totalHeight;
    protected int maxHeight;
    protected int maxWidth;
    protected int arrowOffset;
    protected ButtonState lastState;
    
    public MenuDropdown(final String[] values, final int x, final int y) {
        super(x, y, 0, 0);
        this.open = false;
        this.mouseDown = false;
        this.index = 0;
        this.textOffset = 25;
        this.totalHeight = 0;
        this.maxHeight = 0;
        this.maxWidth = 0;
        this.arrowOffset = 15;
        this.lastState = ButtonState.NORMAL;
        this.values = values;
        if (values.length == 0) {
            this.disabled = true;
        }
        if (!this.disabled) {
            for (final String value : values) {
                final int tWidth = this.getStringWidth(value);
                final int tHeight = this.getStringHeight(value);
                this.totalHeight += tHeight;
                if (this.maxHeight < tHeight) {
                    this.maxHeight = tHeight;
                }
                if (this.maxWidth < tWidth) {
                    this.maxWidth = tWidth;
                }
            }
            this.setValue(values[0]);
        }
        this.width = this.maxWidth;
        this.height = this.maxHeight + 1;
    }
    
    public MenuDropdown(final Class<?> theEnum, final int x, final int y) {
        super(x, y, 0, 0);
        this.open = false;
        this.mouseDown = false;
        this.index = 0;
        this.textOffset = 25;
        this.totalHeight = 0;
        this.maxHeight = 0;
        this.maxWidth = 0;
        this.arrowOffset = 15;
        this.lastState = ButtonState.NORMAL;
        if (theEnum.isEnum()) {
            final List<String> tempVals = new ArrayList<String>();
            for (final Object object : theEnum.getEnumConstants()) {
                final String string = String.valueOf(object);
                tempVals.add((string.toUpperCase().substring(0, 1) + string.toLowerCase().substring(1, string.length())).replaceAll("_", " "));
            }
            this.values = tempVals.toArray(new String[tempVals.size()]);
        }
        if (this.values.length == 0) {
            this.disabled = true;
        }
        if (!this.disabled) {
            for (final String value : this.values) {
                final int tWidth = this.getStringWidth(value);
                final int tHeight = this.getStringHeight(value);
                this.totalHeight += tHeight;
                if (this.maxHeight < tHeight) {
                    this.maxHeight = tHeight;
                }
                if (this.maxWidth < tWidth) {
                    this.maxWidth = tWidth;
                }
            }
            this.setValue(this.values[0]);
        }
        this.width = this.maxWidth;
        this.height = this.maxHeight + 1;
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(65, 65, 65, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(50, 50, 50, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(65, 65, 65, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(20, 20, 20, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(20, 20, 20, 255));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(200, 200, 200, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(100, 120, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(225, 225, 225, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(110, 130, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
    }
    
    @Override
    public boolean onExitGui(final int key) {
        if (this.open) {
            this.open = false;
        }
        return false;
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
        final int width = this.width + this.textOffset;
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = this.open ? ButtonState.ACTIVE : ButtonState.NORMAL;
        if (!this.disabled) {
            boolean inRange = false;
            if (mouseX >= x && mouseX <= x + width + this.arrowOffset - 1) {
                if (mouseY >= y && mouseY <= y + this.height + 1) {
                    state = (this.open ? ButtonState.HOVERACTIVE : ButtonState.HOVER);
                    if (this.mouseDown) {
                        this.open = true;
                    }
                }
                if (mouseY >= y && mouseY <= y + this.totalHeight + this.height + this.values.length) {
                    inRange = true;
                }
            }
            if (this.open && !inRange && this.mouseDown) {
                this.open = false;
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        if (this.open) {
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
    public boolean passesThrough() {
        if (this.disabled) {
            return true;
        }
        if (this.mouseDown) {
            final int x = this.getRenderX();
            final int y = this.getRenderY();
            final int width = this.width + this.textOffset;
            final int height = this.height;
            final int mouseX = this.parent.getMouseX();
            final int mouseY = this.parent.getMouseY();
            if (mouseX >= x && mouseX <= x + width + this.arrowOffset - 1 && mouseY >= y && mouseY <= y + height) {
                return false;
            }
        }
        return !this.open;
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = this.width + this.textOffset;
        final int height = this.height;
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, ButtonState.NORMAL);
        DrawImpl.drawRect(x + 1, y + 1, width - 2 + this.arrowOffset, height - 1, backgroundColor);
        this.drawHorizontalLine(x, y, width + 1, 1, lineColor);
        this.drawVerticalLine(x, y + 1, height - 1, 1, lineColor);
        this.drawHorizontalLine(x, y + height, width + 1, 1, lineColor);
        this.drawVerticalLine(x + width, y + 1, height - 1, 1, lineColor);
        this.drawHorizontalLine(x + width, y, this.arrowOffset, 1, lineColor);
        this.drawHorizontalLine(x + width, y + height, this.arrowOffset, 1, lineColor);
        this.drawVerticalLine(x + width + this.arrowOffset - 1, y + 1, height - 1, 1, lineColor);
        final int arrowX = x + width - 1 + this.arrowOffset / 2;
        final int arrowY = y + (height - 1) / 2;
        if (this.open) {
            this.drawHorizontalLine(arrowX - 1, arrowY + 1, 5, 1, textColor);
            this.drawHorizontalLine(arrowX - 2, arrowY + 2, 7, 1, textColor);
            this.drawHorizontalLine(arrowX - 3, arrowY + 3, 9, 1, textColor);
            this.drawHorizontalLine(arrowX, arrowY, 3, 1, textColor);
            this.drawPixel(arrowX + 1, arrowY - 1, textColor);
        }
        else {
            this.drawHorizontalLine(arrowX - 3, arrowY - 1, 9, 1, textColor);
            this.drawHorizontalLine(arrowX - 2, arrowY, 7, 1, textColor);
            this.drawHorizontalLine(arrowX - 1, arrowY + 1, 5, 1, textColor);
            this.drawHorizontalLine(arrowX, arrowY + 2, 3, 1, textColor);
            this.drawPixel(arrowX + 1, arrowY + 3, textColor);
        }
        this.drawText(this.values[this.index], x + 1 + width / 2 - this.getStringWidth(this.values[this.index]) / 2, y + 1, textColor);
        this.drawDropdown();
        this.mouseDown = false;
    }
    
    public void drawDropdown() {
        if (this.open) {
            final int x = this.getRenderX();
            int y = this.getRenderY();
            final int mouseX = this.parent.getMouseX();
            final int mouseY = this.parent.getMouseY();
            final int backgroundColor = this.getColor(DrawType.BACKGROUND, ButtonState.POPUP);
            final int lineColor = this.getColor(DrawType.LINE, ButtonState.POPUP);
            final int width = this.width + this.textOffset + this.arrowOffset - 1;
            final int height = this.totalHeight + this.values.length;
            DrawImpl.drawRect(x, y + this.height + 1, width, height, backgroundColor);
            this.drawHorizontalLine(x, y + this.height + 1, width + 1, 1, lineColor);
            this.drawVerticalLine(x, y + this.height + 2, height - 1, 1, lineColor);
            this.drawHorizontalLine(x, y + this.height + 1 + height, width + 1, 1, lineColor);
            this.drawVerticalLine(x + width, y + this.height + 2, height - 1, 1, lineColor);
            y += 2;
            for (int i = 0; i < this.values.length; ++i) {
                final String value = this.values[i];
                final int sHeight = this.getStringHeight(value);
                y += sHeight + 1;
                boolean hover = false;
                if (mouseX >= x + 1 && mouseX <= x + width - 1 && mouseY >= y && mouseY <= y + sHeight) {
                    hover = true;
                    DrawImpl.drawRect(x + 1, y, width - 1, sHeight, this.getColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE));
                    if (this.mouseDown) {
                        this.index = i;
                        this.open = false;
                        this.onAction();
                        break;
                    }
                }
                this.drawText(value, x + 1 + width / 2 - this.getStringWidth(value) / 2, y, this.getColor(DrawType.TEXT, (this.index == i) ? (hover ? ButtonState.HOVERACTIVE : ButtonState.ACTIVE) : (hover ? ButtonState.HOVER : ButtonState.NORMAL)));
            }
        }
    }
    
    public String getValue() {
        return this.values[this.index];
    }
    
    public void setValue(final String value) {
        if (value == null) {
            return;
        }
        for (int i = 0; i < this.values.length; ++i) {
            if (this.values[i].equalsIgnoreCase(value.replace('_', ' '))) {
                this.index = i;
                break;
            }
        }
    }
    
    public void setIndex(final int index) {
        this.index = index;
    }
    
    public void onAction() {
    }
}
