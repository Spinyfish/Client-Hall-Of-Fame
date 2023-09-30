package dev.tenacity.anticheat.checks.movement.speed;

import dev.tenacity.anticheat.Detection;
import net.minecraft.entity.player.EntityPlayer;

public class SpeedA extends Detection {

    public SpeedA() {
        super("Speed A");
    }

    @Override
    public boolean runCheck(EntityPlayer player) {

        if (mc.thePlayer.ticksExisted % 5 == 0) {
            this.setViolations(0);
        }

        if (player.posX - player.lastTickPosX > 0.075) {
            this.addViolations(1);
        }

        return this.getViolations() > 2;
    }
}
