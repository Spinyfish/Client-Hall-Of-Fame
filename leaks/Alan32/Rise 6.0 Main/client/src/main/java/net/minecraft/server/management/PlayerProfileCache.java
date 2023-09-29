package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.*;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerProfileCache {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private final Map<String, PlayerProfileCache.ProfileEntry> usernameToProfileEntryMap = Maps.newHashMap();
    private final Map<UUID, PlayerProfileCache.ProfileEntry> uuidToProfileEntryMap = Maps.newHashMap();
    private final LinkedList<GameProfile> gameProfiles = Lists.newLinkedList();
    private final MinecraftServer mcServer;
    protected final Gson gson;
    private final File usercacheFile;
    private static final ParameterizedType TYPE = new ParameterizedType() {
        public Type[] getActualTypeArguments() {
            return new Type[]{PlayerProfileCache.ProfileEntry.class};
        }

        public Type getRawType() {
            return List.class;
        }

        public Type getOwnerType() {
            return null;
        }
    };

    public PlayerProfileCache(final MinecraftServer server, final File cacheFile) {
        this.mcServer = server;
        this.usercacheFile = cacheFile;
        final GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeHierarchyAdapter(PlayerProfileCache.ProfileEntry.class, new PlayerProfileCache.Serializer());
        this.gson = gsonbuilder.create();
        this.load();
    }

    /**
     * Get a GameProfile given the MinecraftServer and the player's username.
     * <p>
     * The UUID of the GameProfile will <b>not</b> be null. If the server is offline, a UUID based on the hash of the
     * username will be used.
     *
     * @param server   The Minecraft Server
     * @param username The player's username
     */
    private static GameProfile getGameProfile(final MinecraftServer server, final String username) {
        final GameProfile[] agameprofile = new GameProfile[1];
        final ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
            public void onProfileLookupSucceeded(final GameProfile p_onProfileLookupSucceeded_1_) {
                agameprofile[0] = p_onProfileLookupSucceeded_1_;
            }

            public void onProfileLookupFailed(final GameProfile p_onProfileLookupFailed_1_, final Exception p_onProfileLookupFailed_2_) {
                agameprofile[0] = null;
            }
        };
        server.getGameProfileRepository().findProfilesByNames(new String[]{username}, Agent.MINECRAFT, profilelookupcallback);

        if (!server.isServerInOnlineMode() && agameprofile[0] == null) {
            final UUID uuid = EntityPlayer.getUUID(new GameProfile(null, username));
            final GameProfile gameprofile = new GameProfile(uuid, username);
            profilelookupcallback.onProfileLookupSucceeded(gameprofile);
        }

        return agameprofile[0];
    }

    /**
     * Add an entry to this cache
     *
     * @param gameProfile The entry's {@link GameProfile}
     */
    public void addEntry(final GameProfile gameProfile) {
        this.addEntry(gameProfile, null);
    }

    /**
     * Add an entry to this cache
     *
     * @param gameProfile    The entry's {@link GameProfile}
     * @param expirationDate The expiration date for this entry. {@code null} is allowed, 1 month will be used in this
     *                       case.
     */
    private void addEntry(final GameProfile gameProfile, Date expirationDate) {
        final UUID uuid = gameProfile.getId();

        if (expirationDate == null) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(2, 1);
            expirationDate = calendar.getTime();
        }

        final String s = gameProfile.getName().toLowerCase(Locale.ROOT);
        final PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = new PlayerProfileCache.ProfileEntry(gameProfile, expirationDate);

        if (this.uuidToProfileEntryMap.containsKey(uuid)) {
            final PlayerProfileCache.ProfileEntry playerprofilecache$profileentry1 = this.uuidToProfileEntryMap.get(uuid);
            this.usernameToProfileEntryMap.remove(playerprofilecache$profileentry1.getGameProfile().getName().toLowerCase(Locale.ROOT));
            this.gameProfiles.remove(gameProfile);
        }

        this.usernameToProfileEntryMap.put(gameProfile.getName().toLowerCase(Locale.ROOT), playerprofilecache$profileentry);
        this.uuidToProfileEntryMap.put(uuid, playerprofilecache$profileentry);
        this.gameProfiles.addFirst(gameProfile);
        this.save();
    }

    /**
     * Get a player's GameProfile given their username. Mojang's server's will be contacted if the entry is not cached
     * locally.
     *
     * @param username The player's username
     */
    public GameProfile getGameProfileForUsername(final String username) {
        final String s = username.toLowerCase(Locale.ROOT);
        PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.usernameToProfileEntryMap.get(s);

        if (playerprofilecache$profileentry != null && (new Date()).getTime() >= playerprofilecache$profileentry.expirationDate.getTime()) {
            this.uuidToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getId());
            this.usernameToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getName().toLowerCase(Locale.ROOT));
            this.gameProfiles.remove(playerprofilecache$profileentry.getGameProfile());
            playerprofilecache$profileentry = null;
        }

        if (playerprofilecache$profileentry != null) {
            final GameProfile gameprofile = playerprofilecache$profileentry.getGameProfile();
            this.gameProfiles.remove(gameprofile);
            this.gameProfiles.addFirst(gameprofile);
        } else {
            final GameProfile gameprofile1 = getGameProfile(this.mcServer, s);

            if (gameprofile1 != null) {
                this.addEntry(gameprofile1);
                playerprofilecache$profileentry = this.usernameToProfileEntryMap.get(s);
            }
        }

        this.save();
        return playerprofilecache$profileentry == null ? null : playerprofilecache$profileentry.getGameProfile();
    }

    /**
     * Get an array of the usernames that are cached in this cache
     */
    public String[] getUsernames() {
        final List<String> list = Lists.newArrayList(this.usernameToProfileEntryMap.keySet());
        return list.toArray(new String[list.size()]);
    }

    /**
     * Get a player's {@link GameProfile} given their UUID
     *
     * @param uuid The player's UUID
     */
    public GameProfile getProfileByUUID(final UUID uuid) {
        final PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uuid);
        return playerprofilecache$profileentry == null ? null : playerprofilecache$profileentry.getGameProfile();
    }

    /**
     * Get a {@link ProfileEntry} by UUID
     *
     * @param uuid The UUID
     */
    private PlayerProfileCache.ProfileEntry getByUUID(final UUID uuid) {
        final PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uuid);

        if (playerprofilecache$profileentry != null) {
            final GameProfile gameprofile = playerprofilecache$profileentry.getGameProfile();
            this.gameProfiles.remove(gameprofile);
            this.gameProfiles.addFirst(gameprofile);
        }

        return playerprofilecache$profileentry;
    }

    /**
     * Load the cached profiles from disk
     */
    public void load() {
        BufferedReader bufferedreader = null;

        try {
            bufferedreader = Files.newReader(this.usercacheFile, Charsets.UTF_8);
            final List<PlayerProfileCache.ProfileEntry> list = this.gson.fromJson(bufferedreader, TYPE);
            this.usernameToProfileEntryMap.clear();
            this.uuidToProfileEntryMap.clear();
            this.gameProfiles.clear();

            if (list != null) {
                for (final PlayerProfileCache.ProfileEntry playerprofilecache$profileentry : Lists.reverse(list)) {
                    if (playerprofilecache$profileentry != null) {
                        this.addEntry(playerprofilecache$profileentry.getGameProfile(), playerprofilecache$profileentry.getExpirationDate());
                    }
                }
            }
        } catch (final FileNotFoundException var9) {
        } catch (final JsonParseException var10) {
        } finally {
            IOUtils.closeQuietly(bufferedreader);
        }
    }

    /**
     * Save the cached profiles to disk
     */
    public void save() {
        final String s = this.gson.toJson(this.getEntriesWithLimit(1000));
        BufferedWriter bufferedwriter = null;

        try {
            bufferedwriter = Files.newWriter(this.usercacheFile, Charsets.UTF_8);
            bufferedwriter.write(s);
            return;
        } catch (final FileNotFoundException var8) {
        } catch (final IOException var9) {
            return;
        } finally {
            IOUtils.closeQuietly(bufferedwriter);
        }
    }

    private List<PlayerProfileCache.ProfileEntry> getEntriesWithLimit(final int limitSize) {
        final ArrayList<PlayerProfileCache.ProfileEntry> arraylist = Lists.newArrayList();

        for (final GameProfile gameprofile : Lists.newArrayList(Iterators.limit(this.gameProfiles.iterator(), limitSize))) {
            final PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.getByUUID(gameprofile.getId());

            if (playerprofilecache$profileentry != null) {
                arraylist.add(playerprofilecache$profileentry);
            }
        }

        return arraylist;
    }

    class ProfileEntry {
        private final GameProfile gameProfile;
        private final Date expirationDate;

        private ProfileEntry(final GameProfile gameProfileIn, final Date expirationDateIn) {
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

    class Serializer implements JsonDeserializer<PlayerProfileCache.ProfileEntry>, JsonSerializer<PlayerProfileCache.ProfileEntry> {
        private Serializer() {
        }

        public JsonElement serialize(final PlayerProfileCache.ProfileEntry p_serialize_1_, final Type p_serialize_2_, final JsonSerializationContext p_serialize_3_) {
            final JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("name", p_serialize_1_.getGameProfile().getName());
            final UUID uuid = p_serialize_1_.getGameProfile().getId();
            jsonobject.addProperty("uuid", uuid == null ? "" : uuid.toString());
            jsonobject.addProperty("expiresOn", PlayerProfileCache.dateFormat.format(p_serialize_1_.getExpirationDate()));
            return jsonobject;
        }

        public PlayerProfileCache.ProfileEntry deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            if (p_deserialize_1_.isJsonObject()) {
                final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                final JsonElement jsonelement = jsonobject.get("name");
                final JsonElement jsonelement1 = jsonobject.get("uuid");
                final JsonElement jsonelement2 = jsonobject.get("expiresOn");

                if (jsonelement != null && jsonelement1 != null) {
                    final String s = jsonelement1.getAsString();
                    final String s1 = jsonelement.getAsString();
                    Date date = null;

                    if (jsonelement2 != null) {
                        try {
                            date = PlayerProfileCache.dateFormat.parse(jsonelement2.getAsString());
                        } catch (final ParseException var14) {
                            date = null;
                        }
                    }

                    if (s1 != null && s != null) {
                        final UUID uuid;

                        try {
                            uuid = UUID.fromString(s);
                        } catch (final Throwable var13) {
                            return null;
                        }

                        final PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = PlayerProfileCache.this.new ProfileEntry(new GameProfile(uuid, s1), date);
                        return playerprofilecache$profileentry;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}
