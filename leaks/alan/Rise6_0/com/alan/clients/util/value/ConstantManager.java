package com.alan.clients.util.value;

import com.alan.clients.Client;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import packet.Packet;
import packet.impl.client.protection.ClientConstantRequest;
import packet.impl.server.protection.ServerAcceptJoinServerRequest;
import packet.impl.server.protection.ServerConstantResult;

public class ConstantManager {
    // If src gets leaked change these constants up
    public double l = 0; // PI
    public double I = 0; // TAU
    public float J = 0; // Max Pitch
    public double O = 0; // 180
    public boolean initiated;


    public void init() {
        if (!initiated) {
            Client.INSTANCE.getEventBus().register(this);
            Client.INSTANCE.getNetworkManager().getCommunication().write(new ClientConstantRequest());
        }

        initiated = true;
    }

    @EventLink()
    public final Listener<BackendPacketEvent> onBackend = event -> {
        Packet packet = event.getPacket();

        if (packet instanceof ServerConstantResult) {
            ServerConstantResult serverConstantResult = ((ServerConstantResult) packet);

            this.l = serverConstantResult.l;
            this.I = serverConstantResult.I;
            this.J = serverConstantResult.J;
            this.O = serverConstantResult.O;

        }
    };
}
