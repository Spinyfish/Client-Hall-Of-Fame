package frapppyz.cutefurry.pics.modules;

public enum Category {

    COMBAT(40, 50),
    MOVE(150, 50),
    PLAYER(260, 50),
    WORLD(370, 50),
    RENDER(480, 50),
    EXPLOIT(590, 50);

    private int x, y;
    private boolean mouseFocused, open = true;

    Category(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isMouseFocused() {
        return mouseFocused;
    }

    public void setMouseFocused(boolean mouseFocused) {
        this.mouseFocused = mouseFocused;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

}