package rip.athena.client.gui.menu.altmanager;

import rip.athena.client.gui.screen.*;
import rip.athena.client.utils.render.*;

public abstract class Panel implements Screen
{
    private float x;
    private float y;
    private float width;
    private float height;
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY) {
        RoundedUtils.drawRound(this.x, this.y, this.width, this.height, 5.0f, ColorUtil.tripleColor(27));
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public void setX(final float x) {
        this.x = x;
    }
    
    public void setY(final float y) {
        this.y = y;
    }
    
    public void setWidth(final float width) {
        this.width = width;
    }
    
    public void setHeight(final float height) {
        this.height = height;
    }
}
