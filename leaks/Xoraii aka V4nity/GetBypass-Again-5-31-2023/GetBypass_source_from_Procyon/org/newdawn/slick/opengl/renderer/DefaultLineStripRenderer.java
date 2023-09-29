// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.opengl.renderer;

public class DefaultLineStripRenderer implements LineStripRenderer
{
    private SGL GL;
    
    public DefaultLineStripRenderer() {
        this.GL = Renderer.get();
    }
    
    public void end() {
        this.GL.glEnd();
    }
    
    public void setAntiAlias(final boolean antialias) {
        if (antialias) {
            this.GL.glEnable(2848);
        }
        else {
            this.GL.glDisable(2848);
        }
    }
    
    public void setWidth(final float width) {
        this.GL.glLineWidth(width);
    }
    
    public void start() {
        this.GL.glBegin(3);
    }
    
    public void vertex(final float x, final float y) {
        this.GL.glVertex2f(x, y);
    }
    
    public void color(final float r, final float g, final float b, final float a) {
        this.GL.glColor4f(r, g, b, a);
    }
    
    public void setLineCaps(final boolean caps) {
    }
    
    public boolean applyGLLineFixes() {
        return true;
    }
}
