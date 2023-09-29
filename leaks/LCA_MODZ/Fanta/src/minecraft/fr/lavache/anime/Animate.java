package fr.lavache.anime;

public class Animate {

    private float value, min, max, speed, time;
    private boolean reversed;
    private Easing ease;

    public Animate() {
        this.ease = Easing.LINEAR;
        this.value = 0;
        this.min = 0;
        this.max = 1;
        this.speed = 50;
        this.reversed = false;
    }

    public void reset() { time = min; }

    public Animate update() {
        if (reversed) {
            if (time > min) time -= (Delta.DELTATIME * .001F * speed);
        } else {
            if (time < max) time += (Delta.DELTATIME * .001F * speed);
        }
        time = clamp(time, min, max);
        this.value = getEase().ease(time, min, max, max);
        return this;
    }

    public Animate setValue(float value) {
        this.value = value;
        return this;
    }
    public Animate setMin(float min) {
        this.min = min;
        return this;
    }

    public Animate setMax(float max) {
        this.max = max;
        return this;
    }

    public Animate setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public Animate setReversed(boolean reversed) {
        this.reversed = reversed;
        return this;
    }

    public Animate setEase(Easing ease) {
        this.ease = ease;
        return this;
    }

    public float getValue() { return value; }
    public float getMin() { return min; }
    public float getMax() { return max; }
    public float getSpeed() { return speed; }
    public boolean isReversed() { return reversed; }
    public Easing getEase() { return ease; }

    private float clamp(float num, float min, float max) { return num < min ? min : (num > max ? max : num); }
}
