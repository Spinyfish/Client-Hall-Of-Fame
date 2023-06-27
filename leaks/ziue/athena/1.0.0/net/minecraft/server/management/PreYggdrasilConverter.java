package net.minecraft.server.management;

import java.io.*;
import net.minecraft.server.*;
import com.google.common.base.*;
import net.minecraft.util.*;
import com.mojang.authlib.*;
import net.minecraft.entity.player.*;
import com.google.common.collect.*;
import java.util.*;
import org.apache.logging.log4j.*;

public class PreYggdrasilConverter
{
    private static final Logger LOGGER;
    public static final File OLD_IPBAN_FILE;
    public static final File OLD_PLAYERBAN_FILE;
    public static final File OLD_OPS_FILE;
    public static final File OLD_WHITELIST_FILE;
    
    private static void lookupNames(final MinecraftServer server, final Collection<String> names, final ProfileLookupCallback callback) {
        final String[] astring = (String[])Iterators.toArray((Iterator)Iterators.filter((Iterator)names.iterator(), (Predicate)new Predicate<String>() {
            public boolean apply(final String p_apply_1_) {
                return !StringUtils.isNullOrEmpty(p_apply_1_);
            }
        }), (Class)String.class);
        if (server.isServerInOnlineMode()) {
            server.getGameProfileRepository().findProfilesByNames(astring, Agent.MINECRAFT, callback);
        }
        else {
            for (final String s : astring) {
                final UUID uuid = EntityPlayer.getUUID(new GameProfile((UUID)null, s));
                final GameProfile gameprofile = new GameProfile(uuid, s);
                callback.onProfileLookupSucceeded(gameprofile);
            }
        }
    }
    
    public static String getStringUUIDFromName(final String p_152719_0_) {
        if (StringUtils.isNullOrEmpty(p_152719_0_) || p_152719_0_.length() > 16) {
            return p_152719_0_;
        }
        final MinecraftServer minecraftserver = MinecraftServer.getServer();
        final GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(p_152719_0_);
        if (gameprofile != null && gameprofile.getId() != null) {
            return gameprofile.getId().toString();
        }
        if (!minecraftserver.isSinglePlayer() && minecraftserver.isServerInOnlineMode()) {
            final List<GameProfile> list = (List<GameProfile>)Lists.newArrayList();
            final ProfileLookupCallback profilelookupcallback = (ProfileLookupCallback)new ProfileLookupCallback() {
                public void onProfileLookupSucceeded(final GameProfile p_onProfileLookupSucceeded_1_) {
                    minecraftserver.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                    list.add(p_onProfileLookupSucceeded_1_);
                }
                
                public void onProfileLookupFailed(final GameProfile p_onProfileLookupFailed_1_, final Exception p_onProfileLookupFailed_2_) {
                    PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for " + p_onProfileLookupFailed_1_.getName(), (Throwable)p_onProfileLookupFailed_2_);
                }
            };
            lookupNames(minecraftserver, Lists.newArrayList((Object[])new String[] { p_152719_0_ }), profilelookupcallback);
            return (list.size() > 0 && list.get(0).getId() != null) ? list.get(0).getId().toString() : "";
        }
        return EntityPlayer.getUUID(new GameProfile((UUID)null, p_152719_0_)).toString();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        OLD_IPBAN_FILE = new File("banned-ips.txt");
        OLD_PLAYERBAN_FILE = new File("banned-players.txt");
        OLD_OPS_FILE = new File("ops.txt");
        OLD_WHITELIST_FILE = new File("white-list.txt");
    }
}
