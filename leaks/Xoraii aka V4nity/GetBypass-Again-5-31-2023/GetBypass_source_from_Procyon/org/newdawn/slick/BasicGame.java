// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick;

public abstract class BasicGame implements Game, InputListener
{
    private static final int MAX_CONTROLLERS = 20;
    private static final int MAX_CONTROLLER_BUTTONS = 100;
    private String title;
    protected boolean[] controllerLeft;
    protected boolean[] controllerRight;
    protected boolean[] controllerUp;
    protected boolean[] controllerDown;
    protected boolean[][] controllerButton;
    
    public BasicGame(final String title) {
        this.controllerLeft = new boolean[20];
        this.controllerRight = new boolean[20];
        this.controllerUp = new boolean[20];
        this.controllerDown = new boolean[20];
        this.controllerButton = new boolean[20][100];
        this.title = title;
    }
    
    public void setInput(final Input input) {
    }
    
    public boolean closeRequested() {
        return true;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public abstract void init(final GameContainer p0) throws SlickException;
    
    public void keyPressed(final int key, final char c) {
    }
    
    public void keyReleased(final int key, final char c) {
    }
    
    public void mouseMoved(final int oldx, final int oldy, final int newx, final int newy) {
    }
    
    public void mouseDragged(final int oldx, final int oldy, final int newx, final int newy) {
    }
    
    public void mouseClicked(final int button, final int x, final int y, final int clickCount) {
    }
    
    public void mousePressed(final int button, final int x, final int y) {
    }
    
    public void controllerButtonPressed(final int controller, final int button) {
        this.controllerButton[controller][button] = true;
    }
    
    public void controllerButtonReleased(final int controller, final int button) {
        this.controllerButton[controller][button] = false;
    }
    
    public void controllerDownPressed(final int controller) {
        this.controllerDown[controller] = true;
    }
    
    public void controllerDownReleased(final int controller) {
        this.controllerDown[controller] = false;
    }
    
    public void controllerLeftPressed(final int controller) {
        this.controllerLeft[controller] = true;
    }
    
    public void controllerLeftReleased(final int controller) {
        this.controllerLeft[controller] = false;
    }
    
    public void controllerRightPressed(final int controller) {
        this.controllerRight[controller] = true;
    }
    
    public void controllerRightReleased(final int controller) {
        this.controllerRight[controller] = false;
    }
    
    public void controllerUpPressed(final int controller) {
        this.controllerUp[controller] = true;
    }
    
    public void controllerUpReleased(final int controller) {
        this.controllerUp[controller] = false;
    }
    
    public void mouseReleased(final int button, final int x, final int y) {
    }
    
    public abstract void update(final GameContainer p0, final int p1) throws SlickException;
    
    public void mouseWheelMoved(final int change) {
    }
    
    public boolean isAcceptingInput() {
        return true;
    }
    
    public void inputEnded() {
    }
    
    public void inputStarted() {
    }
}
