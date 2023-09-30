package Reality.server.packets.client;

import Moses.server.packets12.ClientPacket;
import Moses.server.packets12.Packet;
import Moses.server.packets12.PacketType;
import Reallyu.util.json.JsonUtil;

public class C02PacketCommand extends ClientPacket implements Packet {

    public C02PacketCommand(String command){
        this.packetType = PacketType.C02;
        this.content = command;
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
