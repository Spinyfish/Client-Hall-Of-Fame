package com.alan.clients.module.impl.player.nofall;

import com.alan.clients.component.impl.player.BlinkComponent;
import com.alan.clients.component.impl.player.FallDistanceComponent;
import com.alan.clients.module.impl.player.NoFall;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.value.Mode;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.PlayerUtil;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;

/**
 * @author Alan
 * @since 3/02/2022
 */
public class WatchdogNoFall extends Mode<NoFall> {

    private boolean running;

    public WatchdogNoFall(String name, NoFall parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        final float distance = FallDistanceComponent.distance;

        if (distance > 3 && PlayerUtil.isBlockUnder(20)) {
            BlinkComponent.blinking = true;
            BlinkComponent.setExempt(C0APacketAnimation.class, C02PacketUseEntity.class,
                    C03PacketPlayer.class, C03PacketPlayer.C04PacketPlayerPosition.class,
                    C03PacketPlayer.C06PacketPlayerPosLook.class);

            event.setOnGround(true);
            PacketUtil.send(new C08PacketPlayerBlockPlacement(null));
            this.running = true;
        } else if (this.running) {
            this.running = false;
            BlinkComponent.blinking = false;
        }
    };
}