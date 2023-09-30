package club.pulsive.impl.networking;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.networking.packet.ClientPacketDeserializer;
import club.pulsive.impl.networking.packet.ClientPacketHandler;
import club.pulsive.impl.networking.packet.Packet;
import club.pulsive.impl.networking.packet.PacketEncoder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Getter
public class SocketClient extends WebSocketClient {

    private ClientPacketHandler packetHandler;
    private Gson gson;

    public SocketClient(URI serverUri) {
        super(serverUri);
        setTcpNoDelay(true);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        packetHandler = new ClientPacketHandler(this);
        packetHandler.init();
        gson = new GsonBuilder().registerTypeAdapter(Packet.class, new ClientPacketDeserializer<>(packetHandler)).create();
    }

    @Override
    public void onMessage(String message) {
        Packet packet = gson.fromJson(PacketEncoder.decode(message), Packet.class);
        if (packet != null) {
            JsonObject data = packet.getData();
            packet.process(packetHandler);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Pulsive.INSTANCE.connectSocket(false);
    }

    @Override
    public void onClosing(int code, String reason, boolean remote) {
        super.onClosing(code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {

    }
}
