package me.jinthium.straight.api.clickgui.dropdown.components;

public class Drag {

    private float x, y, initialX, initialY;
    private float startX, startY;
    private boolean dragging;

    public Drag(float initialXVal, float initialYVal) {
        this.initialX = initialXVal;
        this.initialY = initialYVal;
        this.x = initialXVal;
        this.y = initialYVal;
    }

    public final void onDraw(int mouseX, int mouseY) {
        if (dragging) {
            x = (mouseX - startX);
            y = (mouseY - startY);
        }
    }

    public final void onClick(int mouseX, int mouseY, int button, boolean canDrag) {
        if (button == 0 && canDrag) {
            dragging = true;
            startX = (int) (mouseX - x);
            startY = (int) (mouseY - y);
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public final void onRelease(int button) {
        if (button == 0) dragging = false;
    }

}