package com.alan.clients.network;

import by.radioegor146.nativeobfuscator.Native;
import com.alan.clients.Client;
import com.alan.clients.network.handler.ServerPacketHandler;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vantage.HWIDUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import packet.Packet;
import packet.handler.impl.IServerPacketHandler;
import packet.impl.client.login.ClientLoginPacket;
import packet.impl.server.login.ServerLoginPacket;
import packet.type.ServerPacket;
import util.Communication;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

@Getter
@Native
public final class NetworkManager implements InstanceAccess {

    private static final String CLIENT_ID = "637e2ca2ada186cdfa19536a"; // Ask Tecnio, Alan or Ryuzaki for your client ID
    private Socket socket;
    private Communication communication;
    private IServerPacketHandler handler;
    @Setter
    public String username;
    private Thread loginThread;
    public String message;

    public void init(String username) {
        this.username = username;
//        this.connect("localhost", 18934);
        this.connect("144.172.67.17", 18934);
    }

    private void connect(String ip, int port) {
        if (loginThread != null) {
            loginThread.stop();
        }

        message = "";

        System.out.println(ip.equalsIgnoreCase("localhost") ? "Connecting to localhost" : "Connecting to backend - Primary");

        Client.INSTANCE.getEventBus().unregister(this);
        Client.INSTANCE.getEventBus().register(this);

        try {
            loginThread = new Thread(() -> {
                try {
                    System.out.println("SK");
                    this.socket = new Socket(ip, port);
                    System.out.println("CM");
                    this.communication = new Communication(socket, false);
                    System.out.println("HL");
                    this.handler = new ServerPacketHandler();
                } catch (final Exception exception) {
                    System.out.println("CN Error");
                    exception.printStackTrace();
                }

                System.out.println("CN");

                try {
                    System.out.println("LG");
                    this.communication.write(new ClientLoginPacket(
                            username,
                            InetAddress.getLocalHost().getHostName(),
                            System.getProperty("user.name"),
                            System.getProperty("os.name"),
                            HWIDUtil.getHWID(),
                            CLIENT_ID
                    ));
                } catch (UnknownHostException e) {
                    System.out.println("SP Error");
                    e.printStackTrace();
                }


                while (true) {
                    try {
                        final Packet packet = this.communication.read();

                        if (packet != null) {
                            packet.process(this.handler);

                            System.out.println("RP");

                            Client.INSTANCE.getEventBus().handle(new BackendPacketEvent((ServerPacket) packet));
                        } else loginThread.stop();

                        Thread.sleep(50);
                    } catch (final Exception exception) {
                        System.out.println("COM Error");
                        exception.printStackTrace();
                    }
                }

            });

            loginThread.setName("rise-network-thread");
            loginThread.start();
            System.out.println("ST");
        } catch (final Exception ex) {
            if (Client.DEVELOPMENT_SWITCH) ex.printStackTrace();
            System.err.println("Failed to connect to the backend!");
        }
    }


    @EventLink()
    public final Listener<BackendPacketEvent> onBackend = event -> {

        Packet<?> packet = event.getPacket();

        if (packet instanceof ServerLoginPacket) {
            ServerLoginPacket sPacketLoginResponse = ((ServerLoginPacket) packet);

            if (sPacketLoginResponse.isSuccess()) {
                GuiConnecting.a = true;
                Container.a = false;
                EntityPlayer.a = 3;
                GuiIngame.a = 33L;
            }

            message = sPacketLoginResponse.getInformation();
        }
    };

    public static boolean a() {
        return true;
    }
}
