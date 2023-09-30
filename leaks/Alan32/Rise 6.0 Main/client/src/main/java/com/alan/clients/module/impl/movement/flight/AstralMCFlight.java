package com.alan.clients.module.impl.movement.flight;

import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.TeleportEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.NumberValue;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Alan
 * @since 03.07.2022
 */
public class AstralMCFlight extends Mode<Flight> {

    private NumberValue height = new NumberValue("Height", this, 1, 0.1, 10, 0.1);
    private NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 10, 0.1);

    public AstralMCFlight(String name, Flight parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        ChatUtil.display("Place a block to fly");
    }

    @EventLink
    private Listener<TeleportEvent> teleport = event -> {
        mc.thePlayer.motionY = height.getValue().doubleValue();
        MoveUtil.strafe(speed.getValue().doubleValue());
        event.setCancelled(true);
        mc.thePlayer.setPosition(event.getPosX(), event.getPosY(), event.getPosZ());
        PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(event.getPosX(), event.getPosY(), event.getPosZ(), event.getYaw(), event.getPitch(), false));
    };

    @EventLink
    private Listener<PreMotionEvent> preMotion = event -> {
    };
}
