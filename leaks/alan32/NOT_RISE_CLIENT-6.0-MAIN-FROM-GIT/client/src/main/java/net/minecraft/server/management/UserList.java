package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserList<K, V extends UserListEntry<K>> {
    protected static final Logger logger = LogManager.getLogger();
    protected final Gson gson;
    private final File saveFile;
    private final Map<String, V> values = Maps.newHashMap();
    private boolean lanServer = true;
    private static final ParameterizedType saveFileFormat = new ParameterizedType() {
        public Type[] getActualTypeArguments() {
            return new Type[]{UserListEntry.class};
        }

        public Type getRawType() {
            return List.class;
        }

        public Type getOwnerType() {
            return null;
        }
    };

    public UserList(final File saveFile) {
        this.saveFile = saveFile;
        final GsonBuilder gsonbuilder = (new GsonBuilder()).setPrettyPrinting();
        gsonbuilder.registerTypeHierarchyAdapter(UserListEntry.class, new UserList.Serializer());
        this.gson = gsonbuilder.create();
    }

    public boolean isLanServer() {
        return this.lanServer;
    }

    public void setLanServer(final boolean state) {
        this.lanServer = state;
    }

    /**
     * Adds an entry to the list
     */
    public void addEntry(final V entry) {
        this.values.put(this.getObjectKey(entry.getValue()), entry);

        try {
            this.writeChanges();
        } catch (final IOException ioexception) {
            logger.warn("Could not save the list after adding a user.", ioexception);
        }
    }

    public V getEntry(final K obj) {
        this.removeExpired();
        return this.values.get(this.getObjectKey(obj));
    }

    public void removeEntry(final K p_152684_1_) {
        this.values.remove(this.getObjectKey(p_152684_1_));

        try {
            this.writeChanges();
        } catch (final IOException ioexception) {
            logger.warn("Could not save the list after removing a user.", ioexception);
        }
    }

    public String[] getKeys() {
        return this.values.keySet().toArray(new String[this.values.size()]);
    }

    /**
     * Gets the key value for the given object
     */
    protected String getObjectKey(final K obj) {
        return obj.toString();
    }

    protected boolean hasEntry(final K entry) {
        return this.values.containsKey(this.getObjectKey(entry));
    }

    /**
     * Removes expired bans from the list. See {@link BanEntry#hasBanExpired}
     */
    private void removeExpired() {
        final List<K> list = Lists.newArrayList();

        for (final V v : this.values.values()) {
            if (v.hasBanExpired()) {
                list.add(v.getValue());
            }
        }

        for (final K k : list) {
            this.values.remove(k);
        }
    }

    protected UserListEntry<K> createEntry(final JsonObject entryData) {
        return new UserListEntry(null, entryData);
    }

    protected Map<String, V> getValues() {
        return this.values;
    }

    public void writeChanges() throws IOException {
        final Collection<V> collection = this.values.values();
        final String s = this.gson.toJson(collection);
        BufferedWriter bufferedwriter = null;

        try {
            bufferedwriter = Files.newWriter(this.saveFile, Charsets.UTF_8);
            bufferedwriter.write(s);
        } finally {
            IOUtils.closeQuietly(bufferedwriter);
        }
    }

    class Serializer implements JsonDeserializer<UserListEntry<K>>, JsonSerializer<UserListEntry<K>> {
        private Serializer() {
        }

        public JsonElement serialize(final UserListEntry<K> p_serialize_1_, final Type p_serialize_2_, final JsonSerializationContext p_serialize_3_) {
            final JsonObject jsonobject = new JsonObject();
            p_serialize_1_.onSerialization(jsonobject);
            return jsonobject;
        }

        public UserListEntry<K> deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            if (p_deserialize_1_.isJsonObject()) {
                final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                final UserListEntry<K> userlistentry = UserList.this.createEntry(jsonobject);
                return userlistentry;
            } else {
                return null;
            }
        }
    }
}
