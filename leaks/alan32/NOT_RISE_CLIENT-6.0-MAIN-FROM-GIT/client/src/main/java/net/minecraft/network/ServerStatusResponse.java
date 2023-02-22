package net.minecraft.network;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

import java.lang.reflect.Type;
import java.util.UUID;

public class ServerStatusResponse {
    private IChatComponent serverMotd;
    private ServerStatusResponse.PlayerCountData playerCount;
    private ServerStatusResponse.MinecraftProtocolVersionIdentifier protocolVersion;
    private String favicon;

    public IChatComponent getServerDescription() {
        return this.serverMotd;
    }

    public void setServerDescription(final IChatComponent motd) {
        this.serverMotd = motd;
    }

    public ServerStatusResponse.PlayerCountData getPlayerCountData() {
        return this.playerCount;
    }

    public void setPlayerCountData(final ServerStatusResponse.PlayerCountData countData) {
        this.playerCount = countData;
    }

    public ServerStatusResponse.MinecraftProtocolVersionIdentifier getProtocolVersionInfo() {
        return this.protocolVersion;
    }

    public void setProtocolVersionInfo(final ServerStatusResponse.MinecraftProtocolVersionIdentifier protocolVersionData) {
        this.protocolVersion = protocolVersionData;
    }

    public void setFavicon(final String faviconBlob) {
        this.favicon = faviconBlob;
    }

    public String getFavicon() {
        return this.favicon;
    }

    public static class MinecraftProtocolVersionIdentifier {
        private final String name;
        private final int protocol;

        public MinecraftProtocolVersionIdentifier(final String nameIn, final int protocolIn) {
            this.name = nameIn;
            this.protocol = protocolIn;
        }

        public String getName() {
            return this.name;
        }

        public int getProtocol() {
            return this.protocol;
        }

        public static class Serializer implements JsonDeserializer<ServerStatusResponse.MinecraftProtocolVersionIdentifier>, JsonSerializer<ServerStatusResponse.MinecraftProtocolVersionIdentifier> {
            public ServerStatusResponse.MinecraftProtocolVersionIdentifier deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
                final JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "version");
                return new ServerStatusResponse.MinecraftProtocolVersionIdentifier(JsonUtils.getString(jsonobject, "name"), JsonUtils.getInt(jsonobject, "protocol"));
            }

            public JsonElement serialize(final ServerStatusResponse.MinecraftProtocolVersionIdentifier p_serialize_1_, final Type p_serialize_2_, final JsonSerializationContext p_serialize_3_) {
                final JsonObject jsonobject = new JsonObject();
                jsonobject.addProperty("name", p_serialize_1_.getName());
                jsonobject.addProperty("protocol", Integer.valueOf(p_serialize_1_.getProtocol()));
                return jsonobject;
            }
        }
    }

    public static class PlayerCountData {
        private final int maxPlayers;
        private final int onlinePlayerCount;
        private GameProfile[] players;

        public PlayerCountData(final int maxOnlinePlayers, final int onlinePlayers) {
            this.maxPlayers = maxOnlinePlayers;
            this.onlinePlayerCount = onlinePlayers;
        }

        public int getMaxPlayers() {
            return this.maxPlayers;
        }

        public int getOnlinePlayerCount() {
            return this.onlinePlayerCount;
        }

        public GameProfile[] getPlayers() {
            return this.players;
        }

        public void setPlayers(final GameProfile[] playersIn) {
            this.players = playersIn;
        }

        public static class Serializer implements JsonDeserializer<ServerStatusResponse.PlayerCountData>, JsonSerializer<ServerStatusResponse.PlayerCountData> {
            public ServerStatusResponse.PlayerCountData deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
                final JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "players");
                final ServerStatusResponse.PlayerCountData serverstatusresponse$playercountdata = new ServerStatusResponse.PlayerCountData(JsonUtils.getInt(jsonobject, "max"), JsonUtils.getInt(jsonobject, "online"));

                if (JsonUtils.isJsonArray(jsonobject, "sample")) {
                    final JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "sample");

                    if (jsonarray.size() > 0) {
                        final GameProfile[] agameprofile = new GameProfile[jsonarray.size()];

                        for (int i = 0; i < agameprofile.length; ++i) {
                            final JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonarray.get(i), "player[" + i + "]");
                            final String s = JsonUtils.getString(jsonobject1, "id");
                            agameprofile[i] = new GameProfile(UUID.fromString(s), JsonUtils.getString(jsonobject1, "name"));
                        }

                        serverstatusresponse$playercountdata.setPlayers(agameprofile);
                    }
                }

                return serverstatusresponse$playercountdata;
            }

            public JsonElement serialize(final ServerStatusResponse.PlayerCountData p_serialize_1_, final Type p_serialize_2_, final JsonSerializationContext p_serialize_3_) {
                final JsonObject jsonobject = new JsonObject();
                jsonobject.addProperty("max", Integer.valueOf(p_serialize_1_.getMaxPlayers()));
                jsonobject.addProperty("online", Integer.valueOf(p_serialize_1_.getOnlinePlayerCount()));

                if (p_serialize_1_.getPlayers() != null && p_serialize_1_.getPlayers().length > 0) {
                    final JsonArray jsonarray = new JsonArray();

                    for (int i = 0; i < p_serialize_1_.getPlayers().length; ++i) {
                        final JsonObject jsonobject1 = new JsonObject();
                        final UUID uuid = p_serialize_1_.getPlayers()[i].getId();
                        jsonobject1.addProperty("id", uuid == null ? "" : uuid.toString());
                        jsonobject1.addProperty("name", p_serialize_1_.getPlayers()[i].getName());
                        jsonarray.add(jsonobject1);
                    }

                    jsonobject.add("sample", jsonarray);
                }

                return jsonobject;
            }
        }
    }

    public static class Serializer implements JsonDeserializer<ServerStatusResponse>, JsonSerializer<ServerStatusResponse> {
        public ServerStatusResponse deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            final JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "status");
            final ServerStatusResponse serverstatusresponse = new ServerStatusResponse();

            if (jsonobject.has("description")) {
                serverstatusresponse.setServerDescription(p_deserialize_3_.deserialize(jsonobject.get("description"), IChatComponent.class));
            }

            if (jsonobject.has("players")) {
                serverstatusresponse.setPlayerCountData(p_deserialize_3_.deserialize(jsonobject.get("players"), PlayerCountData.class));
            }

            if (jsonobject.has("version")) {
                serverstatusresponse.setProtocolVersionInfo(p_deserialize_3_.deserialize(jsonobject.get("version"), MinecraftProtocolVersionIdentifier.class));
            }

            if (jsonobject.has("favicon")) {
                serverstatusresponse.setFavicon(JsonUtils.getString(jsonobject, "favicon"));
            }

            return serverstatusresponse;
        }

        public JsonElement serialize(final ServerStatusResponse p_serialize_1_, final Type p_serialize_2_, final JsonSerializationContext p_serialize_3_) {
            final JsonObject jsonobject = new JsonObject();

            if (p_serialize_1_.getServerDescription() != null) {
                jsonobject.add("description", p_serialize_3_.serialize(p_serialize_1_.getServerDescription()));
            }

            if (p_serialize_1_.getPlayerCountData() != null) {
                jsonobject.add("players", p_serialize_3_.serialize(p_serialize_1_.getPlayerCountData()));
            }

            if (p_serialize_1_.getProtocolVersionInfo() != null) {
                jsonobject.add("version", p_serialize_3_.serialize(p_serialize_1_.getProtocolVersionInfo()));
            }

            if (p_serialize_1_.getFavicon() != null) {
                jsonobject.addProperty("favicon", p_serialize_1_.getFavicon());
            }

            return jsonobject;
        }
    }
}
