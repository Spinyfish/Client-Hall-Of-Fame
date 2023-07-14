/*
 * Decompiled with CFR 0.152.
 */
package com.alan.clients.network;

import com.alan.clients.network.handler.ServerPacketHandler;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.net.InetAddress;
import java.net.Socket;
import packet.Packet;
import packet.handler.impl.IServerPacketHandler;
import packet.impl.client.login.lIIIIIllIIIIlIIIlIIllIlIIlIlIIIl;
import packet.impl.server.login.lIIlllIIIllIIIIIllIIIllllIllIllI;
import util.Communication;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.event.EventBackendPacket;
import wtf.monsoon.misc.protection.ProtectedInitialization;

public final class NetworkManager {
    private static final String IP = "eu.greendev.xyz";
    private static final int PORT = 18935;
    private static final String CLIENT_ID = "639ec616a2180561d9afcfd8";
    private Socket socket;
    private Communication communication;
    private IServerPacketHandler handler;
    private boolean connected;
    public String email;
    public String password;
    Thread loginThread;
    public String message;
    private ProtectedInitialization protectedInitialization;
    @EventLink
    private final Listener<EventBackendPacket> eventListener = e -> {
        Packet packet = e.getPacket();
        if (packet instanceof lIIlllIIIllIIIIIllIIIllllIllIllI) {
            lIIlllIIIllIIIIIllIIIllllIllIllI sPacketLoginResponse = (lIIlllIIIllIIIIIllIIIllllIllIllI)packet;
            if (sPacketLoginResponse.isSuccess()) {
                this.connected = true;
                Wrapper.loggedIn = true;
                this.protectedInitialization = new ProtectedInitialization();
                this.protectedInitialization.start();
            }
            this.message = sPacketLoginResponse.getInformation();
        }
    };

    public void init() {
        this.message = "";
        if (IP.equalsIgnoreCase("localhost")) {
            System.out.println("Alan forgot to make the backend ip not localhost");
        }
        try {
            this.socket = new Socket(IP, 18935);
            this.communication = new Communication(this.socket, false);
            this.handler = new ServerPacketHandler();
            this.loginThread = new Thread(() -> {
                try {
                    while (true) {
                        Packet<?> packet;
                        if ((packet = this.communication.read()) == null) {
                            continue;
                        }
                        packet.process(this.handler);
                        Wrapper.getEventBus().post(new EventBackendPacket(packet));
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }
            });
            this.loginThread.setName("rise-network-thread");
            this.loginThread.start();
            this.communication.write(new lIIIIIllIIIIlIIIlIIllIlIIlIlIIIl(this.email, this.password, InetAddress.getLocalHost().getHostName(), System.getProperty("user.name"), System.getProperty("os.name"), "", CLIENT_ID));
        }
        catch (Exception ex) {
            Wrapper.getLogger().error("Failed to connect to the backend!");
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    public Communication getCommunication() {
        return this.communication;
    }

    public IServerPacketHandler getHandler() {
        return this.handler;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public Thread getLoginThread() {
        return this.loginThread;
    }

    public String getMessage() {
        return this.message;
    }

    public Listener<EventBackendPacket> getEventListener() {
        return this.eventListener;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public ProtectedInitialization getProtectedInitialization() {
        return this.protectedInitialization;
    }

    public void setProtectedInitialization(ProtectedInitialization protectedInitialization) {
        this.protectedInitialization = protectedInitialization;
    }
}

