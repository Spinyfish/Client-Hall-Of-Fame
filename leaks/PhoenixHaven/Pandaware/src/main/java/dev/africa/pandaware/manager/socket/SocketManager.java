package dev.africa.pandaware.manager.socket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import dev.africa.packetapi.Packet;
import dev.africa.packetapi.PacketRegistry;
import dev.africa.packetapi.encoder.PacketSerializer;
import dev.africa.packetapi.impl.ActionPacket;
import dev.africa.packetapi.impl.AuthenticationPacket;
import dev.africa.packetapi.impl.PingPacket;
import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.interfaces.Initializable;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.utils.client.HWIDUtils;
import dev.africa.pandaware.utils.java.CryptUtils;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SocketManager implements Initializable, MinecraftInstance {
    @Getter
    private boolean wasConnected;

    private WebSocket webSocket;

    private final Queue<Packet> packetQueue = new ConcurrentLinkedQueue<>();

    private ScheduledExecutorService reconnectService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final PacketListener packetListener = new PacketListener();

    @Setter
    @Getter
    private String username = RandomUtils.randomString(10);

    @Override
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.webSocket.sendClose();
            this.webSocket.disconnect();
        }));

        Client.getInstance().getEventDispatcher().subscribe(this.packetListener);

        this.start();
    }

    @Override
    public void shutdown() {
        Client.getInstance().getEventDispatcher().unsubscribe(this.packetListener);
        wasConnected = false;
    }

    void start() {
        if (System.getProperty("142d97db-2d4e-45a4-94a0-a976cd34cce6") == null) {
            return;
        }

        this.username = this.username.replace(" ", "")
                .replaceAll("[^\\p{ASCII}]", "");

        this.packetQueue.clear();
        this.executorService = Executors.newSingleThreadScheduledExecutor();

        final String hwid = HWIDUtils.getHWID();

        try {
            this.webSocket = new WebSocketFactory().createSocket("ws://157.245.91.112:42342/endpoint/socket");
            this.webSocket.addListener(new WebSocketAdapter() {
                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                    reconnectService.shutdownNow();
                    reconnectService = Executors.newSingleThreadScheduledExecutor();

                    wasConnected = true;
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) {
                    try {
                        final String decrypted = CryptUtils.decrypt(text, hwid);
                        final Packet packet = PacketSerializer.decode(decrypted);

                        if (packet == null) return;

                        final PacketRegistry packetRegistry = PacketRegistry.getByName(packet.getPacketName());

                        packetListener.handlePacket(packet, packetRegistry);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                                           WebSocketFrame clientCloseFrame, boolean closedByServer) {
                    if (wasConnected) {
                        reconnect();
                    }
                }
            }).connect();

            this.sendPacket(new AuthenticationPacket(hwid, this.username));

            this.executorService.scheduleAtFixedRate(() -> {
                if (this.wasConnected && this.webSocket.isOpen() && !this.packetQueue.isEmpty()) {
                    try {
                        final Packet packet = this.packetQueue.poll();
                        final String encoded = PacketSerializer.encode(packet);

                        final String encrypted = CryptUtils.encrypt(encoded, (packet instanceof AuthenticationPacket ?
                                "aca96f90-72fb-4265-8da5-dce8b3092127" :
                                hwid));

                        this.webSocket.sendText(encrypted);
                    } catch (Exception ignored) {
                    }
                }
            }, 3L, 3L, TimeUnit.MILLISECONDS);

            this.executorService.scheduleAtFixedRate(() -> {
                this.sendPacket(new ActionPacket("requestUserUpdate", ""));
                this.sendPacket(new PingPacket(System.currentTimeMillis()));

                if (mc.getSession() != null) {
                    Client.getInstance()
                            .getSocketManager()
                            .sendPacket(new ActionPacket("mcUsername", mc.getSession().getUsername()));
                }
            }, 0L, 5L, TimeUnit.SECONDS);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> this.webSocket.disconnect()));
        } catch (Exception e) {
            this.reconnect();
        }
    }

    public void reconnect() {
        this.executorService.shutdownNow();
        this.reconnectService.shutdownNow();
        this.reconnectService = Executors.newSingleThreadScheduledExecutor();

        this.reconnectService.scheduleAtFixedRate(this::start, 6L, 6L, TimeUnit.SECONDS);
    }

    public void sendPacket(Packet packet) {
        this.packetQueue.add(packet);
    }
}
