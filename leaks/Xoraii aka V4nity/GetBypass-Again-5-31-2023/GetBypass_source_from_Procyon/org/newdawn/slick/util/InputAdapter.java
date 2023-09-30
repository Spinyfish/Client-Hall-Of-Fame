// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.util;

import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;

public class InputAdapter implements InputListener
{
    private boolean acceptingInput;
    
    public InputAdapter() {
        this.acceptingInput = true;
    }
    
    public void controllerButtonPressed(final int controller, final int button) {
    }
    
    public void controllerButtonReleased(final int controller, final int button) {
    }
    
    public void controllerDownPressed(final int controller) {
    }
    
    public void controllerDownReleased(final int controller) {
    }
    
    public void controllerLeftPressed(final int controller) {
    }
    
    public void controllerLeftReleased(final int controller) {
    }
    
    public void controllerRightPressed(final int controller) {
    }
    
    public void controllerRightReleased(final int controller) {
    }
    
    public void controllerUpPressed(final int controller) {
    }
    
    public void controllerUpReleased(final int controller) {
    }
    
    public void inputEnded() {
    }
    
    public boolean isAcceptingInput() {
        return this.acceptingInput;
    }
    
    public void setAcceptingInput(final boolean acceptingInput) {
        this.acceptingInput = acceptingInput;
    }
    
    public void keyPressed(final int key, final char c) {
    }
    
    public void keyReleased(final int key, final char c) {
    }
    
    public void mouseMoved(final int oldx, final int oldy, final int newx, final int newy) {
    }
    
    public void mousePressed(final int button, final int x, final int y) {
    }
    
    public void mouseReleased(final int button, final int x, final int y) {
    }
    
    public void mouseWheelMoved(final int change) {
    }
    
    public void setInput(final Input input) {
    }
    
    public void mouseClicked(final int button, final int x, final int y, final int clickCount) {
    }
    
    public void mouseDragged(final int oldx, final int oldy, final int newx, final int newy) {
    }
    
    public void inputStarted() {
    }
}
