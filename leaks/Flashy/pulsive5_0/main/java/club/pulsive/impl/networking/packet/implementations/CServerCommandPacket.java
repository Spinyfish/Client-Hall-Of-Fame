package club.pulsive.impl.networking.packet.implementations;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.networking.packet.ClientPacketHandler;
import club.pulsive.impl.networking.packet.Packet;
import java.util.Base64;

public class CServerCommandPacket extends Packet {

    public CServerCommandPacket(final CommandOperation commandOperation, Object request, String tag) {
        super(Pulsive.INSTANCE.getSocketClient().getPacketHandler().getIDForPacket(CServerCommandPacket.class));
        data.addProperty("operation", String.valueOf(commandOperation));
        data.addProperty("tag", tag);
        switch (commandOperation) {
            case LIST_USERS:
            case DISCONNECT_USER:
            case UNMUTE_USER:
            case MUTE_USER: {
                data.addProperty("request", String.valueOf(request));
                break;
            }
            case PACKET: {
                data.addProperty("request", Base64.getEncoder().encodeToString(Pulsive.INSTANCE.getSocketClient().getGson().toJson(request, Packet.class).getBytes()));
                break;
            }
        }
    }

    @Override
    public void process(ClientPacketHandler packetHandler) {

    }

    public Object getRequest() {
        switch (CommandOperation.valueOf(data.get("operation").getAsString())) {
            case PACKET: {
                return Pulsive.INSTANCE.getSocketClient().getGson().fromJson(new String(Base64.getDecoder().decode(data.get("request").getAsString())), Packet.class);
            }
            default: {
                return data.get("request").getAsString();
            }
        }
    }

    public CommandOperation getCommandOperation() {
        return CommandOperation.valueOf(data.get("operation").getAsString());
    }
    public String getTag() {
        return data.get("tag").getAsString();
    }

    public enum CommandOperation {
        MUTE_USER, UNMUTE_USER, DISCONNECT_USER, LIST_USERS, PACKET
    }

}
