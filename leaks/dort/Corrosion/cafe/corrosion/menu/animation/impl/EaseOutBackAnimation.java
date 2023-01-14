package cafe.corrosion.menu.animation.impl;

import cafe.corrosion.menu.animation.Animation;

public class EaseOutBackAnimation extends Animation {
    public EaseOutBackAnimation(long time) {
        super(time);
    }

    public double calculate() {
        double x = this.getProgression();
        double c1 = 1.70158D;
        double c3 = c1 + 1.0D;
        return 1.0D + c3 * Math.pow(x - 1.0D, 3.0D) + c1 * Math.pow(x - 1.0D, 2.0D);
    }
}
