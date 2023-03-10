/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.util.ChatColorUtil;
import de.gerrygames.viarewind.utils.ChatUtil;
import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class GameProfileStorage
extends StoredObject {
    private Map<UUID, GameProfile> properties = new HashMap<UUID, GameProfile>();

    public GameProfileStorage(UserConnection user) {
        super(user);
    }

    public GameProfile put(UUID uuid, String name) {
        GameProfile gameProfile = new GameProfile(uuid, name);
        this.properties.put(uuid, gameProfile);
        return gameProfile;
    }

    public void putProperty(UUID uuid, Property property) {
        this.properties.computeIfAbsent((UUID)uuid, (Function<UUID, GameProfile>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$putProperty$0(java.util.UUID java.util.UUID ), (Ljava/util/UUID;)Lde/gerrygames/viarewind/protocol/protocol1_7_6_10to1_8/storage/GameProfileStorage$GameProfile;)((UUID)uuid)).properties.add(property);
    }

    public void putProperty(UUID uuid, String name, String value, String signature) {
        this.putProperty(uuid, new Property(name, value, signature));
    }

    public GameProfile get(UUID uuid) {
        return this.properties.get(uuid);
    }

    public GameProfile get(String name, boolean ignoreCase) {
        if (ignoreCase) {
            name = name.toLowerCase();
        }
        for (GameProfile profile : this.properties.values()) {
            String n;
            if (profile.name == null || !(n = ignoreCase ? profile.name.toLowerCase() : profile.name).equals(name)) continue;
            return profile;
        }
        return null;
    }

    public List<GameProfile> getAllWithPrefix(String prefix, boolean ignoreCase) {
        if (ignoreCase) {
            prefix = prefix.toLowerCase();
        }
        ArrayList<GameProfile> profiles = new ArrayList<GameProfile>();
        for (GameProfile profile : this.properties.values()) {
            String n;
            if (profile.name == null || !(n = ignoreCase ? profile.name.toLowerCase() : profile.name).startsWith(prefix)) continue;
            profiles.add(profile);
        }
        return profiles;
    }

    public GameProfile remove(UUID uuid) {
        return this.properties.remove(uuid);
    }

    private static /* synthetic */ GameProfile lambda$putProperty$0(UUID uuid, UUID profile) {
        return new GameProfile(uuid, null);
    }

    public static class Property {
        public String name;
        public String value;
        public String signature;

        public Property(String name, String value, String signature) {
            this.name = name;
            this.value = value;
            this.signature = signature;
        }
    }

    public static class GameProfile {
        public String name;
        public String displayName;
        public int ping;
        public UUID uuid;
        public List<Property> properties = new ArrayList<Property>();
        public int gamemode = 0;

        public GameProfile(UUID uuid, String name) {
            this.name = name;
            this.uuid = uuid;
        }

        public Item getSkull() {
            CompoundTag tag = new CompoundTag();
            CompoundTag ownerTag = new CompoundTag();
            tag.put("SkullOwner", ownerTag);
            ownerTag.put("Id", new StringTag(this.uuid.toString()));
            CompoundTag properties = new CompoundTag();
            ownerTag.put("Properties", properties);
            ListTag textures = new ListTag(CompoundTag.class);
            properties.put("textures", textures);
            for (Property property : this.properties) {
                if (!property.name.equals("textures")) continue;
                CompoundTag textureTag = new CompoundTag();
                textureTag.put("Value", new StringTag(property.value));
                if (property.signature != null) {
                    textureTag.put("Signature", new StringTag(property.signature));
                }
                textures.add(textureTag);
            }
            return new DataItem(397, 1, 3, tag);
        }

        public String getDisplayName() {
            String displayName;
            String string = displayName = this.displayName == null ? this.name : this.displayName;
            if (displayName.length() > 16) {
                displayName = ChatUtil.removeUnusedColor(displayName, 'f');
            }
            if (displayName.length() > 16) {
                displayName = ChatColorUtil.stripColor(displayName);
            }
            if (displayName.length() > 16) {
                displayName = displayName.substring(0, 16);
            }
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }
}

