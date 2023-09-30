package dev.africa.pandaware.impl.module.player.antivoid.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.player.antivoid.AntiVoidModule;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FuncraftAntiVoid extends ModuleMode<AntiVoidModule> {
    public FuncraftAntiVoid(String name, AntiVoidModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (mc.thePlayer.fallDistance >= this.getParent().getFallDistance().getValue().doubleValue() && !PlayerUtils.isBlockUnder()) {
            MovementUtils.strafe(event, MovementUtils.getBaseMoveSpeed());
            if (mc.thePlayer.isCollidedHorizontally && mc.thePlayer.ticksExisted % 150 == 0 && !PlayerUtils.isBlockUnder()) {
                for (int i = 0; i < 4; i++) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true
                    ));
                }
                double motion = 0.42F;
                motion += PlayerUtils.getJumpBoostMotion();

                event.y = motion;
            } else {
                event.y = -0.00001;
            }
        }
    };
}
