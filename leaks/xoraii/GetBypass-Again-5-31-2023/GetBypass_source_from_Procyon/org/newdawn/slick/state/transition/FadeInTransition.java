// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.state.transition;

import org.newdawn.slick.state.GameState;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.Color;

public class FadeInTransition implements Transition
{
    private Color color;
    private int fadeTime;
    
    public FadeInTransition() {
        this(Color.black, 500);
    }
    
    public FadeInTransition(final Color color) {
        this(color, 500);
    }
    
    public FadeInTransition(final Color color, final int fadeTime) {
        this.fadeTime = 500;
        this.color = new Color(color);
        this.color.a = 1.0f;
        this.fadeTime = fadeTime;
    }
    
    public boolean isComplete() {
        return this.color.a <= 0.0f;
    }
    
    public void postRender(final StateBasedGame game, final GameContainer container, final Graphics g) {
        final Color old = g.getColor();
        g.setColor(this.color);
        g.fillRect(0.0f, 0.0f, (float)container.getWidth(), (float)container.getHeight());
        g.setColor(old);
    }
    
    public void update(final StateBasedGame game, final GameContainer container, final int delta) {
        final Color color = this.color;
        color.a -= delta * (1.0f / this.fadeTime);
        if (this.color.a < 0.0f) {
            this.color.a = 0.0f;
        }
    }
    
    public void preRender(final StateBasedGame game, final GameContainer container, final Graphics g) {
    }
    
    public void init(final GameState firstState, final GameState secondState) {
    }
}
