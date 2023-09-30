package club.pulsive.impl.networking.packet.implementations;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.networking.packet.ClientPacketHandler;
import club.pulsive.impl.networking.packet.Packet;

public class CChatPacket extends Packet {

    public CChatPacket(String message) {
        super(Pulsive.INSTANCE.getSocketClient().getPacketHandler().getIDForPacket(CChatPacket.class));
        data.addProperty("msg", message);
    }

    @Override
    public void process(ClientPacketHandler packetHandler) {

    }

    public String getMessage() {
        return data.get("msg").getAsString();
    }

}
