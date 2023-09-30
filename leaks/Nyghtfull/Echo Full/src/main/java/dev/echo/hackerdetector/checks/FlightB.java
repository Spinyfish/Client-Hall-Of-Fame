package dev.echo.hackerdetector.checks;

import dev.echo.hackerdetector.Category;
import dev.echo.hackerdetector.Detection;
import dev.echo.hackerdetector.utils.MovementUtils;
import net.minecraft.entity.player.EntityPlayer;

public class FlightB extends Detection {

    public FlightB() {
        super("Flight B", Category.MOVEMENT);
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return player.airTicks > 20 && player.motionY == 0 && MovementUtils.isMoving(player);
    }
}
