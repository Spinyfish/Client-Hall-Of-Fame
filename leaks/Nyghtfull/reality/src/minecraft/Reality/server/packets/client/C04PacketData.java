package Reality.server.packets.client;

import Moses.server.packets12.ClientPacket;
import Moses.server.packets12.Packet;
import Moses.server.packets12.PacketType;
import Nyghtfull.serverdate.User;
import Reallyu.util.json.JsonUtil;

public class C04PacketData extends ClientPacket implements Packet {

    public C04PacketData(User u, String con) {
        this.content = con;
        this.user = u;
        this.packetType = PacketType.C04;
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
