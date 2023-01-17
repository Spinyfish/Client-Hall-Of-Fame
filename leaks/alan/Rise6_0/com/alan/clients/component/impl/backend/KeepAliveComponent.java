package com.alan.clients.component.impl.backend;

import com.alan.clients.Client;
import com.alan.clients.component.Component;
import com.alan.clients.network.NetworkManager;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PostMotionEvent;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import util.time.StopWatch;
import packet.impl.client.general.llIIlIlIllllIIllIllIIIIIIlIIIlII;
import packet.impl.server.general.llIllllIIIlIIlIIllIlIIIllIIIIIIl;

public class KeepAliveComponent extends Component {

    StopWatch timeSinceKeepAlive = new StopWatch();
    boolean sent;

    @EventLink
    public final Listener<PostMotionEvent> onPostMotion = event -> {
        if (timeSinceKeepAlive.finished(1000 * 10)) {
            timeSinceKeepAlive.reset();

            threadPool.execute(() -> {
                try {
                    if (sent) {
                        Client.INSTANCE.getNetworkManager().setConnected(false);

                        String email = Client.INSTANCE.getNetworkManager().email;
                        String password = Client.INSTANCE.getNetworkManager().password;
                        Client.INSTANCE.setNetworkManager(new NetworkManager());
                        Client.INSTANCE.getNetworkManager().email = email;
                        Client.INSTANCE.getNetworkManager().password = password;
                        Client.INSTANCE.getNetworkManager().init();
                    }


                    Client.INSTANCE.getNetworkManager().getCommunication().write(new llIIlIlIllllIIllIllIIIIIIlIIIlII());
                } catch (Exception ignored) {
                    // Don't wanna give crackheads info
                }
                sent = true;
            });
        }
    };

    @EventLink
    public final Listener<BackendPacketEvent> onBackendPacketEvent = event -> {
        if (event.getPacket() instanceof llIllllIIIlIIlIIllIlIIIllIIIIIIl) {
            sent = false;
            Client.INSTANCE.getNetworkManager().setConnected(true);
        }
    };
}
