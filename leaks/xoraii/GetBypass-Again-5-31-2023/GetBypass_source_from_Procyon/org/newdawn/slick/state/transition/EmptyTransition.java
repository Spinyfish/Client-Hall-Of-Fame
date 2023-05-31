// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.state.transition;

import org.newdawn.slick.state.GameState;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

public class EmptyTransition implements Transition
{
    public boolean isComplete() {
        return true;
    }
    
    public void postRender(final StateBasedGame game, final GameContainer container, final Graphics g) throws SlickException {
    }
    
    public void preRender(final StateBasedGame game, final GameContainer container, final Graphics g) throws SlickException {
    }
    
    public void update(final StateBasedGame game, final GameContainer container, final int delta) throws SlickException {
    }
    
    public void init(final GameState firstState, final GameState secondState) {
    }
}
