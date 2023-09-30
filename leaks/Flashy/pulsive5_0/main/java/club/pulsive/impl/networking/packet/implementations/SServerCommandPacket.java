package club.pulsive.impl.networking.packet.implementations;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.networking.packet.ClientPacketHandler;
import club.pulsive.impl.networking.packet.Packet;
import club.pulsive.impl.networking.user.User;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SServerCommandPacket extends Packet {

    public SServerCommandPacket() {
        super(Pulsive.INSTANCE.getSocketClient().getPacketHandler().getIDForPacket(SServerCommandPacket.class));
    }

    @Override
    public void process(ClientPacketHandler packetHandler) {
        packetHandler.processServerCommandPacket(this);
    }

    public Object getResponse() {
        switch (getOperation()) {
            case PACKET: {
                return Pulsive.INSTANCE.getSocketClient().getGson().fromJson(new String(Base64.getDecoder().decode(data.get("response").getAsString())), Packet.class);
            }
            case LIST_USERS: {
                System.out.println(data.get("response"));
                List<User> clientUsers = new ArrayList<>();
                for (JsonElement user : data.get("response").getAsJsonArray()) {
                    User u = null;
                    clientUsers.add(u = Pulsive.INSTANCE.getSocketClient().getGson().fromJson(user.getAsString(), User.class));
                    System.out.println(u.getClientUsername());
                }
                return clientUsers;
            }
            default: {
                return data.get("response").getAsString();
            }
        }
    }

    public CServerCommandPacket.CommandOperation getOperation() {
        return CServerCommandPacket.CommandOperation.valueOf(data.get("operation").getAsString());
    }

    public String getTag() {
        return data.get("tag").getAsString();
    }

}
