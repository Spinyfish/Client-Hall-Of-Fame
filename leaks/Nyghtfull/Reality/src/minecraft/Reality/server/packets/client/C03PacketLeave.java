package Reality.server.packets.client;

import Moses.server.packets12.ClientPacket;
import Moses.server.packets12.Packet;
import Reallyu.util.json.JsonUtil;

public class C03PacketLeave implements Packet {
    @Override
    public String getJson() {
        return JsonUtil.toJson(this);
    }

    @Override
    public ClientPacket parsePacket(String j) {
        return (ClientPacket) JsonUtil.parseJson(j,this.getClass());
    }
}
