package best.azura.irc.impl.packets.client;

import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;
import best.azura.irc.utils.RSAUtil;

import java.security.PublicKey;

public class C7HandshakeRequest extends Packet {

    public static int packetId = 0;
    public PublicKey rsaPublicKey;

    public C7HandshakeRequest(int Id) {
        super(Id);
        packetId = Id;
        setEncrypt(false);
    }

    public C7HandshakeRequest() {
        super((User) null);
        Id = packetId;
        setEncrypt(false);
    }

    public String getPublicKeyString() {
        if (getContent().has("public")) {
            return getContent().get("public").getAsString();
        }
        return null;
    }

    public PublicKey getPublicKey() {
        if (getPublicKeyString() == null) {
            return null;
        }

        if (rsaPublicKey != null) return rsaPublicKey;

        try {
            return rsaPublicKey = RSAUtil.getPublicKey(getPublicKeyString());
        } catch (Exception ignore) {}

        return null;
    }

    public boolean isValid() {
        return getPublicKeyString() != null && getPublicKey() != null;
    }
}
