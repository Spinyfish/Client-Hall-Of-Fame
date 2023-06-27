package rip.athena.client.gui.framework;

import rip.athena.client.gui.framework.draw.*;

public class MenuComponent extends MenuColorType implements DrawImpl, MenuComponentImpl
{
    protected int x;
    protected int y;
    protected int renderOffsetX;
    protected int renderOffsetY;
    protected int width;
    protected int height;
    protected MenuPriority priority;
    protected boolean disabled;
    protected Menu parent;
    
    public MenuComponent(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.renderOffsetX = 0;
        this.renderOffsetY = 0;
        this.width = width;
        this.height = height;
        this.priority = MenuPriority.MEDIUM;
        this.disabled = false;
        this.onInitColors();
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getRenderOffsetX() {
        return this.renderOffsetX;
    }
    
    public void setRenderOffsetX(final int renderOffsetX) {
        this.renderOffsetX = renderOffsetX;
    }
    
    public int getRenderOffsetY() {
        return this.renderOffsetY;
    }
    
    public void setRenderOffsetY(final int renderOffsetY) {
        this.renderOffsetY = renderOffsetY;
    }
    
    public int getRenderX() {
        return this.x + this.renderOffsetX;
    }
    
    public int getRenderY() {
        return this.y + this.renderOffsetY;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
    
    public boolean passesThrough() {
        return true;
    }
    
    public MenuPriority getPriority() {
        return this.priority;
    }
    
    public void setPriority(final MenuPriority priority) {
        this.priority = priority;
    }
    
    public boolean isDisabled() {
        return this.disabled;
    }
    
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }
    
    public void setParent(final Menu parent) {
        this.parent = parent;
    }
    
    public Menu getParent() {
        return this.parent;
    }
}
