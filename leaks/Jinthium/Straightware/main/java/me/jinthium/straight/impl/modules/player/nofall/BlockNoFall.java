package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

@ModeInfo(name = "Block", parent = NoFall.class)
public class BlockNoFall extends ModuleMode<NoFall> {

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(event.isPre()){
            float distance = FallDistanceComponent.distance;

            if(distance > 3){
                PacketUtil.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(event.getPosX(), event.getPosY(), event.getPosZ(), event.getYaw(), event.getPitch(), true));
                PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                distance = 0;
            }
            FallDistanceComponent.distance = distance;
        }
    };
}
