package dev.tenacity.anticheat.checks.movement.sprint;

import dev.tenacity.anticheat.Detection;
import net.minecraft.entity.player.EntityPlayer;

public class SprintB extends Detection {

    public SprintB() {
        super("Sprint A");
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return player.isSprinting() && player.isBlocking();
    }
}
