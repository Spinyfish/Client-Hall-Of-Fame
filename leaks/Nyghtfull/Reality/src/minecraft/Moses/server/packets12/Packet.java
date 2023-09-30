package Moses.server.packets12;

public interface Packet {
    String getJson();

    ClientPacket parsePacket(String j);

}
