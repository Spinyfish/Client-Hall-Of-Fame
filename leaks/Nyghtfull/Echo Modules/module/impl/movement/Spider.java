package dev.echo.module.impl.movement;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.utils.player.MovementUtils;
import net.minecraft.util.MathHelper;

public final class Spider extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "Vulcan");

    public Spider() {
        super("Spider", Category.MOVEMENT, "Climbs you up walls like a spider");
        addSettings(mode);
    }

    @Link
    public Listener<MotionEvent> onMotionEvent = event -> {
        setSuffix(mode.getMode());
        if (mc.thePlayer.isCollidedHorizontally) {
            if (!mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                return;
            }
            switch (mode.getMode()) {
                case "Vanilla":
                    mc.thePlayer.jump();
                    break;
                case "Verus":
                    if (mc.thePlayer.ticksExisted % 3 == 0) {
                        mc.thePlayer.motionY = 0.42f;
                    }
                    break;
                case "Vulcan":
                    if (mc.thePlayer.isCollidedHorizontally) {
                        if (mc.thePlayer.ticksExisted % 2 == 0) {
                            event.setOnGround(true);
                            mc.thePlayer.motionY = 0.42F;
                        }
                        final double yaw = MovementUtils.getPlayerDirection();
                        event.setX(event.getX() - -MathHelper.sin((float) yaw) * 0.1f);
                        event.setZ(event.getZ() - MathHelper.cos((float) yaw) * 0.1f);
                    }
                    break;
            }
        }
    };
}
