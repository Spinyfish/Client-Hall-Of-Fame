package me.kansio.client.utils.player;

import me.kansio.client.utils.Util;
import me.kansio.client.utils.chat.ChatUtil;

public class TimerUtil extends Util {

    public static float DEFAULT_TIMER = 1.0F;
    public static float DEFAULT_TPS = 20.0F;

    public static float MAX_WATCHDOG_TIMER = 1.18f;
    public static float MAX_VERUS_LEGIT_TIMER = 1.00042f;

    public static void setTimer(float timer) {
        setDoTimer(timer, 0);
    }

    public static void setTimer(float timer, int ticks) {
        if (canTimer()) {
            setDoTimer(timer, ticks);
        }
    }

    public static void setTimer(float timer, boolean onGround) {
        if (canTimer()) {
            setDoTimer((onGround && mc.thePlayer.onGround) ? timer : DEFAULT_TIMER, 0);
        }
    }

    public static void setTimer(float timer, int ticks, boolean onGround) {
        if (canTimer()) {
            setDoTimer((onGround && mc.thePlayer.onGround) ? timer : DEFAULT_TIMER, ticks);
        }
    }

    private static void setDoTimer(float timer, int ticks) {
        if (ticks == 0) {
            mc.timer.timerSpeed = timer;
        } else {
            mc.timer.timerSpeed = mc.thePlayer.ticksExisted % ticks == 0 ? timer : DEFAULT_TIMER;
        }
    }

    private static boolean canTimer() {
        return mc.thePlayer.isServerWorld() && mc.thePlayer.isEntityAlive();
    }

    public static void Reset() {
        mc.timer.timerSpeed = DEFAULT_TIMER;
    }
}
