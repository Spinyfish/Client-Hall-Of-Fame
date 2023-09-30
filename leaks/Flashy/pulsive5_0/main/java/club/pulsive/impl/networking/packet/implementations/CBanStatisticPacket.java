package club.pulsive.impl.networking.packet.implementations;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.networking.packet.ClientPacketHandler;
import club.pulsive.impl.networking.packet.Packet;

public class CBanStatisticPacket extends Packet {

    public CBanStatisticPacket(String reason, long time) {
        super(Pulsive.INSTANCE.getSocketClient().getPacketHandler().getIDForPacket(CBanStatisticPacket.class));
        data.addProperty("reason", reason);
        data.addProperty("time", time);
    }

    @Override
    public void process(ClientPacketHandler packetHandler) {

    }

    public long getTime() {
        return data.get("time").getAsLong();
    }

    public String getReason() {
        return data.get("reason").getAsString();
    }
}
