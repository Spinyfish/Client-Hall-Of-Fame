package club.pulsive.impl.networking.packet;

import com.google.gson.*;

public class ClientPacketDeserializer<Type> implements JsonDeserializer<Type> {

    private final ClientPacketHandler handler;

    public ClientPacketDeserializer(ClientPacketHandler handler) {
        this.handler = handler;
    }

    @Override
    public Type deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int id = jsonObject.get("id").getAsInt();
        return context.deserialize(jsonObject, handler.getPacketByID(id));
    }

}
