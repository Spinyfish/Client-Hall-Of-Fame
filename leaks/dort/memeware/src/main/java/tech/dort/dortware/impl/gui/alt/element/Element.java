package tech.dort.dortware.impl.gui.alt.element;

public abstract class Element {

    private final int x;
    private final int y;

    public Element(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void draw(int x, int y, int mouseX, int mouseY);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public abstract void click(int mouseX, int mouseY);
}
