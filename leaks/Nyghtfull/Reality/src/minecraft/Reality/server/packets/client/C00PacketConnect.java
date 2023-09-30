package Reality.server.packets.client;

import Moses.server.packets12.ClientPacket;
import Moses.server.packets12.Packet;
import Moses.server.packets12.PacketType;
import Nyghtfull.serverdate.User;
import Reallyu.util.json.JsonUtil;

public class C00PacketConnect extends ClientPacket implements Packet {

    public C00PacketConnect(User user) {
        this.user = user;
        this.packetType = PacketType.C00;
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
