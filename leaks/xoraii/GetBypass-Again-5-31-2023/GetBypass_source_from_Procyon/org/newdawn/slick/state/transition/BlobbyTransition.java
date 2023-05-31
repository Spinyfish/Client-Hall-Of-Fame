// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.state.transition;

import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.MaskUtil;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;
import java.util.ArrayList;
import org.newdawn.slick.Color;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.opengl.renderer.SGL;

public class BlobbyTransition implements Transition
{
    protected static SGL GL;
    private GameState prev;
    private boolean finish;
    private Color background;
    private ArrayList blobs;
    private int timer;
    private int blobCount;
    
    public BlobbyTransition() {
        this.blobs = new ArrayList();
        this.timer = 1000;
        this.blobCount = 10;
    }
    
    public BlobbyTransition(final Color background) {
        this.blobs = new ArrayList();
        this.timer = 1000;
        this.blobCount = 10;
        this.background = background;
    }
    
    public void init(final GameState firstState, final GameState secondState) {
        this.prev = secondState;
    }
    
    public boolean isComplete() {
        return this.finish;
    }
    
    public void postRender(final StateBasedGame game, final GameContainer container, final Graphics g) throws SlickException {
        MaskUtil.resetMask();
    }
    
    public void preRender(final StateBasedGame game, final GameContainer container, final Graphics g) throws SlickException {
        this.prev.render(container, game, g);
        MaskUtil.defineMask();
        for (int i = 0; i < this.blobs.size(); ++i) {
            this.blobs.get(i).render(g);
        }
        MaskUtil.finishDefineMask();
        MaskUtil.drawOnMask();
        if (this.background != null) {
            final Color c = g.getColor();
            g.setColor(this.background);
            g.fillRect(0.0f, 0.0f, (float)container.getWidth(), (float)container.getHeight());
            g.setColor(c);
        }
    }
    
    public void update(final StateBasedGame game, final GameContainer container, final int delta) throws SlickException {
        if (this.blobs.size() == 0) {
            for (int i = 0; i < this.blobCount; ++i) {
                this.blobs.add(new Blob(container));
            }
        }
        for (int i = 0; i < this.blobs.size(); ++i) {
            this.blobs.get(i).update(delta);
        }
        this.timer -= delta;
        if (this.timer < 0) {
            this.finish = true;
        }
    }
    
    static {
        BlobbyTransition.GL = Renderer.get();
    }
    
    private class Blob
    {
        private float x;
        private float y;
        private float growSpeed;
        private float rad;
        
        public Blob(final GameContainer container) {
            this.x = (float)(Math.random() * container.getWidth());
            this.y = (float)(Math.random() * container.getWidth());
            this.growSpeed = (float)(1.0 + Math.random() * 1.0);
        }
        
        public void update(final int delta) {
            this.rad += this.growSpeed * delta * 0.4f;
        }
        
        public void render(final Graphics g) {
            g.fillOval(this.x - this.rad, this.y - this.rad, this.rad * 2.0f, this.rad * 2.0f);
        }
    }
}
