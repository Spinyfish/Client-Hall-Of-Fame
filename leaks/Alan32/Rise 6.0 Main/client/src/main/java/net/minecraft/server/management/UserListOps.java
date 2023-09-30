package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

import java.io.File;

public class UserListOps extends UserList<GameProfile, UserListOpsEntry> {
    public UserListOps(final File saveFile) {
        super(saveFile);
    }

    protected UserListEntry<GameProfile> createEntry(final JsonObject entryData) {
        return new UserListOpsEntry(entryData);
    }

    public String[] getKeys() {
        final String[] astring = new String[this.getValues().size()];
        int i = 0;

        for (final UserListOpsEntry userlistopsentry : this.getValues().values()) {
            astring[i++] = userlistopsentry.getValue().getName();
        }

        return astring;
    }

    public boolean func_183026_b(final GameProfile p_183026_1_) {
        final UserListOpsEntry userlistopsentry = this.getEntry(p_183026_1_);
        return userlistopsentry != null && userlistopsentry.func_183024_b();
    }

    /**
     * Gets the key value for the given object
     */
    protected String getObjectKey(final GameProfile obj) {
        return obj.getId().toString();
    }

    /**
     * Gets the GameProfile of based on the provided username.
     *
     * @param username The username to match to a GameProfile
     */
    public GameProfile getGameProfileFromName(final String username) {
        for (final UserListOpsEntry userlistopsentry : this.getValues().values()) {
            if (username.equalsIgnoreCase(userlistopsentry.getValue().getName())) {
                return userlistopsentry.getValue();
            }
        }

        return null;
    }
}
