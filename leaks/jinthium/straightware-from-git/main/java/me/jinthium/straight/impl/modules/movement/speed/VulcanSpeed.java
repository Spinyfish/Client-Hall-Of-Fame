package me.jinthium.straight.impl.modules.movement.speed;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.modules.combat.TargetStrafe;
import me.jinthium.straight.impl.modules.movement.Speed;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.potion.Potion;

@ModeInfo(name = "Vulcan", parent = Speed.class)
public class VulcanSpeed extends ModuleMode<Speed> {

    @Callback
    final EventCallback<PlayerMoveUpdateEvent> playerMoveUpdateEventEventCallback = event -> {
        switch (mc.thePlayer.offGroundTicks) {
            case 0 -> {
                mc.thePlayer.jump();
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    MovementUtil.strafe(0.6);
                } else {
                    MovementUtil.strafe(0.485);
                }
                TargetStrafe.strafe(event);
            }
            case 9 -> {
                if (!(MovementUtil.blockRelativeToPlayer(0, mc.thePlayer.motionY,
                        0) instanceof BlockAir)) {
                    MovementUtil.strafe();
                    TargetStrafe.strafe(event);
                }
            }
            case 2, 1 -> {
                MovementUtil.strafe();
                TargetStrafe.strafe(event);
            }
            case 5 -> mc.thePlayer.motionY = MovementUtil.predictedMotion(mc.thePlayer.motionY, 2);
        }
    };
}
