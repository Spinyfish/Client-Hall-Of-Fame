package club.pulsive.impl.networking.packet.implementations;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.networking.packet.ClientPacketHandler;
import club.pulsive.impl.networking.packet.Packet;
import java.util.Base64;

public class SSoundPacket extends Packet {

    public SSoundPacket() {
        super(Pulsive.INSTANCE.getSocketClient().getPacketHandler().getIDForPacket(SSoundPacket.class));
    }

    @Override
    public void process(ClientPacketHandler packetHandler) {
        //packetHandler.processSendSoundPacket(this);
    }

    public byte[] getBytes() {
        return Base64.getDecoder().decode(data.get("bytes").getAsString());
    }
}
