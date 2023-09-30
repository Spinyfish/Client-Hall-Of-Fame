package dev.echo.module.impl.player;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.network.PacketSendEvent;
import dev.echo.listener.event.impl.player.BoundingBoxEvent;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.utils.player.AlwaysUtil;
import dev.echo.utils.player.MoveUtil;
import dev.echo.utils.player.MovementUtils;
import dev.echo.utils.server.PacketUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;

@SuppressWarnings("unused")
public final class NoFall extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "Vulcan");

    public NoFall() {
        super("No Fall", Category.PLAYER, "Prevents fall damage");
        this.addSettings(mode);
    }

    @Link
    public Listener<MotionEvent> motionEventListener = e -> {
        if (!AlwaysUtil.isPlayerInGame()) {
            return;
        }
        if (e.isPre()) {
            this.setSuffix(mode.getMode());
            if (mc.thePlayer.fallDistance > 3.0 && isBlockUnder()) {
                switch (mode.getMode()) {
                    case "Vanilla":
                        e.setOnGround(true);
                        break;
                    case "Vulcan": {
                        e.setOnGround(true);
                    }
                    break;
                }
                mc.thePlayer.fallDistance = 0;
            }
        }
    };

    @Link
    public Listener<BoundingBoxEvent> onBoundingBoxEvent = event -> {
        if (AlwaysUtil.isPlayerInGame()) {
            if (mode.is("Verus") && mc.thePlayer.fallDistance > 2) {
                final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
                event.setBoundingBox(axisAlignedBB);
            }
        }
    };
    @Link
    public Listener<PacketSendEvent> packetSendEventListener = event -> {
        if (AlwaysUtil.isPlayerInGame()) {
            if (mode.is("Vulcan") && mc.thePlayer.fallDistance > 3.0 && isBlockUnder()) {
                if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                    event.setCancelled(true);
                }
            }
        }
    };

    private boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0) {
            return false;
        }
        for (int offset = 0; offset < (int) mc.thePlayer.posY + 2; offset += 2) {
            AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
