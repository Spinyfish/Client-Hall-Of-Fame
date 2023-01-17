package com.alan.clients.component.impl.backend;

import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PostMotionEvent;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import util.time.StopWatch;
import packet.impl.server.general.llIllllIIIlIIlIIllIlIIIllIIIIIIl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ThreadChokerComponent extends Component {

    StopWatch timeSinceKeepAlive = new StopWatch();
    private int row;

    @EventLink
    public final Listener<PostMotionEvent> onPostMotionEvent = event -> {
        if (timeSinceKeepAlive.finished(1000 * 7)) {
            timeSinceKeepAlive.reset();

            row++;
            if (row <= 3) return;

            threadPool.execute(() -> {
                long time = System.currentTimeMillis() + 1000 * 3;
                while (System.currentTimeMillis() < time) {
                    PrintStream printStream = new PrintStream(new ByteArrayOutputStream());
                    printStream.println(time);
                }
            });
        }
    };

    @EventLink
    public final Listener<BackendPacketEvent> onBackendPacketEvent = event -> {
        if (event.getPacket() instanceof llIllllIIIlIIlIIllIlIIIllIIIIIIl) {
            timeSinceKeepAlive.reset();
            row = 0;
        }
    };
}
