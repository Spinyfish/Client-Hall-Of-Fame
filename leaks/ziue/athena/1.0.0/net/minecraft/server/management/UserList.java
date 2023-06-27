package net.minecraft.server.management;

import com.google.common.collect.*;
import com.google.common.base.*;
import com.google.common.io.*;
import org.apache.commons.io.*;
import java.util.*;
import java.io.*;
import org.apache.logging.log4j.*;
import java.lang.reflect.*;
import com.google.gson.*;

public class UserList<K, V extends UserListEntry<K>>
{
    protected static final Logger logger;
    protected final Gson gson;
    private final File saveFile;
    private final Map<String, V> values;
    private boolean lanServer;
    private static final ParameterizedType saveFileFormat;
    
    public UserList(final File saveFile) {
        this.values = (Map<String, V>)Maps.newHashMap();
        this.lanServer = true;
        this.saveFile = saveFile;
        final GsonBuilder gsonbuilder = new GsonBuilder().setPrettyPrinting();
        gsonbuilder.registerTypeHierarchyAdapter((Class)UserListEntry.class, (Object)new Serializer());
        this.gson = gsonbuilder.create();
    }
    
    public boolean isLanServer() {
        return this.lanServer;
    }
    
    public void setLanServer(final boolean state) {
        this.lanServer = state;
    }
    
    public void addEntry(final V entry) {
        this.values.put(this.getObjectKey(entry.getValue()), entry);
        try {
            this.writeChanges();
        }
        catch (IOException ioexception) {
            UserList.logger.warn("Could not save the list after adding a user.", (Throwable)ioexception);
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
        }
        catch (IOException ioexception) {
            UserList.logger.warn("Could not save the list after removing a user.", (Throwable)ioexception);
        }
    }
    
    public String[] getKeys() {
        return this.values.keySet().toArray(new String[this.values.size()]);
    }
    
    protected String getObjectKey(final K obj) {
        return obj.toString();
    }
    
    protected boolean hasEntry(final K entry) {
        return this.values.containsKey(this.getObjectKey(entry));
    }
    
    private void removeExpired() {
        final List<K> list = (List<K>)Lists.newArrayList();
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
        return new UserListEntry<K>(null, entryData);
    }
    
    protected Map<String, V> getValues() {
        return this.values;
    }
    
    public void writeChanges() throws IOException {
        final Collection<V> collection = this.values.values();
        final String s = this.gson.toJson((Object)collection);
        BufferedWriter bufferedwriter = null;
        try {
            bufferedwriter = Files.newWriter(this.saveFile, Charsets.UTF_8);
            bufferedwriter.write(s);
        }
        finally {
            IOUtils.closeQuietly((Writer)bufferedwriter);
        }
    }
    
    static {
        logger = LogManager.getLogger();
        saveFileFormat = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { UserListEntry.class };
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
    }
    
    class Serializer implements JsonDeserializer<UserListEntry<K>>, JsonSerializer<UserListEntry<K>>
    {
        private Serializer() {
        }
        
        public JsonElement serialize(final UserListEntry<K> p_serialize_1_, final Type p_serialize_2_, final JsonSerializationContext p_serialize_3_) {
            final JsonObject jsonobject = new JsonObject();
            p_serialize_1_.onSerialization(jsonobject);
            return (JsonElement)jsonobject;
        }
        
        public UserListEntry<K> deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            if (p_deserialize_1_.isJsonObject()) {
                final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                final UserListEntry<K> userlistentry = UserList.this.createEntry(jsonobject);
                return userlistentry;
            }
            return null;
        }
    }
}
