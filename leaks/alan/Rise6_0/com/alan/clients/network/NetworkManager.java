package com.alan.clients.network;

import com.alan.clients.Client;
import com.alan.clients.network.handler.ServerPacketHandler;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import lombok.Getter;
import lombok.Setter;
import packet.Packet;
import packet.handler.impl.IServerPacketHandler;
import packet.impl.client.login.lIIIIIllIIIIlIIIlIIllIlIIlIlIIIl;
import packet.impl.server.login.lIIlllIIIllIIIIIllIIIllllIllIllI;
import packet.type.ServerPacket;
import util.Communication;

import java.net.InetAddress;
import java.net.Socket;

@Getter
public final class NetworkManager implements InstanceAccess {

    private static final String IP = "localhost" /*144.172.67.119 Vantage backend ip */;
    private static final int PORT = 18935 /* Rise backend port */;
    private static final String CLIENT_ID = "632eabdcb417a8e83f0ee98b"; // Ask Tecnio, Alan or Ryuzaki for your client ID

    private Socket socket;
    private Communication communication;
    private IServerPacketHandler handler;
    @Setter
    private boolean connected;
    public String email, password;
    Thread loginThread;

    public void init() {
        if (IP.equalsIgnoreCase("localhost")) {
            System.out.println("Alan forgot to make the backend ip not localhost");
        }

        if (Client.DEVELOPMENT_SWITCH) System.out.println("Connecting to backend...");
        Client.INSTANCE.getEventBus().register(this);

        try {
            this.socket = new Socket(IP, PORT);
            this.communication = new Communication(socket, false);
            this.handler = new ServerPacketHandler();

            loginThread = new Thread(() -> {
                try {
                    while (true) {
                        final Packet packet = this.communication.read();

                        if (packet != null) {
                            packet.process(this.handler);

                            Client.INSTANCE.getEventBus().handle(new BackendPacketEvent((ServerPacket) packet));
                        }
                    }
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }
            });

            loginThread.setName("rise-network-thread");
            loginThread.start();

            this.communication.write(new lIIIIIllIIIIlIIIlIIllIlIIlIlIIIl(
                    email,
                    password,
                    InetAddress.getLocalHost().getHostName(),
                    System.getProperty("user.name"),
                    System.getProperty("os.name"),
                    "4",
                    CLIENT_ID
            ));
        } catch (final Exception ex) {
            if (Client.DEVELOPMENT_SWITCH) ex.printStackTrace();

            System.err.println("Failed to connect to the backend!");
        }
    }

    @EventLink()
    public final Listener<BackendPacketEvent> onBackend = event -> {

        Packet<?> packet = event.getPacket();

        if (packet instanceof lIIlllIIIllIIIIIllIIIllllIllIllI) {
            lIIlllIIIllIIIIIllIIIllllIllIllI sPacketLoginResponse = ((lIIlllIIIllIIIIIllIIIllllIllIllI) packet);

            if (sPacketLoginResponse.isSuccess()) {
                connected = true;
            }
        }
    };
}
