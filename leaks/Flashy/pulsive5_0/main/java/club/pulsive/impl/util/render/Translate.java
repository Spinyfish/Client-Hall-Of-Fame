package club.pulsive.impl.util.render;

public final class Translate {

    private double x, y;

    public Translate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void animate(double newX, double newY) {
        x = TransitionUtil.transition(x, newX, 0.4D);
        y = TransitionUtil.transition(y, newY, 0.4D);
    }

    public double getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}