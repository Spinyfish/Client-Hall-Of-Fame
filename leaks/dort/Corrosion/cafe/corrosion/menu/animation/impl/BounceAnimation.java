package cafe.corrosion.menu.animation.impl;

import cafe.corrosion.menu.animation.Animation;

public class BounceAnimation extends Animation {
    public BounceAnimation(long time) {
        super(time);
    }

    public double calculate() {
        double x = this.getProgression();
        double n1 = 7.5625D;
        double d1 = 2.75D;
        if (x < 1.0D / d1) {
            return n1 * x * x;
        } else if (x < 2.0D / d1) {
            return n1 * (x -= 1.5D / d1) * x + 0.75D;
        } else {
            return x < 2.5D / d1 ? n1 * (x -= 2.25D / d1) * x + 0.9375D : n1 * (x -= 2.625D / d1) * x + 0.984375D;
        }
    }
}
