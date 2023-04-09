package tech.dort.dortware.impl.utils;

import net.minecraft.util.Timer;

public class TimeUtils {
    public static void sleepTimer(Timer timer, float speed, long resetDelay) {
        new Thread(() -> {
            timer.timerSpeed = speed;
            try {
                Thread.sleep(resetDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timer.timerSpeed = 1.0F;
        }).start();
    }

}
