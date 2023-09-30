package club.pulsive.impl.networking.packet.implementations;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.networking.packet.ClientPacketHandler;
import club.pulsive.impl.networking.packet.Packet;
import club.pulsive.impl.networking.user.User;

public class SUserConnectPacket extends Packet {

    public SUserConnectPacket() {
        super(Pulsive.INSTANCE.getSocketClient().getPacketHandler().getIDForPacket(SUserConnectPacket.class));
    }

    @Override
    public void process(ClientPacketHandler packetHandler) {
        packetHandler.processUserConnectPacket(this);
    }

    public User getUser() {
        return Pulsive.INSTANCE.getSocketClient().getGson().fromJson(data.get("user").getAsString(), User.class);
    }

}
