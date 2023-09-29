package alos.stella.ui.draggble;

import net.minecraft.util.MathHelper;

public class DragComp {

    private final String name;
    private int x, y, width, height;

    private boolean drag;
    private int prevX, prevY;

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public DragComp(String name, int x, int y, int width, int height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(int mouseX, int mouseY) {
        if(drag) {
            x = (mouseX - prevX);
            y = (mouseY - prevY);
        }
    }

    public void click(int mouseX, int mouseY) {
        if(MathHelper.isMouseHoveringOnRect(x, y, width, height, mouseX, mouseY)) {
            drag = true;
            prevX = mouseX - x;
            prevY = mouseY - y;
        }
    }

    public void release() {
        drag = false;
    }

    public boolean allowDraw() {
        return true;
    }

}