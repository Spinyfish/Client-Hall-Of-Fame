/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.server.management.UserListEntry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserList<K, V extends UserListEntry<K>> {
    protected static final Logger logger = LogManager.getLogger();
    protected final Gson gson;
    private final File saveFile;
    private final Map<String, V> values = Maps.newHashMap();
    private boolean lanServer = true;
    private static final ParameterizedType saveFileFormat = new ParameterizedType(){

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{UserListEntry.class};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };

    public UserList(File saveFile) {
        this.saveFile = saveFile;
        GsonBuilder gsonbuilder = new GsonBuilder().setPrettyPrinting();
        gsonbuilder.registerTypeHierarchyAdapter(UserListEntry.class, (Object)new Serializer());
        this.gson = gsonbuilder.create();
    }

    public boolean isLanServer() {
        return this.lanServer;
    }

    public void setLanServer(boolean state) {
        this.lanServer = state;
    }

    public void addEntry(V entry) {
        this.values.put(this.getObjectKey(((UserListEntry)entry).getValue()), entry);
        try {
            this.writeChanges();
        }
        catch (IOException ioexception) {
            logger.warn("Could not save the list after adding a user.", (Throwable)ioexception);
        }
    }

    public V getEntry(K obj) {
        this.removeExpired();
        return (V)((UserListEntry)this.values.get(this.getObjectKey(obj)));
    }

    public void removeEntry(K p_152684_1_) {
        this.values.remove(this.getObjectKey(p_152684_1_));
        try {
            this.writeChanges();
        }
        catch (IOException ioexception) {
            logger.warn("Could not save the list after removing a user.", (Throwable)ioexception);
        }
    }

    public String[] getKeys() {
        return this.values.keySet().toArray(new String[this.values.size()]);
    }

    protected String getObjectKey(K obj) {
        return obj.toString();
    }

    protected boolean hasEntry(K entry) {
        return this.values.containsKey(this.getObjectKey(entry));
    }

    private void removeExpired() {
        ArrayList list = Lists.newArrayList();
        for (UserListEntry v : this.values.values()) {
            if (!v.hasBanExpired()) continue;
            list.add(v.getValue());
        }
        for (Object k : list) {
            this.values.remove(k);
        }
    }

    protected UserListEntry<K> createEntry(JsonObject entryData) {
        return new UserListEntry<Object>(null, entryData);
    }

    protected Map<String, V> getValues() {
        return this.values;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeChanges() throws IOException {
        Collection<V> collection = this.values.values();
        String s = this.gson.toJson(collection);
        BufferedWriter bufferedwriter = null;
        try {
            bufferedwriter = Files.newWriter((File)this.saveFile, (Charset)Charsets.UTF_8);
            bufferedwriter.write(s);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(bufferedwriter);
            throw throwable;
        }
        IOUtils.closeQuietly((Writer)bufferedwriter);
    }

    class Serializer
    implements JsonDeserializer<UserListEntry<K>>,
    JsonSerializer<UserListEntry<K>> {
        private Serializer() {
        }

        public JsonElement serialize(UserListEntry<K> p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
            JsonObject jsonobject = new JsonObject();
            p_serialize_1_.onSerialization(jsonobject);
            return jsonobject;
        }

        public UserListEntry<K> deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            if (p_deserialize_1_.isJsonObject()) {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                UserListEntry userlistentry = UserList.this.createEntry(jsonobject);
                return userlistentry;
            }
            return null;
        }
    }
}

