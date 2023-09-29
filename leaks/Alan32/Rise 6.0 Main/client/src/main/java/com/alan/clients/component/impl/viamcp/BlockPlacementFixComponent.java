package com.alan.clients.component.impl.viamcp;

import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.viamcp.ViaMCP;

public final class BlockPlacementFixComponent extends Component {

    @EventLink()
    public final Listener<PacketSendEvent> onPacketSend = event -> {

        if (ViaMCP.getInstance().getVersion() >= ProtocolVersion.v1_11.getVersion()) {
            final Packet<?> packet = event.getPacket();

            if (packet instanceof C08PacketPlayerBlockPlacement) {
                final C08PacketPlayerBlockPlacement wrapper = ((C08PacketPlayerBlockPlacement) packet);

                wrapper.facingX /= 16.0F;
                wrapper.facingY /= 16.0F;
                wrapper.facingZ /= 16.0F;

                event.setPacket(wrapper);
            }
        }
    };

}
