package ifk.server.packets.toserver;

import Moses.server.packets12.ClientPacket;
import Moses.server.packets12.Packet;
import Moses.server.packets12.PacketType;
import Moses.server.packets12.ServerPacket;
import Reallyu.util.json.JsonUtil;

public class S02PacketKick extends ServerPacket implements Packet {

    public S02PacketKick(String reason){
        content = reason;
        this.packetType = PacketType.S02;
    }

    @Override
    public String getJson() {
        return JsonUtil.toJson(this);
    }

    @Override
    public ClientPacket parsePacket(String j) {
        return (ClientPacket) JsonUtil.parseJson(j,this.getClass());
    }
}
