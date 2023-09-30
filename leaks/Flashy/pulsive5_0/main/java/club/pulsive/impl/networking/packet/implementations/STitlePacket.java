package club.pulsive.impl.networking.packet.implementations;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.networking.packet.ClientPacketHandler;
import club.pulsive.impl.networking.packet.Packet;

public class STitlePacket extends Packet {

    public STitlePacket() {
        super(Pulsive.INSTANCE.getSocketClient().getPacketHandler().getIDForPacket(STitlePacket.class));
    }

    @Override
    public void process(ClientPacketHandler packetHandler) {
        packetHandler.processTitlePacket(this);
    }

    public String getTitle() {
        return data.get("title").getAsString();
    }

    public String getSubTitle() {
        return data.get("subtitle").getAsString();
    }

    public int getFadeInValue() {
        return data.get("fade").getAsInt();
    }

    public int getStayValue() {
        return data.get("stay").getAsInt();
    }

    public int getFadeOutValue() {
        return data.get("fadeout").getAsInt();
    }

}
