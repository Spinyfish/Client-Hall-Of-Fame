package rip.athena.client.gui.framework.components;

import rip.athena.client.gui.framework.*;
import org.lwjgl.input.*;

public class MenuDraggable extends MenuComponent
{
    protected boolean mouseDown;
    protected boolean dragging;
    protected boolean wantToDrag;
    protected int xSaved;
    protected int ySaved;
    protected int lastX;
    protected int lastY;
    
    public MenuDraggable(final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.mouseDown = false;
        this.dragging = false;
        this.wantToDrag = false;
        this.xSaved = -1;
        this.ySaved = -1;
        this.lastX = -1;
        this.lastY = -1;
        this.setPriority(MenuPriority.LOW);
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
            this.dragging = true;
        }
    }
    
    @Override
    public void onPreSort() {
        if (this.dragging) {
            this.setPriority(MenuPriority.HIGHEST);
        }
        else {
            this.setPriority(MenuPriority.LOWEST);
        }
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        if (!this.disabled && mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height && this.mouseDown && !this.wantToDrag) {
            this.wantToDrag = true;
            this.xSaved = this.parent.getX() - mouseX;
            this.ySaved = this.parent.getY() - mouseY;
        }
        if ((this.xSaved != x || this.ySaved != y) && this.xSaved != -1 && this.ySaved != -1 && (this.lastX != mouseX || this.lastY != mouseY)) {
            this.lastX = mouseX;
            this.lastY = mouseY;
            this.onAction();
            this.getParent().setLocation(mouseX + this.xSaved, mouseY + this.ySaved);
        }
        if (this.wantToDrag) {
            this.dragging = Mouse.isButtonDown(0);
            if (!(this.wantToDrag = this.dragging)) {
                this.xSaved = -1;
                this.ySaved = -1;
            }
        }
        this.mouseDown = false;
    }
    
    public void onAction() {
    }
}
