package dev.tenacity.anticheat.checks.combat;

import dev.tenacity.anticheat.Detection;
import net.minecraft.entity.player.EntityPlayer;

public class VelocityA extends Detection {

    public VelocityA() {
        super("Velocity A");
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return (player.hurtTime >= 0 && mc.thePlayer.motionY == 0);
    }

}
