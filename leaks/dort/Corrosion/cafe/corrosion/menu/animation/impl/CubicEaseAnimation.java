package cafe.corrosion.menu.animation.impl;

import cafe.corrosion.menu.animation.Animation;

public class CubicEaseAnimation extends Animation {
    public CubicEaseAnimation(long time) {
        super(time);
    }

    public double calculate() {
        double x = this.getProgression();
        return x < 0.5D ? 4.0D * x * x * x : 1.0D - Math.pow(-2.0D * x + 2.0D, 3.0D) / 2.0D;
    }
}
