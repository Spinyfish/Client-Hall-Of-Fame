package Reality.server.packets.client;

import Moses.server.packets12.ClientPacket;
import Moses.server.packets12.Packet;
import Moses.server.packets12.PacketType;
import Reallyu.util.json.JsonUtil;

public class C01PacketChat extends ClientPacket implements Packet{
    public C01PacketChat(String chat) {
        content = chat;
        packetType = PacketType.C01;
    }

    @Override
    public String getJson() {
        return JsonUtil.toJson(this);
    }

    @Override
    public ClientPacket parsePacket(String j) {
        return (ClientPacket) JsonUtil.parseJson(j, this.getClass());
    }
}
