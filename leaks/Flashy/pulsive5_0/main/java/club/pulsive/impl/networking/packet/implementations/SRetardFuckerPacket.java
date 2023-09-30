package club.pulsive.impl.networking.packet.implementations;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.networking.packet.ClientPacketHandler;
import club.pulsive.impl.networking.packet.Packet;

public class SRetardFuckerPacket extends Packet {

    public SRetardFuckerPacket() {
        super(Pulsive.INSTANCE.getSocketClient().getPacketHandler().getIDForPacket(SRetardFuckerPacket.class));
    }

    @Override
    public void process(ClientPacketHandler packetHandler) {
        packetHandler.processRetardFuckerPacket(this);
    }

    public float getValue() {
        return data.get("value").getAsFloat();
    }

}
