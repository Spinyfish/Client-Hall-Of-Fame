package club.marsh.bloom.impl.utils.combat;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Getter
@Setter
public class ClickTimer {
    private double minCps, cps;
    private long clicks = 0L, lastClick = System.currentTimeMillis(); //long because i BET someones gonna click to the integer limit
    private Random random = new Random();
    public ClickTimer(Double cps) {
        this.minCps = cps;
    }

    public void update() {
        cps = minCps;
        cps += random.nextDouble();
        cps *= 0.75+random.nextDouble()/4;
        cps *= 1 + random.nextDouble();
        cps += Math.sin(System.currentTimeMillis()/500D) + random.nextGaussian();
        cps += Math.cos(random.nextInt(500) + System.currentTimeMillis());
    }

    public boolean hasEnoughTimeElapsed(boolean resetTimer) {
        boolean result = System.currentTimeMillis()-lastClick > 1000 / cps;
        if (resetTimer && result)
            lastClick = System.currentTimeMillis();
        return result;
    }


}
