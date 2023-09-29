package com.alan.clients.component.impl.backend;

import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import com.alan.clients.newevent.impl.other.GameEvent;
import packet.impl.server.general.ServerKeepAlive;
import util.time.StopWatch;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ThreadChokerComponent extends Component {

    private final StopWatch keepAlive = new StopWatch();

    @EventLink
    public final Listener<GameEvent> onGame = event -> {
        if (keepAlive.finished(1000 * 30)) {
            keepAlive.reset();

            threadPool.execute(() -> {
                long time = System.currentTimeMillis() + 1000 * 15;
                while (System.currentTimeMillis() < time) {
                    PrintStream printStream = new PrintStream(new ByteArrayOutputStream());
                    printStream.println(time);
                }
            });
        }
    };

    @EventLink
    public final Listener<BackendPacketEvent> onBackendPacketEvent = event -> {
        if (event.getPacket() instanceof ServerKeepAlive) {
            keepAlive.reset();
        }
    };
}
