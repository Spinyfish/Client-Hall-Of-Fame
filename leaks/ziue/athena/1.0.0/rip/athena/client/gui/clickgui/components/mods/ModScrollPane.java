package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.components.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.*;
import rip.athena.client.utils.render.*;
import org.lwjgl.input.*;
import java.util.*;

public class ModScrollPane extends MenuScrollPane
{
    private boolean fullHeightScroller;
    
    public ModScrollPane(final int x, final int y, final int width, final int height, final boolean fullHeightScroller) {
        super(x, y, width, height);
        this.fullHeightScroller = fullHeightScroller;
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.BACKGROUND, ButtonState.POPUP, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(25, 24, 29, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(30, 30, 30, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(25, 24, 29, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(25, 24, 29, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(23, 23, 25, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(35, 35, 38, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(35, 35, 38, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(35, 35, 38, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
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
        final int scrollerX = x + this.width - this.scrollerWidth;
        final int scrollerY = y + 1;
        int scrollerHeight = height - 1;
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
                if (desiredChange + this.scrollAmount <= 0) {
                    desiredChange += this.scrollAmount;
                }
                else {
                    desiredChange = 0;
                }
            }
            else if (this.scroll < 0) {
                if (desiredChange - this.scrollAmount >= -maxY) {
                    desiredChange -= this.scrollAmount;
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
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        GL11.glScissor(this.getRenderX() - 5, Minecraft.getMinecraft().displayHeight - (this.getRenderY() + this.getHeight()), this.getWidth() + 5, this.getHeight() - 1);
        for (final MenuComponent component4 : this.components) {
            final boolean inViewport = component4.getRenderY() >= y - component4.getHeight() && component4.getRenderY() <= y + component4.getHeight() + height;
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
        GL11.glDisable(3089);
        GL11.glPopMatrix();
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
        float scrollerDelta = -this.theY / (float)(scrollerHeight + maxY);
        final int newY = scrollerY + Math.round(scrollerHeight * scrollerDelta);
        if (newSize > 4 && scrollerSizeDelta < 1.0f) {
            scrollerHeight -= 3;
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            RoundedUtils.drawRoundedRect((float)(scrollerX + 2), (float)(y + 2), (float)(scrollerX + this.scrollerWidth - 2), (float)(y + scrollerHeight), 6.0f, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
            RoundedUtils.drawGradientRound((float)scrollerX, (float)(newY - 3), (float)this.scrollerWidth, (float)newSize, 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor());
        }
        this.mouseDown = false;
        if (this.wantsToDrag) {
            this.dragging = Mouse.isButtonDown(0);
            this.wantsToDrag = this.dragging;
        }
    }
    
    public void setFullHeightScroller(final boolean fullHeightScroller) {
        this.fullHeightScroller = fullHeightScroller;
    }
}
