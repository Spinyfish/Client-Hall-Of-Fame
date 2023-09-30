package dev.africa.pandaware.impl.module.movement.noslow.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.movement.noslow.NoSlowModule;
import dev.africa.pandaware.utils.client.Timer;
import dev.africa.pandaware.utils.player.MovementUtils;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NCPNoSlow extends ModuleMode<NoSlowModule> {
    public NCPNoSlow(String name, NoSlowModule parent) {
        super(name, parent);
    }

    private final Timer timer = new Timer();

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (MovementUtils.isMoving() && mc.thePlayer.isUsingItem()) {
            if (timer.hasReached(50)) {
                timer.reset();
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
            if (event.getEventState() == Event.EventState.PRE) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            } else {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(
                        new BlockPos(-1, -1, -1), 255, null, 0.0f, 0.0f, 0.0f));
            }
        }
    };
}
