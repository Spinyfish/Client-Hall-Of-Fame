package com.alan.clients.module.impl.player.nofall;

import com.alan.clients.component.impl.player.FallDistanceComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.module.impl.player.NoFall;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.value.Mode;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

/**
 * @author Auth
 * @since 3/02/2022
 */
public class PlaceNoFall extends Mode<NoFall> {

    public PlaceNoFall(String name, NoFall parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        float distance = FallDistanceComponent.distance;

        if (distance > 3) {
            PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(event.getPosX(), event.getPosY(), event.getPosZ(), event.getYaw(), event.getPitch(), true));
            PacketUtil.send(new C08PacketPlayerBlockPlacement(SlotComponent.getItemStack()));
            distance = 0;
        }

        FallDistanceComponent.distance = distance;
    };
}