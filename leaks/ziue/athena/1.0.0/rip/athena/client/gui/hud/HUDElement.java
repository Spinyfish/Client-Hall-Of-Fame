package rip.athena.client.gui.hud;

import rip.athena.client.modules.*;

public abstract class HUDElement implements IHUD
{
    private Module parent;
    private String identifier;
    private int x;
    private int y;
    private int width;
    private int height;
    private double scale;
    private boolean visible;
    
    public HUDElement(final String identifier, final int width, final int height) {
        this.identifier = identifier;
        this.width = width;
        this.height = height;
        this.scale = 1.0;
        this.visible = true;
    }
    
    public Module getParent() {
        return this.parent;
    }
    
    public void setParent(final Module parent) {
        this.parent = parent;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }
    
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }
    
    public int getX() {
        return this.x;
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    public void setScale(final double scale) {
        this.scale = scale;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
}
