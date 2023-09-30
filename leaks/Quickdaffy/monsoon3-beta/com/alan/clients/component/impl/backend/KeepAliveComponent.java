/*
 * Decompiled with CFR 0.152.
 */
package com.alan.clients.component.impl.backend;

import com.alan.clients.network.NetworkManager;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;
import packet.impl.client.general.llIIlIlIllllIIllIllIIIIIIlIIIlII;
import packet.impl.server.general.llIllllIIIlIIlIIllIlIIIllIIIIIIl;
import util.time.StopWatch;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.event.EventBackendPacket;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.ui.login.LoginScreen;

public class KeepAliveComponent {
    StopWatch timeSinceKeepAlive = new StopWatch();
    boolean sent;
    @EventLink
    public final Listener<EventUpdate> onGame = event -> {
        if (this.timeSinceKeepAlive.finished(10000L) && !(Minecraft.getMinecraft().currentScreen instanceof LoginScreen)) {
            this.timeSinceKeepAlive.reset();
            new Thread(() -> {
                try {
                    if (this.sent) {
                        Wrapper.getMonsoon().getNetworkManager().setConnected(false);
                        String email = Wrapper.getMonsoon().getNetworkManager().email;
                        String password = Wrapper.getMonsoon().getNetworkManager().password;
                        Wrapper.getMonsoon().setNetworkManager(new NetworkManager());
                        Wrapper.getMonsoon().getNetworkManager().email = email;
                        Wrapper.getMonsoon().getNetworkManager().password = password;
                        Wrapper.getMonsoon().getNetworkManager().init();
                    }
                    Wrapper.getMonsoon().getNetworkManager().getCommunication().write(new llIIlIlIllllIIllIllIIIIIIlIIIlII());
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.sent = true;
            }).start();
        }
    };
    @EventLink
    public final Listener<EventBackendPacket> onBackendPacketEvent = event -> {
        if (event.getPacket() instanceof llIllllIIIlIIlIIllIlIIIllIIIIIIl) {
            this.sent = false;
            Wrapper.getMonsoon().getNetworkManager().setConnected(true);
        }
    };
}

