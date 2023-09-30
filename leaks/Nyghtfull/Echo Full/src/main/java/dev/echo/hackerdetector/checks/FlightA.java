package dev.echo.hackerdetector.checks;

import dev.echo.hackerdetector.Category;
import dev.echo.hackerdetector.Detection;
import dev.echo.hackerdetector.utils.MovementUtils;
import net.minecraft.entity.player.EntityPlayer;

public class FlightA extends Detection {

    public FlightA() {
        super("Flight A", Category.MOVEMENT);
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return !player.onGround && player.motionY == 0 && MovementUtils.isMoving(player);
    }
}
