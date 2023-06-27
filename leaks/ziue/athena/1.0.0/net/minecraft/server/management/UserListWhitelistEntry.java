package net.minecraft.server.management;

import com.mojang.authlib.*;
import com.google.gson.*;
import java.util.*;

public class UserListWhitelistEntry extends UserListEntry<GameProfile>
{
    public UserListWhitelistEntry(final GameProfile profile) {
        super(profile);
    }
    
    public UserListWhitelistEntry(final JsonObject p_i1130_1_) {
        super(gameProfileFromJsonObject(p_i1130_1_), p_i1130_1_);
    }
    
    @Override
    protected void onSerialization(final JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("uuid", (this.getValue().getId() == null) ? "" : this.getValue().getId().toString());
            data.addProperty("name", this.getValue().getName());
            super.onSerialization(data);
        }
    }
    
    private static GameProfile gameProfileFromJsonObject(final JsonObject p_152646_0_) {
        if (p_152646_0_.has("uuid") && p_152646_0_.has("name")) {
            final String s = p_152646_0_.get("uuid").getAsString();
            UUID uuid;
            try {
                uuid = UUID.fromString(s);
            }
            catch (Throwable var4) {
                return null;
            }
            return new GameProfile(uuid, p_152646_0_.get("name").getAsString());
        }
        return null;
    }
}
