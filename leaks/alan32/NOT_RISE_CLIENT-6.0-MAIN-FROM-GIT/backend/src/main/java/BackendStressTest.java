import lombok.Setter;
import packet.Packet;
import packet.handler.impl.IServerPacketHandler;
import packet.impl.client.community.ClientCommunityMessageSend;
import packet.impl.client.login.ClientLoginPacket;
import packet.impl.server.community.ServerCommunityMessageSend;
import packet.impl.server.community.ServerCommunityPopulatePacket;
import packet.impl.server.general.ServerKeepAlive;
import packet.impl.server.login.ServerLoginPacket;
import packet.impl.server.protection.ServerConstantResult;
import packet.impl.server.protection.lIllIIlllIIIIlIllIIIIllIlllllIll;
import packet.impl.server.protection.lIllIlIlIlIIIlllIIIlllIIIIlllI;
import packet.impl.server.store.IllIIIllllIlIlIIIllIlIllllIIllll;
import util.Communication;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class BackendStressTest {

    private static final String IP = "localhost" /*144.172.67.17 Vantage backend ip */;
    private static final int PORT = 18934 /* Vantage backend port */;
    private static final String CLIENT_ID = "639ec616a2180561d9afcfd8"; // Monsoon client ID: 6381e31d7792829a181e9224 | Monsoon Beta client ID: 639ec616a2180561d9afcfd8

    private Socket socket;
    private Communication communication;
    private IServerPacketHandler handler;
    @Setter
    private boolean connected;
    public String email, password;
    Thread loginThread;
    public String message;

    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            new Thread(() -> new BackendStressTest().init()).start();
        }
        //new BackendStressTest().init();
    }

    public void init() {
        message = "";
        if (IP.equalsIgnoreCase("localhost")) {
            System.out.println("Alan forgot to make the backend ip not localhost");
        }

        //if (Client.DEVELOPMENT_SWITCH) System.out.println("Connecting to backend...");

        try {
            this.socket = new Socket(IP, PORT);
            this.communication = new Communication(socket, false);
            handler = new ServerPacketHandler();

            loginThread = new Thread(() -> {
                try {
                    while (true) {
                        System.gc();
                        final Packet packet = this.communication.read();

                        if (packet != null) {
                            packet.process(this.handler);
                            this.handlePacket(packet);
                        }
                    }
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }
            });

            loginThread.setName("rise-network-thread");
            loginThread.start();

            new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        this.communication.write(new ClientCommunityMessageSend("hi! " + new Random().nextFloat()));
                    }
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }
            }).start();

            // this.communication.write(new ClientCommunityMessageSend("hi! " + new Random().nextFloat()));

            this.communication.write(new ClientLoginPacket(
                    email,
                    InetAddress.getLocalHost().getHostName(),
                    System.getProperty("user.name"),
                    System.getProperty("os.name"),
                    "",
                    CLIENT_ID
            ));
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handlePacket(Packet packet) {
        if(packet instanceof ServerCommunityMessageSend) {
            System.out.println(((ServerCommunityMessageSend) packet).getMessage().message);
        }
    }


    public final class ServerPacketHandler implements IServerPacketHandler {
        @Override
        public void handle(ServerLoginPacket packet) {
            System.out.println(packet.getInformation());
        }

        @Override
        public void handle(ServerCommunityPopulatePacket packet) {

        }

        @Override
        public void handle(ServerCommunityMessageSend packet) {

        }

        @Override
        public void handle(lIllIlIlIlIIIlllIIIlllIIIIlllI packet) {

        }

        @Override
        public void handle(IllIIIllllIlIlIIIllIlIllllIIllll packet) {

        }

        @Override
        public void handle(lIllIIlllIIIIlIllIIIIllIlllllIll packet) {

        }

        @Override
        public void handle(ServerKeepAlive packet) {

        }

        @Override
        public void handle(ServerConstantResult serverConstantResult) {

        }
    }

}
