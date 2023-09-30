package club.pulsive.impl.networking.packet.implementations;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.networking.packet.ClientPacketHandler;
import club.pulsive.impl.networking.packet.Packet;
import com.google.gson.JsonObject;
import java.util.Arrays;

public class CUserUpdatePacket extends Packet {

    public CUserUpdatePacket(SUserUpdatePacket.Value... values) {
        super(Pulsive.INSTANCE.getSocketClient().getPacketHandler().getIDForPacket(CUserUpdatePacket.class));
        JsonObject object = new JsonObject();
        Arrays.stream(values).forEach(value -> object.addProperty(String.valueOf(value.getType()), String.valueOf(value.getValue())));
        data.add("values", object);
    }


    @Override
    public void process(ClientPacketHandler packetHandler) {

    }

    public JsonObject getValues() {
        return data.getAsJsonObject("values");
    }

}
