package com.alan.clients.module.impl.player.antivoid;

import com.alan.clients.component.impl.player.BlinkComponent;
import com.alan.clients.module.impl.player.AntiVoid;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.Mode;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;

public class BlinkAntiVoid extends Mode<AntiVoid> {

    private final NumberValue distance = new NumberValue("Distance", this, 5, 0, 10, 1);

    private Vec3 position;
    private boolean overVoid;

    public BlinkAntiVoid(String name, AntiVoid parent) {
        super(name, parent);
    }

    @Override
    public void onDisable() {
        BlinkComponent.blinking = false;
    }

    @EventLink()
    public final Listener<PacketSendEvent> onPacketSend = event -> {

        final Packet<?> packet = event.getPacket();

        if (packet instanceof C03PacketPlayer) {
            final C03PacketPlayer wrapper = (C03PacketPlayer) packet;

            if (!PlayerUtil.isBlockUnder()) {
                BlinkComponent.blinking = true;

                overVoid = true;

                if (position != null && mc.thePlayer.fallDistance > distance.getValue().floatValue()) {
                    BlinkComponent.packets.clear();
                    BlinkComponent.blinking = false;

                    mc.thePlayer.setPosition(position.xCoord, position.yCoord, position.zCoord);

                    mc.thePlayer.motionY = MoveUtil.predictedMotion(0);
                    MoveUtil.stop();
                }
            } else {

                if (overVoid) {
                    overVoid = false;
                    BlinkComponent.blinking = false;
                }

                position = new Vec3(wrapper.x, wrapper.y, wrapper.z);
            }
        }
    };
}