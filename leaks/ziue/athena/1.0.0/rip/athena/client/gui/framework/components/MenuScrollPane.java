package rip.athena.client.gui.framework.components;

import java.util.concurrent.*;
import java.awt.*;
import rip.athena.client.gui.framework.*;
import java.util.*;
import rip.athena.client.gui.framework.draw.*;
import org.lwjgl.input.*;

public class MenuScrollPane extends MenuComponent
{
    protected List<MenuComponent> components;
    protected String text;
    protected boolean mouseDown;
    protected boolean dragging;
    protected boolean mouseDragging;
    protected boolean wantsToDrag;
    protected int minOffset;
    protected int theY;
    protected int scrollAmount;
    protected int scrollerWidth;
    protected int scrollerHeight;
    protected int scroll;
    protected ButtonState lastState;
    
    public MenuScrollPane(final String text, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.mouseDown = false;
        this.dragging = false;
        this.mouseDragging = false;
        this.wantsToDrag = false;
        this.minOffset = 5;
        this.theY = 0;
        this.scrollAmount = 75;
        this.scrollerWidth = 10;
        this.scrollerHeight = 5;
        this.scroll = 0;
        this.lastState = ButtonState.NORMAL;
        this.text = text;
        this.components = new CopyOnWriteArrayList<MenuComponent>();
    }
    
    public MenuScrollPane(final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.mouseDown = false;
        this.dragging = false;
        this.mouseDragging = false;
        this.wantsToDrag = false;
        this.minOffset = 5;
        this.theY = 0;
        this.scrollAmount = 75;
        this.scrollerWidth = 10;
        this.scrollerHeight = 5;
        this.scroll = 0;
        this.lastState = ButtonState.NORMAL;
        this.text = "";
        this.components = new CopyOnWriteArrayList<MenuComponent>();
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(65, 65, 65, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(50, 50, 50, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(65, 65, 65, 255));
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
        if (button == 0 && this.getPriority().getPriority() <= MenuPriority.MEDIUM.getPriority()) {
            this.mouseDown = true;
        }
        Collections.sort(this.components, (a, b) -> Integer.compare(a.getPriority().getPriority(), b.getPriority().getPriority()));
        Collections.reverse(this.components);
        for (final MenuComponent component : this.components) {
            if (component.getParent() == null) {
                component.setParent(this.getParent());
            }
            if (((component.getRenderY() >= this.getRenderY() - component.getHeight() && component.getRenderY() <= this.getRenderY() + this.height) || !component.passesThrough()) && ((this.parent.getMouseY() > this.getRenderY() && this.parent.getMouseY() < this.getRenderY() + this.height) || !component.passesThrough())) {
                component.onMouseClick(button);
            }
        }
    }
    
    @Override
    public void onMouseClickMove(final int button) {
        Collections.sort(this.components, (a, b) -> Integer.compare(a.getPriority().getPriority(), b.getPriority().getPriority()));
        Collections.reverse(this.components);
        for (final MenuComponent component : this.components) {
            if (component.getParent() == null) {
                component.setParent(this.getParent());
            }
            if (component.getRenderY() >= this.getRenderY() - component.getHeight() && component.getRenderY() <= this.getRenderY() + this.height && ((this.parent.getMouseY() > this.getRenderY() && this.parent.getMouseY() < this.getRenderY() + this.height) || this.wantsToDrag || !component.passesThrough())) {
                component.onMouseClickMove(button);
            }
        }
        this.mouseDragging = true;
    }
    
    @Override
    public void onKeyDown(final char character, final int key) {
        for (final MenuComponent component : this.components) {
            if (component.getParent() == null) {
                component.setParent(this.getParent());
            }
            if ((component.getRenderY() >= this.getRenderY() && component.getRenderY() + component.getHeight() <= this.getRenderY() + this.height) || !component.passesThrough()) {
                component.onKeyDown(character, key);
            }
        }
    }
    
    @Override
    public boolean onExitGui(final int key) {
        boolean cancel = false;
        for (final MenuComponent component : this.components) {
            if (component.getParent() == null) {
                component.setParent(this.getParent());
            }
            if (((component.getRenderY() >= this.y && component.getRenderY() + component.getHeight() <= this.y + this.height) || !component.passesThrough()) && component.onExitGui(key)) {
                cancel = true;
            }
        }
        return cancel;
    }
    
    @Override
    public void onMouseScroll(final int scroll) {
        this.scroll = scroll;
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
        if ((this.mouseDown || this.mouseDragging) && mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height) {
            return false;
        }
        boolean passesThrough = true;
        for (final MenuComponent component : this.components) {
            if (component.getParent() == null) {
                component.setParent(this.getParent());
            }
            if (!component.passesThrough()) {
                passesThrough = false;
            }
        }
        return !this.wantsToDrag && passesThrough;
    }
    
    @Override
    public void onPreSort() {
        ButtonState state = ButtonState.NORMAL;
        if (this.disabled) {
            state = ButtonState.DISABLED;
        }
        MenuPriority highest = MenuPriority.LOWEST;
        if (this.wantsToDrag) {
            highest = MenuPriority.HIGH;
        }
        for (final MenuComponent component : this.components) {
            if (component.getParent() == null) {
                component.setParent(this.getParent());
            }
            if (component.getPriority().getPriority() > highest.getPriority()) {
                highest = component.getPriority();
            }
        }
        this.setPriority(highest);
        this.lastState = state;
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        final int height = this.height;
        Collections.sort(this.components, (a, b) -> Integer.compare(a.getPriority().getPriority(), b.getPriority().getPriority()));
        int maxY = 0;
        for (final MenuComponent component : this.components) {
            if (component.getParent() == null) {
                component.setParent(this.getParent());
            }
            final int tempY = component.getY() + component.getHeight();
            if (tempY > maxY) {
                maxY = tempY;
            }
        }
        maxY -= height;
        maxY += 3;
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        DrawImpl.drawRect(x + 1, y + 1, this.width - 1, height - 1, backgroundColor);
        final int scrollerX = x + this.width - this.scrollerWidth;
        final int scrollerY = y + 1;
        final int scrollerHeight = height - 1;
        ButtonState scrollerState = ButtonState.HOVER;
        if (((mouseX >= scrollerX && mouseX <= scrollerX + this.scrollerWidth) || (this.wantsToDrag && this.dragging)) && ((mouseY >= scrollerY && mouseY <= scrollerY + scrollerHeight) || (this.wantsToDrag && this.dragging))) {
            scrollerState = ButtonState.ACTIVE;
            if (!this.wantsToDrag) {
                this.wantsToDrag = this.mouseDown;
            }
        }
        int desiredChange = this.theY;
        final float scrollerSizeDelta = height / (float)(maxY + height);
        if (scrollerSizeDelta <= 1.0f && mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + height) {
            if (this.scroll > 0) {
                if (desiredChange + this.scrollAmount * 5 <= 0) {
                    desiredChange += this.scrollAmount * 5;
                }
                else {
                    desiredChange = 0;
                }
            }
            else if (this.scroll < 0) {
                if (desiredChange - this.scrollAmount * 5 >= -maxY) {
                    desiredChange -= this.scrollAmount * 5;
                }
                else {
                    desiredChange = -maxY;
                }
            }
        }
        int newSize = Math.round(scrollerSizeDelta * scrollerHeight);
        if (scrollerSizeDelta > 1.0f) {
            newSize = 0;
        }
        for (final MenuComponent component2 : this.components) {
            if (component2.getParent() == null) {
                component2.setParent(this.getParent());
            }
            component2.setRenderOffsetX(x);
            component2.setRenderOffsetY(y + this.theY);
            if (component2 instanceof MenuColorPicker) {
                component2.setRenderOffsetX(-component2.getX() + x + this.width - component2.getWidth() - 10);
            }
            if (component2.getWidth() > this.width) {
                component2.setWidth(this.width - 1);
            }
            if (component2.getWidth() > this.width - this.scrollerWidth && scrollerSizeDelta < 1.0f) {
                component2.setWidth(this.width - this.scrollerWidth - 1);
            }
        }
        Collections.sort(this.components, (a, b) -> Integer.compare(a.getPriority().getPriority(), b.getPriority().getPriority()));
        Collections.reverse(this.components);
        int passThroughIndex = -1;
        int index = this.components.size();
        for (final MenuComponent component3 : this.components) {
            if (!component3.passesThrough() && passThroughIndex == -1) {
                passThroughIndex = index;
            }
            --index;
        }
        Collections.reverse(this.components);
        final int oldIndex = index = index;
        for (final MenuComponent component4 : this.components) {
            final boolean inViewport = component4.getRenderY() >= y && component4.getRenderY() <= y + height;
            final boolean override = !component4.passesThrough();
            if (!inViewport && !override) {
                component4.onPreSort();
            }
            if (inViewport || override) {
                if (index >= passThroughIndex - 1) {
                    this.parent.setMouseX(mouseX);
                    this.parent.setMouseY(mouseY);
                    if (this.wantsToDrag || ((this.parent.getMouseY() <= this.getRenderY() || this.parent.getMouseY() >= this.getRenderY() + this.height) && !this.wantsToDrag && (component4.getPriority() != this.getPriority() || this.getPriority().getPriority() <= MenuPriority.HIGH.getPriority()))) {
                        this.parent.setMouseX(Integer.MAX_VALUE);
                        this.parent.setMouseY(Integer.MAX_VALUE);
                    }
                }
                else {
                    if (component4 instanceof MenuDraggable) {
                        ++index;
                        continue;
                    }
                    this.parent.setMouseX(Integer.MAX_VALUE);
                    this.parent.setMouseY(Integer.MAX_VALUE);
                }
                component4.onPreSort();
                component4.onRender();
            }
            ++index;
        }
        if (passThroughIndex == -1) {
            this.theY = desiredChange;
            if (this.wantsToDrag && (this.mouseDown || this.dragging) && scrollerSizeDelta < 1.0f) {
                float scrollerDelta = (mouseY - (y + this.minOffset * 2)) / (float)(height - this.minOffset * 4);
                if (scrollerDelta > 1.0f) {
                    scrollerDelta = 1.0f;
                }
                else if (scrollerDelta < 0.0f) {
                    scrollerDelta = 0.0f;
                }
                this.theY = Math.round(-scrollerDelta * maxY);
            }
        }
        else if (scrollerState == ButtonState.ACTIVE) {
            scrollerState = ButtonState.HOVER;
        }
        DrawImpl.drawRect(x, y, this.width, this.minOffset * 2, backgroundColor);
        if (this.text.length() > 0) {
            final int minus = this.getStringWidth(this.text) + this.minOffset * 3;
            this.drawText(this.text, x + 5, y + this.getStringHeight(this.text) / 2, textColor);
            this.drawHorizontalLine(x + minus, y, this.width - minus + 1, 1, lineColor);
            this.drawHorizontalLine(x, y, this.minOffset, 1, lineColor);
        }
        else {
            this.drawHorizontalLine(x, y, this.width + 1, 1, lineColor);
        }
        this.drawVerticalLine(x, y + 1, height - 1, 1, lineColor);
        this.drawHorizontalLine(x, y + height, this.width + 1, 1, lineColor);
        this.drawVerticalLine(x + this.width, y + 1, height - 1, 1, lineColor);
        float scrollerDelta = -this.theY / (float)(scrollerHeight + maxY);
        final int newY = scrollerY + Math.round(scrollerHeight * scrollerDelta);
        if (newSize > 4) {
            DrawImpl.drawRect(scrollerX, newY + 1, this.scrollerWidth, newSize - 4, this.getColor(DrawType.BACKGROUND, scrollerState));
        }
        this.mouseDown = false;
        if (this.wantsToDrag) {
            this.dragging = Mouse.isButtonDown(0);
            this.wantsToDrag = this.dragging;
        }
    }
    
    public void drawExtras() {
        for (final MenuComponent component : this.components) {
            if (component instanceof MenuColorPicker) {
                final MenuColorPicker picker = (MenuColorPicker)component;
                picker.drawPicker();
            }
            else if (component instanceof MenuLabel) {
                final MenuLabel label = (MenuLabel)component;
                label.drawTooltip();
            }
            else if (component instanceof MenuCheckbox) {
                final MenuCheckbox checkbox = (MenuCheckbox)component;
                checkbox.drawTooltip();
            }
            else if (component instanceof MenuComboBox) {
                final MenuComboBox box = (MenuComboBox)component;
                box.drawDropdown();
            }
            else {
                if (!(component instanceof MenuDropdown)) {
                    continue;
                }
                final MenuDropdown dropdown = (MenuDropdown)component;
                dropdown.drawDropdown();
            }
        }
    }
    
    public void addComponent(final MenuComponent component) {
        this.theY = 0;
        this.components.add(component);
    }
    
    public List<MenuComponent> getComponents() {
        return this.components;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public void onAction() {
    }
}
