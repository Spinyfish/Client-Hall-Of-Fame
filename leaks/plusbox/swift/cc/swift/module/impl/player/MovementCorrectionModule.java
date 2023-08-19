package cc.swift.module.impl.player;

import cc.swift.Swift;
import cc.swift.events.JumpEvent;
import cc.swift.events.MovementInputEvent;
import cc.swift.events.StrafeEvent;
import cc.swift.module.Module;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.util.MathHelper;

public final class MovementCorrectionModule extends Module {
    public MovementCorrectionModule() {
        super("MovementCorrection", Category.PLAYER);
    }

    @Handler
    public final Listener<MovementInputEvent> movementInputEventListener = event -> {
        double yawDifference = Math.toRadians(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - Swift.INSTANCE.getRotationHandler().getYaw()));
        double cos = Math.cos(yawDifference);
        double sin = Math.sin(yawDifference);

        double forward = Math.round(event.getMoveForward() * cos + event.getMoveStrafe() * sin);
        double strafe = Math.round(event.getMoveStrafe() * cos - event.getMoveForward() * sin);

        event.setMoveForward((float) forward);
        event.setMoveStrafe((float) strafe);
    };

    @Handler
    public final Listener<JumpEvent> jumpEventListener = event -> {
        event.setYaw(Swift.INSTANCE.getRotationHandler().getYaw());
    };

    @Handler
    public final Listener<StrafeEvent> strafeEventListener = event -> {
        event.setYaw(Swift.INSTANCE.getRotationHandler().getYaw());
    };

}
