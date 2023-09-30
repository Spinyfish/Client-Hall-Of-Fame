package club.pulsive.impl.networking.packet.implementations;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.networking.packet.ClientPacketHandler;
import club.pulsive.impl.networking.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class SUserUpdatePacket extends Packet {

    public SUserUpdatePacket() {
        super(Pulsive.INSTANCE.getSocketClient().getPacketHandler().getIDForPacket(SUserUpdatePacket.class));
    }

    @Override
    public void process(ClientPacketHandler packetHandler) {
        packetHandler.processUserUpdatePacket(this);
    }

    public JsonObject getValues() {
        return data.getAsJsonObject("values");
    }

    public enum UpdateType {
        CLIENT_USERNAME, UID, RANK
    }

    @Getter@AllArgsConstructor
    public static class Value {
        private final UpdateType type;
        private final Object value;
    }

}
