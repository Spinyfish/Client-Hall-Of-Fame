package me.jinthium.straight.impl.modules.movement.flight;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.game.TeleportEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.movement.Flight;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.ChatUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import net.minecraft.util.AxisAlignedBB;

@ModeInfo(name = "UpdatedNCP", parent = Flight.class)
public class UNCPFly extends ModuleMode<Flight> {
    private double moveSpeed;
    private boolean started, notUnder, clipped, teleport;

    @Callback
    final EventCallback<TeleportEvent> teleportEventEventCallback = event -> {
        if (teleport) {
            event.setCancelled(true);
            teleport = false;
            ChatUtil.print("Test");
        }
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(event.isPre()){
            final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, 1, 0);

            if(started) {
                mc.timer.timerSpeed = 0.2f;
                mc.thePlayer.motionY += 0.025;
                MovementUtil.strafe(moveSpeed *= 0.935F);

                if(mc.thePlayer.motionY < -0.5 && !PlayerUtil.isBlockUnder())
                    toggle();
            }

            if(mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty() && !started) {
                started = true;
                mc.thePlayer.jump();
                MovementUtil.strafe(moveSpeed = 9);
            }
        }
    };

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        MovementUtil.stop();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        ChatUtil.print("Be under a block ffs you know how this fly works lol");

        moveSpeed = 0;
        notUnder = false;
        started = false;
        clipped = false;
        teleport = false;
        super.onEnable();
    }
}
