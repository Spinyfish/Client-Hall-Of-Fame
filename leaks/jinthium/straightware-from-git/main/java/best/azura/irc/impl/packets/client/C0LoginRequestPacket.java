package best.azura.irc.impl.packets.client;

import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;

public class C0LoginRequestPacket extends Packet {

    public static int packetId = 0;

    public C0LoginRequestPacket(int Id) {
        super(Id);
        packetId = Id;
    }


    public C0LoginRequestPacket() {
        super((User)null);
        Id = packetId;
    }

    public String getUsername() {
        if (getContent().has("username")) {
            return getContent().get("username").getAsString();
        }
        return null;
    }

    public String getHardwareId() {
        if (getContent().has("hwid")) {
            return getContent().get("hwid").getAsString();
        }

        return null;
    }

    public String getPassword() {
        if (getContent().has("password")) {
            return getContent().get("password").getAsString();
        }

        return null;
    }

    public int getProduct() {
        if (getContent().has("product")) {
            return getContent().get("product").getAsInt();
        }

        return -1;
    }

    public long getIntentId() {
        if (getContent().has("intent")) {
            return getContent().get("intent").getAsInt();
        }

        return -1L;
    }

    public boolean isIntent() {
        return getIntentId() != -1 && getProduct() != -1;
    }

    public boolean isValidFormat() {
        return (getUsername() != null && getHardwareId() != null && getPassword() != null && getProduct() != -1) || (getIntentId() != -1 && getProduct() != -1);
    }

}
