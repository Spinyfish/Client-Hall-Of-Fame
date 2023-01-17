package com.alan.clients.module.impl.ghost.wtap;

import com.alan.clients.module.impl.ghost.WTap;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.value.Mode;
import com.alan.clients.util.packet.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public final class SilentWTap extends Mode<WTap> {

    private boolean sprinting, wTap;
    private int ticks;

    public SilentWTap(String name, WTap parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<AttackEvent> onAttack = event -> {
        ticks = 0;
    };

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        ticks++;

        switch (ticks) {
            case 1:
                wTap = Math.random() * 100 < getParent().chance.getValue().doubleValue();
                if (sprinting) {
                    PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                } else {
                    PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                }
                break;

            case 2:
                if (!sprinting) {
                    PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                }
                break;
        }
    };

    @EventLink()
    public final Listener<PacketSendEvent> onPacketSend = event -> {

        final Packet<?> packet = event.getPacket();

        if (packet instanceof C0BPacketEntityAction) {
            final C0BPacketEntityAction wrapper = (C0BPacketEntityAction) packet;

            switch (wrapper.getAction()) {
                case START_SPRINTING:
                    sprinting = true;
                    break;

                case STOP_SPRINTING:
                    sprinting = false;
                    break;
            }
        }
    };
}
