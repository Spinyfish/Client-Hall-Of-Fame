package rip.athena.client.gui.framework.components;

import rip.athena.client.gui.framework.*;

public class MenuNoDrag extends MenuComponent
{
    protected boolean mouseDown;
    
    public MenuNoDrag(final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.mouseDown = false;
        this.setPriority(MenuPriority.LOW);
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
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        return !this.mouseDown || mouseX < x || mouseX > x + this.width || mouseY < y || mouseY > y + this.height;
    }
    
    @Override
    public void onRender() {
        this.mouseDown = false;
    }
    
    public void onAction() {
    }
}
