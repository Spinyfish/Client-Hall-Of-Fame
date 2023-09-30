package com.alan.clients.util.value;

import com.alan.clients.Client;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import packet.Packet;
import packet.impl.client.protection.ClientConstantsPacket;
import packet.impl.server.protection.ServerConstantResult;

public class ConstantManager {
    public double l = 0; // PI
    public double I = 0; // TAU
    public float J = 0; // Max Pitch
    public double O = 0; // 180
    public boolean initiated = false;

    public void init() {
        initiated = false;
        if (!initiated) {
            Client.INSTANCE.getEventBus().register(this);
            Client.INSTANCE.getNetworkManager().getCommunication().write(new ClientConstantsPacket());
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
//            System.out.println("Received Constants");

        }
    };
}
