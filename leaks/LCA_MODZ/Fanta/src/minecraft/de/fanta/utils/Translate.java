package de.fanta.utils;

public class Translate
{
    private float x;
    private float y;
    private long lastMS;

    public Translate(final float x, final float y) {
        this.x = x;
        this.y = y;
        this.lastMS = System.currentTimeMillis();
    }

    public void interpolate(final float targetX, final float targetY, final int xSpeed, final int ySpeed) {
        final long currentMS = System.currentTimeMillis();
        final long delta = currentMS - this.lastMS;
        this.lastMS = currentMS;
        final int deltaX = (int)(Math.abs(targetX - this.x) * 0.51f);
        final int deltaY = (int)(Math.abs(targetY - this.y) * 0.51f);
        this.x = AnimationUtil.calculateCompensation(targetX, this.x, delta, deltaX);
        this.y = AnimationUtil.calculateCompensation(targetY, this.y, delta, deltaY);
    }

    public void interpolate(final float targetX, final float targetY, final double speed) {
        final long currentMS = System.currentTimeMillis();
        final long delta = currentMS - this.lastMS;
        this.lastMS = currentMS;
        double deltaX = 0.0;
        double deltaY = 0.0;
        if (speed != 0.0) {
            deltaX = Math.abs(targetX - this.x) * 0.35f / (10.0 / speed);
            deltaY = Math.abs(targetY - this.y) * 0.35f / (10.0 / speed);
        }
        this.x = AnimationUtil.calculateCompensation(targetX, this.x, delta, deltaX);
        this.y = AnimationUtil.calculateCompensation(targetY, this.y, delta, deltaY);
    }

    public float getX() {
        return this.x;
    }

    public void setX(final float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(final float y) {
        this.y = y;
    }
}
