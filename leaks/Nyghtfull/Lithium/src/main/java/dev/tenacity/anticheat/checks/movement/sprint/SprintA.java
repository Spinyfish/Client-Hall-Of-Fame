package dev.tenacity.anticheat.checks.movement.sprint;

import dev.tenacity.anticheat.Detection;
import net.minecraft.entity.player.EntityPlayer;

public class SprintA extends Detection {

    public SprintA() {
        super("Sprint A");
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return player.isSprinting() && player.moveForward <= 0;
    }
}
