package com.alan.clients.component.impl.backend;

import com.alan.clients.Client;
import com.alan.clients.component.Component;
import com.alan.clients.component.impl.render.NotificationComponent;
import com.alan.clients.network.NetworkManager;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import com.alan.clients.newevent.impl.other.GameEvent;
import packet.impl.client.general.ClientKeepAlive;
import packet.impl.server.general.ServerKeepAlive;
import util.time.StopWatch;

public class KeepAliveComponent extends Component {

    private final StopWatch clientKA = new StopWatch();
    private final StopWatch serverKA = new StopWatch();

    @EventLink
    public final Listener<GameEvent> onGame = event -> {
        if (Client.INSTANCE.getNetworkManager().getCommunication() == null) {
            serverKA.reset();
            clientKA.reset();
            return;
        }

        if (serverKA.finished(30000)) {
            serverKA.reset();

            NotificationComponent.post("Rise", "Reconnecting to backend...");

            String username = Client.INSTANCE.getNetworkManager().getUsername();
            Client.INSTANCE.setNetworkManager(new NetworkManager());
            Client.INSTANCE.getNetworkManager().init(username);
        }

        if (clientKA.finished(15000)) {
            clientKA.reset();

            try {
                Client.INSTANCE.getNetworkManager().getCommunication().write(new ClientKeepAlive());
            } catch (Exception ignored) {
            }
        }
    };

    @EventLink
    public final Listener<BackendPacketEvent> onBackendPacketEvent = event -> {
        if (event.getPacket() instanceof ServerKeepAlive) {
            serverKA.reset();
        }
    };
}
