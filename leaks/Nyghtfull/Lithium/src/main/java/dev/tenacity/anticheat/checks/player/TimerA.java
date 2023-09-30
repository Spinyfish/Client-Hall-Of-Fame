package dev.tenacity.anticheat.checks.player;

import dev.tenacity.anticheat.Detection;
import net.minecraft.entity.player.EntityPlayer;

public class TimerA extends Detection {

    public long lastTime = System.currentTimeMillis();

    public TimerA() {
        super("Timer A");
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        // Code removed. Made by Liticane.
        return false;
    }
}
