/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.clickgui.animations;

public class Animation {
    private long startTime = 0L;
    private int amount;
    private double speed;
    private double percent;
    private boolean reversed;
    private double value;

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void updateAnimation() {
        double percentage = Animation.getPercentage((double)this.amount / this.speed, this.startTime);
        if (this.reversed) {
            percentage = 100.0 - percentage;
        }
        this.value = Animation.getDoubleFromPercentage(percentage, this.amount);
        this.percent = percentage;
    }

    public static double getPercentage(double animationLengthMS, long startSysMS) {
        double time = System.currentTimeMillis() - startSysMS;
        double result = time / animationLengthMS * 100.0;
        return result <= 100.0 ? result : 100.0;
    }

    public boolean hasFinished() {
        return this.reversed ? this.getPercent() == 0.0 : this.getPercent() == 100.0 || this.startTime == 0L;
    }

    public static double getDoubleFromPercentage(double percentage, double size) {
        return size / 100.0 * percentage;
    }

    public double getValue() {
        return this.value;
    }

    public double getSpeed() {
        return this.speed;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPercent() {
        return this.percent;
    }

    public void setReverse(boolean reverse) {
        this.reversed = reverse;
    }

    public boolean isReversed() {
        return this.reversed;
    }
}

