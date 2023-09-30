package dev.africa.packetapi.encoder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.africa.packetapi.Packet;
import dev.africa.packetapi.PacketRegistry;
import lombok.experimental.UtilityClass;

import java.util.Base64;

@UtilityClass
public class PacketSerializer {
    private final Gson gson = new GsonBuilder().create();

    public String encode(Packet packet) {
        final String json = gson.toJson(packet);

        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    public Packet decode(String jsonIn) {
        final String decodedJson = new String(Base64.getDecoder().decode(jsonIn));
        final JsonObject jsonObject = new JsonParser().parse(decodedJson).getAsJsonObject();

        if (!jsonObject.has("packetName") ||
                !jsonObject.has("assemblyName"))
            return null;

        final String packetName = jsonObject.get("packetName").getAsString();

        final PacketRegistry packetRegistry = PacketRegistry.getByName(packetName);

        if (packetRegistry == null)
            return null;

        return gson.fromJson(decodedJson, packetRegistry.getClazz());
    }
}
