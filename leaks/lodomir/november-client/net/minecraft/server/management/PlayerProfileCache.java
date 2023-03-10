/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.Iterators
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
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.ProfileLookupCallback
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
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
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

public class PlayerProfileCache {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private final Map<String, ProfileEntry> usernameToProfileEntryMap = Maps.newHashMap();
    private final Map<UUID, ProfileEntry> uuidToProfileEntryMap = Maps.newHashMap();
    private final LinkedList<GameProfile> gameProfiles = Lists.newLinkedList();
    private final MinecraftServer mcServer;
    protected final Gson gson;
    private final File usercacheFile;
    private static final ParameterizedType TYPE = new ParameterizedType(){

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{ProfileEntry.class};
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

    public PlayerProfileCache(MinecraftServer server, File cacheFile) {
        this.mcServer = server;
        this.usercacheFile = cacheFile;
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeHierarchyAdapter(ProfileEntry.class, (Object)new Serializer());
        this.gson = gsonbuilder.create();
        this.load();
    }

    private static GameProfile getGameProfile(MinecraftServer server, String username) {
        final GameProfile[] agameprofile = new GameProfile[1];
        ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback(){

            public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                agameprofile[0] = p_onProfileLookupSucceeded_1_;
            }

            public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                agameprofile[0] = null;
            }
        };
        server.getGameProfileRepository().findProfilesByNames(new String[]{username}, Agent.MINECRAFT, profilelookupcallback);
        if (!server.isServerInOnlineMode() && agameprofile[0] == null) {
            UUID uuid = EntityPlayer.getUUID(new GameProfile((UUID)null, username));
            GameProfile gameprofile = new GameProfile(uuid, username);
            profilelookupcallback.onProfileLookupSucceeded(gameprofile);
        }
        return agameprofile[0];
    }

    public void addEntry(GameProfile gameProfile) {
        this.addEntry(gameProfile, null);
    }

    private void addEntry(GameProfile gameProfile, Date expirationDate) {
        UUID uuid = gameProfile.getId();
        if (expirationDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(2, 1);
            expirationDate = calendar.getTime();
        }
        String s = gameProfile.getName().toLowerCase(Locale.ROOT);
        ProfileEntry playerprofilecache$profileentry = new ProfileEntry(gameProfile, expirationDate);
        if (this.uuidToProfileEntryMap.containsKey(uuid)) {
            ProfileEntry playerprofilecache$profileentry1 = this.uuidToProfileEntryMap.get(uuid);
            this.usernameToProfileEntryMap.remove(playerprofilecache$profileentry1.getGameProfile().getName().toLowerCase(Locale.ROOT));
            this.gameProfiles.remove(gameProfile);
        }
        this.usernameToProfileEntryMap.put(gameProfile.getName().toLowerCase(Locale.ROOT), playerprofilecache$profileentry);
        this.uuidToProfileEntryMap.put(uuid, playerprofilecache$profileentry);
        this.gameProfiles.addFirst(gameProfile);
        this.save();
    }

    public GameProfile getGameProfileForUsername(String username) {
        String s = username.toLowerCase(Locale.ROOT);
        ProfileEntry playerprofilecache$profileentry = this.usernameToProfileEntryMap.get(s);
        if (playerprofilecache$profileentry != null && new Date().getTime() >= playerprofilecache$profileentry.expirationDate.getTime()) {
            this.uuidToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getId());
            this.usernameToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getName().toLowerCase(Locale.ROOT));
            this.gameProfiles.remove(playerprofilecache$profileentry.getGameProfile());
            playerprofilecache$profileentry = null;
        }
        if (playerprofilecache$profileentry != null) {
            GameProfile gameprofile = playerprofilecache$profileentry.getGameProfile();
            this.gameProfiles.remove(gameprofile);
            this.gameProfiles.addFirst(gameprofile);
        } else {
            GameProfile gameprofile1 = PlayerProfileCache.getGameProfile(this.mcServer, s);
            if (gameprofile1 != null) {
                this.addEntry(gameprofile1);
                playerprofilecache$profileentry = this.usernameToProfileEntryMap.get(s);
            }
        }
        this.save();
        return playerprofilecache$profileentry == null ? null : playerprofilecache$profileentry.getGameProfile();
    }

    public String[] getUsernames() {
        ArrayList list = Lists.newArrayList(this.usernameToProfileEntryMap.keySet());
        return list.toArray(new String[list.size()]);
    }

    public GameProfile getProfileByUUID(UUID uuid) {
        ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uuid);
        return playerprofilecache$profileentry == null ? null : playerprofilecache$profileentry.getGameProfile();
    }

    private ProfileEntry getByUUID(UUID uuid) {
        ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uuid);
        if (playerprofilecache$profileentry != null) {
            GameProfile gameprofile = playerprofilecache$profileentry.getGameProfile();
            this.gameProfiles.remove(gameprofile);
            this.gameProfiles.addFirst(gameprofile);
        }
        return playerprofilecache$profileentry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load() {
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = Files.newReader((File)this.usercacheFile, (Charset)Charsets.UTF_8);
            List list = (List)this.gson.fromJson((Reader)bufferedreader, (Type)TYPE);
            this.usernameToProfileEntryMap.clear();
            this.uuidToProfileEntryMap.clear();
            this.gameProfiles.clear();
            for (ProfileEntry playerprofilecache$profileentry : Lists.reverse((List)list)) {
                if (playerprofilecache$profileentry == null) continue;
                this.addEntry(playerprofilecache$profileentry.getGameProfile(), playerprofilecache$profileentry.getExpirationDate());
            }
        }
        catch (FileNotFoundException fileNotFoundException) {
            IOUtils.closeQuietly(bufferedreader);
        }
        catch (JsonParseException jsonParseException) {
            IOUtils.closeQuietly(bufferedreader);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(bufferedreader);
            throw throwable;
        }
        IOUtils.closeQuietly((Reader)bufferedreader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save() {
        String s = this.gson.toJson(this.getEntriesWithLimit(1000));
        BufferedWriter bufferedwriter = null;
        try {
            bufferedwriter = Files.newWriter((File)this.usercacheFile, (Charset)Charsets.UTF_8);
            bufferedwriter.write(s);
        }
        catch (FileNotFoundException fileNotFoundException) {
            IOUtils.closeQuietly(bufferedwriter);
        }
        catch (IOException var9) {
            IOUtils.closeQuietly(bufferedwriter);
            return;
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(bufferedwriter);
            throw throwable;
        }
        IOUtils.closeQuietly((Writer)bufferedwriter);
        return;
    }

    private List<ProfileEntry> getEntriesWithLimit(int limitSize) {
        ArrayList arraylist = Lists.newArrayList();
        for (GameProfile gameprofile : Lists.newArrayList((Iterator)Iterators.limit(this.gameProfiles.iterator(), (int)limitSize))) {
            ProfileEntry playerprofilecache$profileentry = this.getByUUID(gameprofile.getId());
            if (playerprofilecache$profileentry == null) continue;
            arraylist.add(playerprofilecache$profileentry);
        }
        return arraylist;
    }

    class Serializer
    implements JsonDeserializer<ProfileEntry>,
    JsonSerializer<ProfileEntry> {
        private Serializer() {
        }

        public JsonElement serialize(ProfileEntry p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("name", p_serialize_1_.getGameProfile().getName());
            UUID uuid = p_serialize_1_.getGameProfile().getId();
            jsonobject.addProperty("uuid", uuid == null ? "" : uuid.toString());
            jsonobject.addProperty("expiresOn", dateFormat.format(p_serialize_1_.getExpirationDate()));
            return jsonobject;
        }

        public ProfileEntry deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            if (p_deserialize_1_.isJsonObject()) {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                JsonElement jsonelement = jsonobject.get("name");
                JsonElement jsonelement1 = jsonobject.get("uuid");
                JsonElement jsonelement2 = jsonobject.get("expiresOn");
                if (jsonelement != null && jsonelement1 != null) {
                    String s = jsonelement1.getAsString();
                    String s1 = jsonelement.getAsString();
                    Date date = null;
                    if (jsonelement2 != null) {
                        try {
                            date = dateFormat.parse(jsonelement2.getAsString());
                        }
                        catch (ParseException var14) {
                            date = null;
                        }
                    }
                    if (s1 != null && s != null) {
                        UUID uuid;
                        try {
                            uuid = UUID.fromString(s);
                        }
                        catch (Throwable var13) {
                            return null;
                        }
                        PlayerProfileCache playerProfileCache = PlayerProfileCache.this;
                        playerProfileCache.getClass();
                        ProfileEntry playerprofilecache$profileentry = playerProfileCache.new ProfileEntry(new GameProfile(uuid, s1), date);
                        return playerprofilecache$profileentry;
                    }
                    return null;
                }
                return null;
            }
            return null;
        }
    }

    class ProfileEntry {
        private final GameProfile gameProfile;
        private final Date expirationDate;

        private ProfileEntry(GameProfile gameProfileIn, Date expirationDateIn) {
            this.gameProfile = gameProfileIn;
            this.expirationDate = expirationDateIn;
        }

        public GameProfile getGameProfile() {
            return this.gameProfile;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }
    }
}

