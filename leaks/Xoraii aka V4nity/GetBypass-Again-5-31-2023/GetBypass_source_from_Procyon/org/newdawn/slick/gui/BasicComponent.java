// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.gui;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Graphics;

public abstract class BasicComponent extends AbstractComponent
{
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    
    public BasicComponent(final GUIContext container) {
        super(container);
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public abstract void renderImpl(final GUIContext p0, final Graphics p1);
    
    public void render(final GUIContext container, final Graphics g) throws SlickException {
        this.renderImpl(container, g);
    }
    
    public void setLocation(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
}
