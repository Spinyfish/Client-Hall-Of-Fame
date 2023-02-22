package com.alan.clients.module.impl.player.nofall;

import com.alan.clients.component.impl.player.FallDistanceComponent;
import com.alan.clients.module.impl.player.NoFall;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Alan
 * @since 3/02/2022
 */
public class WatchdogNoFall extends Mode<NoFall> {
    boolean active;

    public WatchdogNoFall(String name, NoFall parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (FallDistanceComponent.distance > 3.5 && !(PlayerUtil.blockRelativeToPlayer(0, MoveUtil.predictedMotion(mc.thePlayer.motionY), 0) instanceof BlockAir) && mc.thePlayer.ticksSinceTeleport > 50) {
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 50 - Math.random(), mc.thePlayer.posZ, false));

            FallDistanceComponent.distance = 0;
        }
    };
}