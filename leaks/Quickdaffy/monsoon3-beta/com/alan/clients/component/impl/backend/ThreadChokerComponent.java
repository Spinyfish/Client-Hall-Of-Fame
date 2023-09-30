/*
 * Decompiled with CFR 0.152.
 */
package com.alan.clients.component.impl.backend;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import packet.impl.server.general.llIllllIIIlIIlIIllIlIIIllIIIIIIl;
import util.time.StopWatch;
import wtf.monsoon.impl.event.EventBackendPacket;
import wtf.monsoon.impl.event.EventUpdate;

public class ThreadChokerComponent {
    StopWatch timeSinceKeepAlive = new StopWatch();
    private int row;
    @EventLink
    public final Listener<EventUpdate> onGame = event -> {
        if (this.timeSinceKeepAlive.finished(7000L)) {
            this.timeSinceKeepAlive.reset();
            ++this.row;
            if (this.row <= 3) {
                return;
            }
            new Thread(() -> {
                long time = System.currentTimeMillis() + 3000L;
                while (System.currentTimeMillis() < time) {
                    PrintStream printStream = new PrintStream(new ByteArrayOutputStream());
                    printStream.println(time);
                }
            }).start();
        }
    };
    @EventLink
    public final Listener<EventBackendPacket> onBackendPacketEvent = event -> {
        if (event.getPacket() instanceof llIllllIIIlIIlIIllIlIIIllIIIIIIl) {
            this.timeSinceKeepAlive.reset();
            this.row = 0;
        }
    };
}

