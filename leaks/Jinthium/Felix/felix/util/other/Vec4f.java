package felix.util.other;

public class Vec4f {
    public float x;
    public float y;
    public float w;
    public float h;

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getW() {
        return this.w;
    }

    public float getH() {
        return this.h;
    }

    public void setX(final float x) {
        this.x = x;
    }

    public void setY(final float y) {
        this.y = y;
    }

    public void setW(final float w) {
        this.w = w;
    }

    public void setH(final float h) {
        this.h = h;
    }

    public Vec4f(final float x, final float y, final float w, final float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
}
