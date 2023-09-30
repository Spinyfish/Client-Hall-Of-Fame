package club.pulsive.impl.networking.packet.implementations;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.networking.packet.ClientPacketHandler;
import club.pulsive.impl.networking.packet.Packet;
import club.pulsive.impl.networking.user.User;

public class SChatPacket extends Packet {

    public SChatPacket() {
        super(Pulsive.INSTANCE.getSocketClient().getPacketHandler().getIDForPacket(SChatPacket.class));
    }

    @Override
    public void process(ClientPacketHandler packetHandler) {
        packetHandler.processChatPacket(this);
    }

    public String getMessage() {
        return data.get("msg").getAsString();
    }

    public User getUser() {
        return Pulsive.INSTANCE.getSocketClient().getGson().fromJson(data.get("user").getAsString(), User.class);
    }

}
