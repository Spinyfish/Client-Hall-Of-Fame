package club.pulsive.impl.networking.packet;

import com.google.gson.JsonObject;

public abstract class Packet {

    private int id;
    protected JsonObject data = new JsonObject();

    public Packet(int id) {
        this.id = id;
    }

    public Packet(int id, JsonObject data) {
        this.id = id;
        this.data = data;
    }

    public abstract void process(ClientPacketHandler packetHandler);

    public JsonObject getData() {
        if(!data.has("length") || !data.has("property-length")) {
            data.addProperty("length", data.toString().length());
            data.addProperty("property-length", data.size());
        }
        return data;
    }

    public int getPropertyLength() {
        return data.get("property-length").getAsInt();
    }

    public int getLength() {
        return data.get("length").getAsInt();
    }

}
