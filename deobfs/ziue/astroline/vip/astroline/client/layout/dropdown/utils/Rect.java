/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.layout.dropdown.utils;

public class Rect {
    private float x;
    private float y;
    private float width;
    private float height;

    public Rect() {
    }

    public Rect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Rect)) {
            return false;
        }
        Rect other = (Rect)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.getX(), other.getX()) != 0) {
            return false;
        }
        if (Float.compare(this.getY(), other.getY()) != 0) {
            return false;
        }
        if (Float.compare(this.getWidth(), other.getWidth()) != 0) {
            return false;
        }
        if (Float.compare(this.getHeight(), other.getHeight()) == 0) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Rect;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getX());
        result = result * 59 + Float.floatToIntBits(this.getY());
        result = result * 59 + Float.floatToIntBits(this.getWidth());
        result = result * 59 + Float.floatToIntBits(this.getHeight());
        return result;
    }

    public String toString() {
        return "Rect(x=" + this.getX() + ", y=" + this.getY() + ", width=" + this.getWidth() + ", height=" + this.getHeight() + ")";
    }
}
